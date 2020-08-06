package com.xyz.client.feign.interceptor;

import com.xyz.client.HttpClientUtils;
import com.xyz.client.OAuth1HttpClient;
import com.xyz.client.config.ClientCredentialConfig;
import feign.Client;
import feign.Request;
import feign.Response;
import feign.Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * OAuth1 client
 *
 * @author sxl
 */
@Slf4j
public class OAuth1FeignClient implements Client {
    private static final String ACCEPT_HEADER_NAME = "Accept";

    private static final int DEFAULT_CONNECT_TIMEOUT = 6000;
    private static final int DEFAULT_READ_TIMEOUT = 30000;

    private final CloseableHttpClient httpClient;
    private final ClientCredentialConfig clientCredentialConfig;

    public OAuth1FeignClient(CloseableHttpClient httpClient, ClientCredentialConfig clientCredentialConfig) {
        this.httpClient = httpClient;
        this.clientCredentialConfig = clientCredentialConfig;
    }

    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        try {
            HttpUriRequest httpRequest = toHttpUriRequest(request);

            ClientCredentialConfig.OAuthConfig oauthKey = clientCredentialConfig.findOAuthConfigByUrl(request.url());
            OAuth1HttpClient oAuth1HttpClient = OAuth1HttpClient.getOrCreate(httpClient, oauthKey.getKey(), oauthKey.getSecret());
            oAuth1HttpClient.sign(httpRequest);

            CloseableHttpResponse httpResponse = httpClient.execute(httpRequest);
            return toFeignResponse(httpResponse, request);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    private HttpUriRequest toHttpUriRequest(Request request)
            throws URISyntaxException {
        RequestBuilder requestBuilder = RequestBuilder.create(request.httpMethod().name());
        requestBuilder.setConfig(HttpClientUtils.buildRequestConfig(DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT));

        URI uri = new URIBuilder(request.url()).build();

        requestBuilder.setUri(uri.getScheme() + "://" + uri.getAuthority() + uri.getRawPath());

        // request query params
        List<NameValuePair> queryParams = URLEncodedUtils.parse(uri, requestBuilder.getCharset());
        for (NameValuePair queryParam : queryParams) {
            requestBuilder.addParameter(queryParam);
        }

        // request headers
        boolean hasAcceptHeader = false;
        for (Map.Entry<String, Collection<String>> headerEntry : request.headers().entrySet()) {
            String headerName = headerEntry.getKey();
            if (headerName.equalsIgnoreCase(ACCEPT_HEADER_NAME)) {
                hasAcceptHeader = true;
            }

            if (headerName.equalsIgnoreCase(Util.CONTENT_LENGTH)) {
                // The 'Content-Length' header is always set by the Apache client and it
                // doesn't like us to set it as well.
                continue;
            }

            for (String headerValue : headerEntry.getValue()) {
                requestBuilder.addHeader(headerName, headerValue);
            }
        }
        // some servers choke on the default accept string, so we'll set it to anything
        if (!hasAcceptHeader) {
            requestBuilder.addHeader(ACCEPT_HEADER_NAME, "*/*");
        }

        // request body
        if (request.body() != null) {
            HttpEntity entity;
            if (request.charset() != null) {
                ContentType contentType = getContentType(request);
                String content = request.requestTemplate().requestBody().asString();
                entity = new StringEntity(content, contentType);
            } else {
                entity = new ByteArrayEntity(request.body());
            }
            requestBuilder.setEntity(entity);
        } else {
            requestBuilder.setEntity(new ByteArrayEntity(new byte[0]));
        }

        return requestBuilder.build();
    }

    private ContentType getContentType(Request request) {
        ContentType contentType = null;
        for (Map.Entry<String, Collection<String>> entry : request.headers().entrySet()) {
            if ("Content-Type".equalsIgnoreCase(entry.getKey())) {
                Collection<String> values = entry.getValue();
                if (values != null && !values.isEmpty()) {
                    contentType = ContentType.parse(values.iterator().next());
                    if (contentType.getCharset() == null) {
                        contentType = contentType.withCharset(request.charset());
                    }
                    break;
                }
            }
        }
        return contentType;
    }

    private Response toFeignResponse(HttpResponse httpResponse, Request request) {
        StatusLine statusLine = httpResponse.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        String reason = statusLine.getReasonPhrase();
        Map<String, Collection<String>> headers = new HashMap<>();
        Header[] allHeaders = httpResponse.getAllHeaders();
        for (Header header : allHeaders) {
            String name = header.getName();
            String value = header.getValue();
            Collection<String> headerValues = headers.computeIfAbsent(name, k -> new ArrayList<>());
            headerValues.add(value);
        }

        return Response.builder().status(statusCode).reason(reason).headers(headers).request(request).body(this.toFeignBody(httpResponse)).build();
    }

    private Response.Body toFeignBody(HttpResponse httpResponse) {
        final HttpEntity entity = httpResponse.getEntity();
        return entity == null ? null : new Response.Body() {
            @Override
            public Integer length() {
                return entity.getContentLength() >= 0L && entity.getContentLength() <= 2147483647L ? (int) entity.getContentLength() : null;
            }

            @Override
            public boolean isRepeatable() {
                return entity.isRepeatable();
            }

            @Override
            public InputStream asInputStream() throws IOException {
                return entity.getContent();
            }

            @Override
            public Reader asReader(Charset charset) throws IOException {
                Util.checkNotNull(charset, "charset should not be null");
                return new InputStreamReader(this.asInputStream(), charset);
            }

            @Override
            public void close() throws IOException {
                EntityUtils.consume(entity);
            }
        };
    }
}