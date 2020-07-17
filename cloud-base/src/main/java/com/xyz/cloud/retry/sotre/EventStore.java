package com.xyz.cloud.retry.sotre;

import com.xyz.cloud.retry.RetryableEvent;
import com.xyz.utils.JsonUtils;
import com.xyz.utils.ValidationUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Append-only store for storing Spring Event
 */
public interface EventStore {
    EventStore newStore(Class<? extends RetryableEvent> eventClass);

    <T extends RetryableEvent> void add(@NotBlank String listenerClassName, @NotBlank String actionMethodName, @NotNull T event, int maxAttempts);

    List<StoreItem> list();

    <T extends RetryableEvent> void remove(@NotBlank String listenerClassName, @NotBlank String actionMethodName, @NotNull T event);

    <T extends RetryableEvent> void update(@NotBlank String listenerClassName, @NotBlank String actionMethodName, @NotNull T event);

    int size();

    Class<? extends RetryableEvent> getEventClass();

    default String buildKey(String listenerClassName, String actionMethodName, RetryableEvent event) {
        return String.format("%s_%s_%s", listenerClassName, actionMethodName, event.getTraceId());
    }

    @Data
    @Slf4j
    class StoreItem {
        private String listenerClassName;
        private String actionMethodName;
        private String eventBody;
        private int maxAttempts;

        public static <T extends RetryableEvent> StoreItem create(String listenerClassName, String actionMethodName, T event, int maxAttempts) {
            StoreItem item = new StoreItem();
            item.listenerClassName = listenerClassName;
            item.actionMethodName = actionMethodName;
            item.eventBody = JsonUtils.beanToJson(event);
            item.maxAttempts = maxAttempts;
            return item;
        }

        public <T extends RetryableEvent> void redo(ApplicationContext ctx, T event) {
            try {
                log.debug("Retrying event {}", JsonUtils.beanToJson(event));
                Object listener = ctx.getBean(Class.forName(listenerClassName));
                Method method = BeanUtils.findDeclaredMethod(listener.getClass(), actionMethodName, event.getClass());
                ValidationUtils.notNull(method, String.format("Method %s not found in class %s", actionMethodName, listenerClassName));
                method.invoke(listener, event);
            } catch (Throwable e) {
                log.warn("Failed to handle event {}", JsonUtils.beanToJson(event), e);
            }
        }
    }
}
