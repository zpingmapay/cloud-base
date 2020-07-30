package com.xyz.cloud.trace.annotation;

import com.xyz.cloud.trace.config.TraceableConfiguration;
import com.xyz.cloud.trace.config.TraceableThreadPoolConfiguration;
import com.xyz.cloud.trace.holder.HttpHeadersHolder;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.*;

/**
 * Enable trace feature by importing a {@link TraceableConfiguration auto-configuration}
 * class and also import {@link TraceableThreadPoolConfiguration configuration}, which
 * initiate a {@code ThreadPoolExecutor} bean.
 * <p>
 * Request, response exception (if any) and execution time of Methods annotated with
 * {@code GetMapping} or {@code PostMapping} in a {@link RestController controller} class
 * will be logged automatically after trace feature enabled.
 * <p>
 * Http headers will be extracted in a {@link HttpHeadersHolder} object and can be accessed
 * in the whole request life circle.
 * If a {@code trace-id} exists in http headers, it will be populated, otherwise a uuid
 * will be generated automatically. Trace-id will be logged in logback file as {@code TID}
 * column. It will be also passed to remoted Rest services or an async functions.
 * <p>
 * If a {@user-id} is populated in the context, it will also be logged as as {@code UID} column.
 * <p>
 * Annotation {@code Traceable} is a way to enable trace feature on the other methods.
 *
 * @author Zaiping Ma
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({TraceableConfiguration.class, TraceableThreadPoolConfiguration.class})
public @interface EnableTraceable {
}
