package com.xyz.client;

import com.xyz.cache.CacheManager;
import com.xyz.function.TryWithCatch;
import com.xyz.utils.Uuid;
import com.xyz.utils.ValidationUtils;
import feign.Client;
import feign.Request;
import feign.Response;
import feign.Util;
import lombok.extern.slf4j.Slf4j;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.signature.AuthorizationHeaderSigningStrategy;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.Configurable;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.MDC;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author sxl
 */
@Slf4j
public class FeignOAuth1Client implements Client {
    private static final String ACCEPT_HEADER_NAME = "Accept";
    public static final String CONSUMER_KEY = "consumer-key";
    public static final String CONSUMER_SECRET = "consumer-secret";

    private static final String HEADER_TRACE_ID = "trace-id";
    private static final String HEADER_TIMESTAMP = "timestamp";
    private static final String TID = "tid";

    private final CloseableHttpClient client;

    public FeignOAuth1Client() {
        client = httpClient();
    }

    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        HttpUriRequest httpRequest;
        try {
            httpRequest = toHttpUriRequest(request, options);
        } catch (URISyntaxException e) {
            throw new IOException("URL '" + request.url() + "' couldn't be parsed into a URI", e);
        }

        addTraceableHeader(httpRequest);
        try {
            sign(httpRequest);
        } catch (Exception e) {
            throw new RuntimeException("签名异常", e);
        }

        // 执行请求，获取响应
        HttpResponse httpResponse = client.execute(httpRequest);

        // 将HttpClient的响应对象转换为Feign的Response
        return toFeignResponse(httpResponse, request);
    }

    HttpUriRequest toHttpUriRequest(Request request, Request.Options options)
            throws URISyntaxException {
        RequestBuilder requestBuilder = RequestBuilder.create(request.httpMethod().name());

        // per request timeouts
        RequestConfig requestConfig =
                (client instanceof Configurable ? RequestConfig.copy(((Configurable) client).getConfig())
                        : RequestConfig.custom())
                        .setConnectTimeout(options.connectTimeoutMillis())
                        .setSocketTimeout(options.readTimeoutMillis())
                        .build();
        requestBuilder.setConfig(requestConfig);

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
                String content = request.requestBody().asString();
                entity = new StringEntity(content, contentType);
            } else {
                entity = new ByteArrayEntity(request.requestBody().asBytes());
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

    Response toFeignResponse(HttpResponse httpResponse, Request request) throws IOException {
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

    Response.Body toFeignBody(HttpResponse httpResponse) {
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
            public Reader asReader() throws IOException {
                return new InputStreamReader(this.asInputStream(), Util.UTF_8);
            }

            @Override
            public Reader asReader(Charset charset) throws IOException {
                Util.checkNotNull(charset, "charset should not be null", new Object[0]);
                return new InputStreamReader(this.asInputStream(), charset);
            }

            @Override
            public void close() throws IOException {
                EntityUtils.consume(entity);
            }
        };
    }

    /**
     * 建立服务连接超时时间,单位ms
     */
    private static final int DEFAULT_CONNECT_TIMEOUT = 5000;
    /**
     * 从连接池获取连接超时时间,单位ms
     */
    private static final int REQUEST_TIMEOUT = 5000;
    /**
     * 服务处理的最长等待时间,单位ms
     */
    private static final int DEFAULT_SOCKET_TIMEOUT = 60000;
    /**
     * 总的最大并发度
     */
    private static final int DEFAULT_MAX_TOTAL = 200;
    /**
     * 单个主机或域名的最大并发度
     */
    private static final int DEFAULT_MAX_PER_ROUTE = 100;
    /**
     * 空闲连接回收等待时间
     */
    private static final int CLOSE_IDLE_CONNECTION_WAIT_TIME_SECS = 30;

    public static PoolingHttpClientConnectionManager poolingConnectionManager() {
        SSLContextBuilder builder = new SSLContextBuilder();
        try {
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
        } catch (NoSuchAlgorithmException | KeyStoreException e) {
            log.error("Pooling Connection Manager Initialisation failure because of " + e.getMessage(), e);
        }
        SSLConnectionSocketFactory sslSf = null;
        try {
            sslSf = new SSLConnectionSocketFactory(builder.build());
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            log.error("Pooling Connection Manager Initialisation failure because of " + e.getMessage(), e);
        }

        RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", new PlainConnectionSocketFactory());
        if (sslSf != null) {
            registryBuilder.register("https", sslSf);
        }

        PoolingHttpClientConnectionManager poolingConnectionManager = new PoolingHttpClientConnectionManager(registryBuilder.build());
        poolingConnectionManager.setMaxTotal(DEFAULT_MAX_TOTAL);
        poolingConnectionManager.setDefaultMaxPerRoute(DEFAULT_MAX_PER_ROUTE);

        return poolingConnectionManager;
    }

    private CloseableHttpClient httpClient() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(REQUEST_TIMEOUT)
                .setConnectTimeout(DEFAULT_CONNECT_TIMEOUT)
                .setSocketTimeout(DEFAULT_SOCKET_TIMEOUT).build();

        return HttpClients.custom()
                .evictIdleConnections(CLOSE_IDLE_CONNECTION_WAIT_TIME_SECS, TimeUnit.SECONDS)
                .setDefaultRequestConfig(requestConfig)
                .setConnectionManager(poolingConnectionManager())
                .build();
    }

    private void addTraceableHeader(HttpUriRequest requestBase) {
        requestBase.addHeader(HEADER_TIMESTAMP, String.valueOf(System.currentTimeMillis()));
        TryWithCatch.run(() -> {
            String tid = MDC.get(TID);
            tid = StringUtils.isBlank(tid) ? Uuid.shortUuid() : tid;
            requestBase.addHeader(HEADER_TRACE_ID, tid);
        });
    }

    private void sign(HttpUriRequest httpRequest) throws Exception {
        try {
            Optional<String> consumerKeyOp = Arrays.stream(httpRequest.getHeaders(CONSUMER_KEY)).findFirst().map(NameValuePair::getValue);
            ValidationUtils.isTrue(consumerKeyOp.isPresent(), "OAuth1 consumer key not correct");
            Optional<String> consumerSecretOp = Arrays.stream(httpRequest.getHeaders(CONSUMER_SECRET)).findFirst().map(NameValuePair::getValue);
            ValidationUtils.isTrue(consumerSecretOp.isPresent(), "OAuth1 consumer secret not correct");
            String consumerKey = consumerKeyOp.get();
            ValidationUtils.isTrue(StringUtils.isNoneBlank(consumerKey), "OAuth1 consumer key not correct");
            String consumerSecret = consumerSecretOp.get();
            ValidationUtils.isTrue(StringUtils.isNoneBlank(consumerSecret), "OAuth1 consumer secret not correct");

            OAuthConsumer authConsumer = CacheManager.getFromLocalCacheOrCreate(FeignOAuth1Client.class.getName(), consumerKey.toUpperCase(), () -> {
                OAuthConsumer oauthConsumer = new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);
                oauthConsumer.setSigningStrategy(new AuthorizationHeaderSigningStrategy());
                return oauthConsumer;
            });
            authConsumer.sign(httpRequest);
        } finally {
            httpRequest.removeHeaders(CONSUMER_KEY);
            httpRequest.removeHeaders(CONSUMER_SECRET);
        }
    }
}