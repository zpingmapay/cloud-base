package com.xyz.cloud.utils;

import com.xyz.utils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Locale;

/**
 * @author lihongbin
 * @date 2021年05月08日 21:19
 */
public final class TemplateStatement {
    private static TemplateEngine TEXT_TEMPLATE_ENGINE = new TemplateEngine();

    public static String txtProcess(@NotEmpty String txt, @NotNull Object beanObj) {
        try {
            if (StringUtils.isEmpty(txt) || beanObj == null) return null;
            Context context = new Context(Locale.SIMPLIFIED_CHINESE, BeanUtils.beanToMap(beanObj));
            return TEXT_TEMPLATE_ENGINE.process(txt, context);
        } catch (Exception e) {
            return null;
        }
    }
}
