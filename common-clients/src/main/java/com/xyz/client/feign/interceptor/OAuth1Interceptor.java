package com.xyz.client.feign.interceptor;

import com.xyz.client.HttpClientUtils;
import com.xyz.client.Oauth1Signer;
import com.xyz.client.config.ClientCredentialConfig;
import com.xyz.exception.CommonException;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ByteArrayEntity;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.xyz.client.HttpClientUtils.CONTENT_TYPE_JSON;
import static com.xyz.client.HttpClientUtils.HEADER_CONTENT_TYPE;

/**
 * OAuth1 interceptor
 *
 * @author sxl
 */
@Slf4j
public class OAuth1Interceptor implements RequestInterceptor {
    private static final String ACCEPT_HEADER_NAME = "Accept";

    private static final int DEFAULT_CONNECT_TIMEOUT = 6000;
    private static final int DEFAULT_READ_TIMEOUT = 30000;

    private final ClientCredentialConfig clientCredentialConfig;

    public OAuth1Interceptor(ClientCredentialConfig clientCredentialConfig) {
        this.clientCredentialConfig = clientCredentialConfig;
    }

    @Override
    public void apply(RequestTemplate template) {
        try {
            HttpUriRequest httpRequest = toHttpUriRequest(template);
            ClientCredentialConfig.OAuthConfig oauthKey = clientCredentialConfig.findOAuthConfigByUrl(template.feignTarget().url());
            String token = Oauth1Signer.getOrCreate(oauthKey.getKey(), oauthKey.getSecret()).sign(httpRequest);
            template.header(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON);
            template.header(Oauth1Signer.HEADER_AUTH_TOKEN, token);
        } catch (Exception e) {
            log.error("Failed to sign request", e);
            throw new CommonException(null, "Failed to sign request", e);
        }
    }

    private HttpUriRequest toHttpUriRequest(RequestTemplate request)
            throws URISyntaxException {
        RequestBuilder requestBuilder = RequestBuilder.create(request.method());
        requestBuilder.setConfig(HttpClientUtils.buildRequestConfig(DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT));

        URI uri = new URIBuilder(request.feignTarget().url() + request.url()).build();

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
            HttpEntity entity = new ByteArrayEntity(request.body());
            requestBuilder.setEntity(entity);
        } else {
            requestBuilder.setEntity(new ByteArrayEntity(new byte[0]));
        }

        return requestBuilder.build();
    }
}