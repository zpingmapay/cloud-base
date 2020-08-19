package com.xyz.utils;

import com.xyz.function.TryWithCatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;

public class SpelUtils {
    private final static ExpressionParser parser = new SpelExpressionParser();

    public static String parse(String exp, ProceedingJoinPoint pjp) {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();
        MethodBasedEvaluationContext context = new MethodBasedEvaluationContext(pjp.getTarget(), method, pjp.getArgs(), new DefaultParameterNameDiscoverer());

        return parser.parseExpression(exp).getValue(context, String.class);
    }

    public static <C, R> R parse(String exp, C contextObject, Class<R> clazz, R defaultResult) {
        return TryWithCatch.apply(() -> {
            Expression expression = parser.parseExpression(exp);
            StandardEvaluationContext context = new StandardEvaluationContext(contextObject);
            return expression.getValue(context, clazz);
        }, defaultResult);
    }
}
