package com.xyz.log;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleLog {
    private final String[] filterPackages;

    public SimpleLog(String... filterPackages) {
        this.filterPackages = filterPackages;
    }

    public void debug(Logger log, String format, Throwable t, Object... args) {
        String msg = formatMsg(format, args);
        log.debug(msg, filterStack(t));
    }

    public void debug(Logger log, String format, Object... args) {
        String msg = formatMsg(format, args);
        log.debug(msg);
    }

    public void info(Logger log, String format, Throwable t, Object... args) {
        String msg = formatMsg(format, args);
        log.info(msg, filterStack(t));
    }

    public void info(Logger log, String format, Object... args) {
        String msg = formatMsg(format, args);
        log.info(msg);
    }

    public void warn(Logger log, String format, Throwable t, Object... args) {
        String msg = formatMsg(format, args);
        log.warn(msg, filterStack(t));
    }

    public void warn(Logger log, String format, Object... args) {
        String msg = formatMsg(format, args);
        log.warn(msg);
    }

    public void error(Logger log, String format, Throwable t, Object... args) {
        String msg = formatMsg(format, args);
        log.error(msg, filterStack(t));
    }

    public void error(Logger log, String format, Object... args) {
        String msg = formatMsg(format, args);
        log.error(msg);
    }


    private String formatMsg(String format, Object[] args) {
        if(args == null || args.length == 0) {
            return format;
        }
        String template = format.replaceAll("\\{}", "%s");
        return String.format(template, args);
    }


    private final String FILTER_PACKAGE = "com.xyz";

    private Throwable filterStack(Throwable exception) {
        if(exception == null) {
            return null;
        }
        StackTraceElement[] stackTrace = exception.getStackTrace();
        try {
            List<StackTraceElement> list = Arrays.stream(stackTrace)
                    .filter(e -> e.getClassName().contains(FILTER_PACKAGE) || Arrays.stream(filterPackages).anyMatch(x -> e.getClassName().contains(x)))
                    .collect(Collectors.toList());

            if (CollectionUtils.isEmpty(list)) {
                return exception;
            }
            exception.setStackTrace(list.toArray(new StackTraceElement[0]));
            return exception;
        } catch (Exception e) {
            return exception;
        }
    }
}
