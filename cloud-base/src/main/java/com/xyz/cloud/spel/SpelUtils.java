package com.xyz.cloud.spel;

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

import static com.xyz.cloud.spel.SpelRelation.*;


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
     *
     * @param spelBean      POJO with fields annotated by @SpelCondition
     * @param contextObject context object
     * @return boolean result and the violation messages
     */
    private static final Pair<Boolean, List<String>> TRUE = new ImmutablePair<>(true, Collections.emptyList());

    public static <B, C> Pair<Boolean, List<String>> evaluate(B spelBean, C contextObject) {
        List<Pair<String, String>> spels = beanToSpelList(spelBean);
        List<String> result = spels.stream()
                .filter(x -> !parse(x.getLeft(), contextObject, Boolean.class, false))
                .map(Pair::getRight)
                .distinct()
                .collect(Collectors.toList());
        return CollectionUtils.isEmpty(result) ? TRUE : new ImmutablePair<>(false, result);
    }

    public static <Bean> String beanToSpel(Bean bean) {
        List<String> spelParts = beanToSpelList(bean).stream().map(Pair::getLeft).collect(Collectors.toList());
        return StringUtils.join(spelParts, " and ");
    }

    private static <Bean> List<Pair<String, String>> beanToSpelList(Bean bean) {
        return Arrays.stream(bean.getClass().getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(SpelCondition.class))
                .map(f -> convertFieldToSpel(bean, f))
                .filter(Objects::nonNull)
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
        if (StringUtils.isBlank(name)) {
            name = field.getName();
        }
        String msg = annotation.msg();
        SpelRelation spelRelation = annotation.relation();

        if (spelRelation == IN || spelRelation == NIN) {
            String logicRelation = spelRelation == IN ? " or " : " and ";
            spelRelation = spelRelation == IN ? EQ : NE;
            if (fieldValue instanceof Collection) {
                return collectionToSpel(name, logicRelation, spelRelation, fieldValue, msg);
            } else {
                return arrayToSpel(name, logicRelation, spelRelation, fieldValue, msg);
            }
        }
        String errorMsg = String.format(msg, fieldValue);
        return new ImmutablePair<>(fieldToSpel(name, spelRelation, fieldValue), errorMsg);
    }

    private static Pair<String, String> collectionToSpel(String name, String logicRelation, SpelRelation spelRelation, Object fieldValue, String msg) {
        Collection<?> itemList = (Collection<?>) fieldValue;
        return itemToSpel(name, logicRelation, spelRelation, itemList, msg);
    }

    private static Pair<String, String> arrayToSpel(String name, String logicRelation, SpelRelation spelRelation, Object fieldValue, String msg) {
        int length = Array.getLength(fieldValue);
        List<Object> itemList = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            Object itemValue = Array.get(fieldValue, i);
            itemList.add(itemValue);
        }
        return itemToSpel(name, logicRelation, spelRelation, itemList, msg);
    }

    private static Pair<String, String> itemToSpel(String name, String logicRelation, SpelRelation spelRelation, Collection<?> itemList, String msg) {
        List<String> list = itemList.stream().map(x -> fieldToSpel(name, spelRelation, x)).collect(Collectors.toList());
        String spel = "(" + StringUtils.join(list, logicRelation) + ")";
        String errorMsg = String.format(msg, StringUtils.join(itemList, ","));
        return new ImmutablePair<>(spel, errorMsg);
    }

    private static String fieldToSpel(String name, SpelRelation spelRelation, Object fieldValue) {
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" ").append(spelRelation.getValue()).append(" ");

        if (fieldValue instanceof String) {
            sb.append("'").append(fieldValue).append("'");
        } else {
            sb.append(fieldValue);
        }
        return sb.toString();
    }

}
