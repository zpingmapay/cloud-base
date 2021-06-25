package com.xyz.cloud.spel;

import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.util.List;

import static com.xyz.cloud.spel.SpelRelation.*;


public class SpelUtilsTest {
    @Test
    public void testParse() {
        Order order = buildOrder("1", BigDecimal.valueOf(1.0), BigDecimal.TEN, BigDecimal.TEN, "上海市", 1, "GAS", true, Lists.newArrayList("seed_user", "lost_user"));
        BigDecimal awards = SpelUtils.parse("3 + orderAmount * 0.001", order, BigDecimal.TEN);
        Assert.isTrue(BigDecimal.valueOf(3.01).compareTo(awards) == 0, "awarded points calculation is not correct");

        boolean orderPaid = SpelUtils.parse("price.multiply(quantity).compareTo(orderAmount) == 0 && paid", order, false);
        Assert.isTrue(orderPaid, "order is paid indeed");
    }

    @Test
    public void testParseContains() {
        Order order = buildOrder("1", BigDecimal.valueOf(1.0), BigDecimal.TEN, BigDecimal.TEN, "上海市", 1, "GAS", true, Lists.newArrayList("seed_user", "lost_user"));
        Boolean result = SpelUtils.parse("orderAmount > 3 and T(org.springframework.util.CollectionUtils).containsAny(userTags, {'new_user', 'seed_user'})", order, false);
        Assert.isTrue(result, "result should be true");
        result = SpelUtils.parse("orderAmount > 3 and userTags.containsAll({'lost_user', 'seed_user'})", order, false);
        Assert.isTrue(result, "result should be true");
        result = SpelUtils.parse("orderAmount > 3 and !T(org.springframework.util.CollectionUtils).containsAny(userTags, {'new_user', 'test_user'})", order, false);
        Assert.isTrue(result, "result should be true");
    }

    @Test
    public void testToSpel() {
        PromoRule rule = new PromoRule();
        rule.setCities(new String[]{"北京市", "上海市"});
        rule.setExclusiveCities(new String[]{"天津市", "海南市"});
        rule.setOrderAmount(new BigDecimal(100));
        rule.setStationIds(new int[]{1,2,3});
        rule.setSkuCode("GAS");
        rule.setPayMethods(Lists.newArrayList("wechat_pay", "ali_pay"));
        rule.setUserTags(Lists.newArrayList("new_user", "seed_user"));
        String spelRule = SpelUtils.beanToSpel(rule);
        Assert.notNull(spelRule, "rule is null");
    }

    @Test
    public void testEvaluate1() {
        PromoRule rule = new PromoRule();
        rule.setCities(new String[]{"北京市", "上海市"});
        rule.setExclusiveCities(new String[]{"天津市", "海南市"});
        rule.setOrderAmount(new BigDecimal(100));
        rule.setStationIds(new int[]{1,2,3});
        rule.setSkuCode("GAS");
        rule.setPayMethods(Lists.newArrayList("wechat_pay", "ali_pay"));

        Order order = buildOrder("1", BigDecimal.valueOf(1.0), BigDecimal.TEN, BigDecimal.TEN, "天津市", 1, "GAS", true, null);
        Pair<Boolean, List<String>> evaluateResult = SpelUtils.evaluate(rule, order);
        Assert.isTrue(!evaluateResult.getLeft(), "result is true");
        Assert.isTrue(4 == evaluateResult.getRight().size(), "not 4 error msg");
    }

    @Test
    public void testEvaluate2() {
        PromoRule rule = new PromoRule();
        rule.setCities(new String[]{"北京市", "上海市"});
        rule.setExclusiveCities(new String[]{"天津市", "海南市"});
        rule.setOrderAmount(new BigDecimal(10));
        rule.setStationIds(new int[]{1,2,3});
        rule.setSkuCode("GAS");
        rule.setUserTags(Lists.newArrayList("seed_user", "new_user"));

        Order order = buildOrder("1", BigDecimal.valueOf(1.0), BigDecimal.TEN, BigDecimal.TEN, "上海市", 1, "GAS", true, Lists.newArrayList("lost_user"));
        Pair<Boolean, List<String>> evaluateResult = SpelUtils.evaluate(rule, order);
        Assert.isTrue(!evaluateResult.getLeft(), "result is false");
    }

    private Order buildOrder(String orderNo, BigDecimal price, BigDecimal quantity, BigDecimal orderAmount, String city, int station, String skuCode, boolean paid, List<String> userTags) {
        Order order = new Order();
        order.orderNo = orderNo;
        order.price = price;
        order.quantity = quantity;
        order.orderAmount = orderAmount;
        order.city = city;
        order.station = station;
        order.skuCode = skuCode;
        order.paid = paid;
        order.userTags = userTags;
        return order;
    }

    @Data
    public class Order {
        private String orderNo;
        private BigDecimal price;
        private BigDecimal quantity;
        private BigDecimal orderAmount;
        private String city;
        private int station;
        private String skuCode;
        private boolean paid;
        private List<String> userTags;
    }

    @Data
    public class PromoRule {
        @SpelCondition(name = "city", relation = IN, msg = "下单城市不参加活动,仅限%s地区用户")
        private String[] cities;
        @SpelCondition(name = "city", relation = NIN, msg = "%s地区用户不参加活动")
        private String[] exclusiveCities;
        @SpelCondition(name = "station", relation = IN, msg = "下单站点不参加活动")
        private int[] stationIds;
        @SpelCondition(relation = GE, msg = "订单金额不低于%s元")
        private BigDecimal orderAmount;
        @SpelCondition
        private String skuCode;
        @SpelCondition(name = "payMethod", relation = IN, msg = "支付方式不满足活动要求")
        private List<String> payMethods;
        @SpelCondition(name = "userTags", relation = HAS_ANY, msg = "该类用户不参加活动")
        private List<String> userTags;
    }
}
