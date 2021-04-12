package com.xyz.cloud.utils;

import com.xyz.cloud.utils.spel.SpelCondition;
import com.xyz.cloud.utils.spel.SpelRelation;
import com.xyz.function.TryWithCatch;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class SpelUtils {
    private final static ExpressionParser parser = new SpelExpressionParser();

    public static String parse(String exp, ProceedingJoinPoint pjp) {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();
        MethodBasedEvaluationContext context = new MethodBasedEvaluationContext(pjp.getTarget(), method, pjp.getArgs(), new DefaultParameterNameDiscoverer());

        return TryWithCatch.apply(() -> parser.parseExpression(exp).getValue(context, String.class), method.getName());
    }

    public static <C, R> R parse(String exp, C contextObject, Class<R> clazz, R defaultResult) {
        return TryWithCatch.apply(() -> {
            Expression expression = parser.parseExpression(exp);
            StandardEvaluationContext context = new StandardEvaluationContext(contextObject);
            return expression.getValue(context, clazz);
        }, defaultResult);
    }

    /**
     * Evaluate a context object against a spel bean.
     * @param spelBean POJO with fields annotated by @SpelCondition
     * @param contextObject context object
     * @return boolean result and the violation messages
     */
    public static <B, C> Pair<Boolean, List<String>> evaluate(B spelBean, C contextObject) {
        List<Pair<String, String>> spels = beanToSpelList(spelBean);
        List<String> result = spels.stream()
                .filter(x -> !parse(x.getLeft(), contextObject, Boolean.class, false))
                .map(x -> x.getRight())
                .distinct()
                .collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(result)) {
            return new ImmutablePair<>(false, result);
        }
        return new ImmutablePair<>(true, Collections.emptyList());
    }

    public static <Bean> String beanToSpel(Bean bean) {
        List<String> spelParts = beanToSpelList(bean).stream().map(x -> x.getLeft()).collect(Collectors.toList());
        return StringUtils.join(spelParts, " and ");
    }

    private static <Bean> List<Pair<String, String>> beanToSpelList(Bean bean) {
        return Arrays.stream(bean.getClass().getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(SpelCondition.class))
                .map(f -> convertFieldToSpel(bean, f))
                .filter(x -> x != null)
                .collect(Collectors.toList());
    }

    private static <Bean> Pair<String, String> convertFieldToSpel(Bean bean, Field field) {
        field.setAccessible(true);
        Object fieldValue = ReflectionUtils.getField(field, bean);
        if (fieldValue == null) {
            return null;
        }
        SpelCondition annotation = field.getAnnotation(SpelCondition.class);
        String name = annotation.name();
        String msg = annotation.msg();
        if(StringUtils.isBlank(name)) {
            name = field.getName();
        }

        SpelRelation spelRelation = annotation.relation();
        if (spelRelation == SpelRelation.IN || spelRelation == SpelRelation.NIN) {
            String logicRelation = spelRelation == SpelRelation.IN ? " or ": " and ";
            spelRelation = spelRelation == SpelRelation.IN ? SpelRelation.EQ : SpelRelation.NE;
            if (fieldValue instanceof Collection) {
                return collectionToSpel(name, logicRelation, spelRelation, fieldValue, msg);
            } else {
                return arrayToSpel(name, logicRelation, spelRelation, fieldValue, msg);
            }
        }

        return fieldToSpel(name, spelRelation, fieldValue, msg);
    }

    private static Pair<String, String> collectionToSpel(String name, String logicRelation, SpelRelation spelRelation, Object fieldValue, String msg) {
        Collection<?> coll = (Collection<?>) fieldValue;
        List<String> list = coll.stream().map(x -> fieldToSpel(name, spelRelation, x, msg).getLeft()).collect(Collectors.toList());
        return  new ImmutablePair<>("(" + StringUtils.join(list, logicRelation) + ")", msg);
    }

    private static Pair<String, String> arrayToSpel(String name, String logicRelation, SpelRelation spelRelation, Object fieldValue, String msg) {
        int length = Array.getLength(fieldValue);
        List<String> list = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            Object itemValue = Array.get(fieldValue, i);
            list.add(fieldToSpel(name, spelRelation, itemValue, msg).getLeft());
        }
        return new ImmutablePair<>("(" + StringUtils.join(list, logicRelation) + ")", msg);
    }


    private static Pair<String, String> fieldToSpel(String name, SpelRelation spelRelation, Object fieldValue, String msg) {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" ").append(spelRelation.getValue()).append(" ");

        if (fieldValue instanceof String) {
            sb.append("'").append(fieldValue).append("'");
        } else {
            sb.append(fieldValue);
        }
        return new ImmutablePair<>(sb.toString(), msg);
    }

}
