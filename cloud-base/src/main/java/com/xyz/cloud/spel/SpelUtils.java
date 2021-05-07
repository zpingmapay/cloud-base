package com.xyz.cloud.spel;

import com.xyz.function.TryWithCatch;
import com.xyz.utils.ValidationUtils;
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

import static com.xyz.cloud.spel.SpelRelation.EQ;
import static com.xyz.cloud.spel.SpelRelation.NE;


public class SpelUtils {
    private final static ExpressionParser parser = new SpelExpressionParser();

    public static String parse(String exp, ProceedingJoinPoint pjp) {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();
        MethodBasedEvaluationContext context = new MethodBasedEvaluationContext(pjp.getTarget(), method, pjp.getArgs(), new DefaultParameterNameDiscoverer());

        return TryWithCatch.apply(() -> parser.parseExpression(exp).getValue(context, String.class), method.getName());
    }

    @SuppressWarnings("unchecked")
    public static <C, R> R parse(String exp, C contextObject, R defaultResult) {
        return TryWithCatch.apply(() -> {
            Expression expression = parser.parseExpression(exp);
            StandardEvaluationContext context = new StandardEvaluationContext(contextObject);
            return (R) expression.getValue(context, defaultResult.getClass());
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
        return evaluate(spelBean, contextObject, null);
    }

    public static <B, C> Pair<Boolean, List<String>> evaluate(B spelBean, C contextObject, Map<String, List<String>> messageMapping) {
        List<Pair<String, String>> spels = beanToSpelList(spelBean, messageMapping);
        List<String> result = spels.stream()
                .filter(x -> !parse(x.getLeft(), contextObject, false))
                .map(Pair::getRight)
                .distinct()
                .collect(Collectors.toList());
        return CollectionUtils.isEmpty(result) ? TRUE : fail(result);
    }

    public static <Bean> String beanToSpel(Bean bean) {
        List<Pair<String, String>> spels = beanToSpelList(bean, null);
        return spels.stream()
                .map(Pair::getLeft)
                .collect(Collectors.joining(" and "));
    }

    public static <Bean> List<Pair<String, String>> beanToSpelList(Bean bean, Map<String, List<String>> fieldValueMapping) {
        return Arrays.stream(bean.getClass().getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(SpelCondition.class))
                .map(f -> convertFieldToSpel(bean, f, fieldValueMapping))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private static <Bean> Pair<String, String> convertFieldToSpel(Bean bean, Field field, Map<String, List<String>> messageMapping) {
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

        switch (spelRelation) {
            case IN:
                return collectionToSpel(field, name, " or ", EQ, fieldValue, msg, messageMapping);
            case NIN:
                return collectionToSpel(field, name, " and ", NE, fieldValue, msg, messageMapping);
            case HAS_ANY:
                return hasAnyToSpel(field, name, fieldValue, msg, messageMapping);
            case HAS_NONE:
                return hasNoneToSpel(field, name, fieldValue, msg, messageMapping);
            case HAS_ALL:
                return hasAllToSpel(field, name, fieldValue, msg, messageMapping);
            default:
                String errorMsg = convertErrorMsg(field, msg, messageMapping, fieldValue);
                return new ImmutablePair<>(fieldToSpel(name, spelRelation, fieldValue), errorMsg);
        }
    }

    private static Pair<String, String> collectionToSpel(Field field, String name, String logicRelation, SpelRelation spelRelation, Object fieldValue, String msg, Map<String, List<String>> messageMapping) {
        Collection<Object> itemList = fieldToCollection(fieldValue);
        ValidationUtils.notEmpty(itemList, String.format("Cannot convert %s to spel", field.getName()));
        String errorMsg = convertErrorMsg(field, msg, messageMapping, StringUtils.join(itemList, ","));
        return itemToSpel(name, logicRelation, spelRelation, itemList, errorMsg);
    }

    private static Pair<String, String> hasAnyToSpel(Field field, String name, Object fieldValue, String msg, Map<String, List<String>> messageMapping) {
        Collection<Object> itemList = fieldToCollection(fieldValue);
        ValidationUtils.notEmpty(itemList, String.format("Cannot convert %s to spel", field.getName()));
        String spel = "T(org.springframework.util.CollectionUtils).containsAny(" + name + ", " + joinFieldValue(itemList) + ")";
        String errorMsg = convertErrorMsg(field, msg, messageMapping, StringUtils.join(itemList, ","));
        return new ImmutablePair<>(spel, errorMsg);
    }

    private static Pair<String, String> hasAllToSpel(Field field, String name, Object fieldValue, String msg, Map<String, List<String>> messageMapping) {
        Collection<Object> itemList = fieldToCollection(fieldValue);
        ValidationUtils.notEmpty(itemList, String.format("Cannot convert %s to spel", field.getName()));
        String spel = name + ".containsAll(" + joinFieldValue(itemList) + ")";
        String errorMsg = convertErrorMsg(field, msg, messageMapping, StringUtils.join(itemList, ","));
        return new ImmutablePair<>(spel, errorMsg);
    }

    private static Pair<String, String> hasNoneToSpel(Field field, String name, Object fieldValue, String msg, Map<String, List<String>> messageMapping) {
        Collection<Object> itemList = fieldToCollection(fieldValue);
        ValidationUtils.notEmpty(itemList, String.format("Cannot convert %s to spel", field.getName()));
        String spel = "!T(org.springframework.util.CollectionUtils).containsAny(" + name + ", " + joinFieldValue(itemList) + ")";
        String errorMsg = convertErrorMsg(field, msg, messageMapping, StringUtils.join(itemList, ","));
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

    private static Pair<String, String> itemToSpel(String name, String logicRelation, SpelRelation spelRelation, Collection<?> itemList, String errorMsg) {
        List<String> list = itemList.stream().map(x -> fieldToSpel(name, spelRelation, x)).collect(Collectors.toList());
        String spel = "(" + StringUtils.join(list, logicRelation) + ")";
        return new ImmutablePair<>(spel, errorMsg);
    }

    @SuppressWarnings("unchecked")
    private static Collection<Object> fieldToCollection(Object fieldValue) {
        Collection<Object> itemList;
        if (fieldValue instanceof Collection) {
            itemList = (Collection<Object>) fieldValue;
        } else {
            int length = Array.getLength(fieldValue);
            itemList = new ArrayList<>();
            for (int i = 0; i < length; i++) {
                Object itemValue = Array.get(fieldValue, i);
                itemList.add(itemValue);
            }
        }
        return itemList;
    }

    private static String joinFieldValue(Collection<Object> itemList) {
        String joint = itemList.stream().map(item -> {
            if (item instanceof String) {
                return "'" + item + "'";
            } else {
                return item.toString();
            }
        }).collect(Collectors.joining(","));
        return "{" + joint + "}";
    }

    private static String convertErrorMsg(Field field, String msg, Map<String, List<String>> messageMapping, Object fieldValue) {
        String errorMsg;
        if (CollectionUtils.isEmpty(messageMapping) || !messageMapping.containsKey(field.getName())) {
            errorMsg = String.format(msg, fieldValue);
        } else {
            errorMsg = String.format(msg, StringUtils.join(messageMapping.get(field.getName()), ","));
        }
        return errorMsg;
    }

    private static Pair<Boolean, List<String>> fail(List<String> reasons) {
        return new ImmutablePair<>(false, reasons);
    }

}
