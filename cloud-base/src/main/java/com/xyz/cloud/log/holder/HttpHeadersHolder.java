package com.xyz.cloud.log.holder;

import com.xyz.utils.JsonUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * Http headers holder holds the original http headers or the converted domain object from headers.
 * Life circle of the http header object in the holder is per http request.
 * <p>
 * Two ways to retrieve header values, one is by the getXXX() method of the holder object:
 * <code>
 * String appId = holder.getString("app-id");
 * </code>
 * Another method is via the converted domain object:
 * <code>
 * DomainHeadersHolder.DomainHeader headers = (DomainHeadersHolder.DomainHeader)holder.getHeaderObject();
 * String appId = headers.getAppId());
 * String userId = headers.getUserId());
 * </code>
 */
public interface HttpHeadersHolder {
    Object extract(HttpServletRequest httpServletRequest);

    String getString(String key);

    default void setHeaderObject(Object header) {
        Objects.requireNonNull(RequestContextHolder.getRequestAttributes()).setAttribute(HttpHeadersHolder.class.getName(), header, RequestAttributes.SCOPE_REQUEST);
    }

    default Object getHeaderObject() {
        return Objects.requireNonNull(RequestContextHolder.getRequestAttributes()).getAttribute(HttpHeadersHolder.class.getName(), RequestAttributes.SCOPE_REQUEST);
    }

    default int getInt(String key) {
        return Integer.valueOf(getString(key));
    }

    default boolean getBoolean(String key) {
        return Boolean.valueOf(getString(key));
    }

    default long getLong(String key) {
        return Long.valueOf(getString(key));
    }

    default <T> T getObject(String key, Class<T> clazz) {
        return JsonUtils.jsonToBean(getString(key), clazz);
    }
}
