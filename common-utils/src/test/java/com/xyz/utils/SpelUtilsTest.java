package com.xyz.utils;

import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.math.BigDecimal;

public class SpelUtilsTest {
    @Test
    public void testParse() {
        Order order = buildOrder("1", BigDecimal.valueOf(1.0), BigDecimal.TEN, BigDecimal.TEN, true);
        BigDecimal awards = SpelUtils.parse("3 + payAmount * 0.001", order, BigDecimal.class, BigDecimal.TEN);
        Assert.isTrue(BigDecimal.valueOf(3.01).compareTo(awards) == 0, "awarded points calculation is not correct");

        boolean orderPaid = SpelUtils.parse("price.multiply(quantity).compareTo(payAmount) == 0 && paid", order, Boolean.class, false);
        Assert.isTrue(orderPaid, "order is paid indeed");
    }

    private Order buildOrder(String orderNo, BigDecimal price, BigDecimal quantity, BigDecimal payAmount, boolean paid) {
        Order order = new Order();
        order.orderNo = orderNo;
        order.price = price;
        order.quantity = quantity;
        order.payAmount = payAmount;
        order.paid = paid;
        return order;
    }

    @Data
    public class Order {
        private String orderNo;
        private BigDecimal price;
        private BigDecimal quantity;
        private BigDecimal payAmount;
        private boolean paid;
    }
}
