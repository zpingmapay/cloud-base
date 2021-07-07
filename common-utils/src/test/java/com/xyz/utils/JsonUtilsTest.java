package com.xyz.utils;

import com.google.common.collect.Lists;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static com.alibaba.fastjson.serializer.SerializerFeature.DisableCircularReferenceDetect;
import static com.alibaba.fastjson.serializer.SerializerFeature.WriteClassName;
import static com.xyz.utils.BeanUtilsTest.initSample;

public class JsonUtilsTest {
    @Test
    public void should_all_success() {
        BeanUtilsTest.Sample sample = initSample();
        String json = JsonUtils.beanToJson(sample);
        Assert.notNull(json, "Bean to json failed");

        BeanUtilsTest.Sample sample1 = JsonUtils.jsonToBean(json, BeanUtilsTest.Sample.class);
        Assert.isTrue(sample1.getParam5().equals(sample.getParam5()), "Json to bean failed");
    }

    @Test
    public void testDeserializeWithSubClass() {
        List<Item> list = Lists.newArrayList();
        list.add(new LinkItem("1", "/some/link"));
        list.add(new HrefItem("2", "href:/some/link"));
        list.add(new UrlItem("3", "https://some/link?a=([{$id}])"));

        String json = JsonUtils.beanToJson(list, DisableCircularReferenceDetect, WriteClassName);
        Assert.notNull(json, "to json is null");

        List<Item> items = JsonUtils.jsonToList(json, Item.class);
        Assert.isTrue(!CollectionUtils.isEmpty(items), "list is empty");

        String json1 = JsonUtils.beanToJson(items, DisableCircularReferenceDetect, WriteClassName);
        Assert.isTrue(json.equals(json1), "deserialize failed");
    }

    @Data
    public static abstract class Item {
        protected String id;
    }

    @Data
    public static class LinkItem extends Item {
        private String link;

        public LinkItem(String id, String link) {
            this.id = id;
            this.link = link;
        }
    }

    @Data
    public static class HrefItem extends Item {
        private String href;

        public HrefItem(String id, String href) {
            this.id = id;
            this.href = href;
        }
    }

    @Data
    public static class UrlItem extends Item {
        private String url;

        public UrlItem(String id, String url) {
            this.id = id;
            this.url = url;
        }
    }
}
