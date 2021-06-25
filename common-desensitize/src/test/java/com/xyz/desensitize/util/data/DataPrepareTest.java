package com.xyz.desensitize.util.data;


import com.xyz.desensitize.util.entry.*;

import java.util.*;

/**
 * 数据准备工具
 *
 * @author binbin.hou
 * date 2019/1/9
 */
public final class DataPrepareTest {

    /**
     * 构建用户-属性为列表，列表中为基础属性
     *
     * @return 构建嵌套信息
     * @since 0.0.2
     */
    public static UserEntryBaseType buildUserEntryBaseType() {
        UserEntryBaseType userEntryBaseType = new UserEntryBaseType();
        userEntryBaseType.setChineseNameList(Arrays.asList("盘古", "女娲", "伏羲"));
        userEntryBaseType.setChineseNameArray(new String[]{"盘古", "女娲", "伏羲"});
        return userEntryBaseType;
    }

    /**
     * 构建用户-属性为列表，列表中为基础属性
     *
     * @return 构建嵌套信息
     * @since 0.0.11
     */
    public static CustomUserEntryBaseType buildCustomUserEntryBaseType() {
        CustomUserEntryBaseType userEntryBaseType = new CustomUserEntryBaseType();
        userEntryBaseType.setChineseNameList(Arrays.asList("盘古", "女娲", "伏羲"));
        userEntryBaseType.setChineseNameArray(new String[]{"盘古", "女娲", "伏羲"});
        return userEntryBaseType;
    }

    /**
     * 构建用户-属性为列表，数组。列表中为对象。
     * @return 构建嵌套信息
     * @since 0.0.2
     */
    public static UserEntryObject buildUserEntryObject() {
        UserEntryObject userEntryObject = new UserEntryObject();
        User user = buildUser();
        User user2 = buildUser();
        User user3 = buildUser();
        userEntryObject.setUser(user);
        userEntryObject.setUserList(Arrays.asList(user2));
        userEntryObject.setUserArray(new User[]{user3});
        return userEntryObject;
    }

    /**
     * 构建用户-属性为列表，数组。列表中为对象。
     *
     * @return 构建嵌套信息
     * @since 0.0.11
     */
    public static CustomUserEntryObject buildCustomUserEntryObject() {
        CustomUserEntryObject userEntryObject = new CustomUserEntryObject();
        User user = buildUser();
        User user2 = buildUser();
        User user3 = buildUser();
        userEntryObject.setUser(user);
        userEntryObject.setUserList(Arrays.asList(user2));
        userEntryObject.setUserArray(new User[]{user3});
        return userEntryObject;
    }

    /**
     * 构建用户-属性为列表，数组，对象
     *
     * @return 对象
     * @since 0.0.2
     */
    public static UserGroup buildUserGroup() {
        UserGroup userGroup = new UserGroup();
        User user = buildUser();
        User coolUser = buildUser();

        userGroup.setPassword("123456");
        userGroup.setCoolUser(coolUser);
        userGroup.setUser(user);
        userGroup.setUserCollection(Collections.singletonList(user));
        userGroup.setUserList(Arrays.asList(user));
        userGroup.setUserSet(new HashSet<>(Arrays.asList(user)));
        Map<String, User> map = new HashMap<>();
        map.put("map", user);
        userGroup.setUserMap(map);
        return userGroup;
    }

    /**
     * 构建用户-属性为列表，数组，对象
     *
     * @return 对象
     * @since 0.0.11
     */
    public static CustomUserGroup buildCustomUserGroup() {
        CustomUserGroup userGroup = new CustomUserGroup();
        User user = buildUser();
        User coolUser = buildUser();

        userGroup.setPassword("123456");
        userGroup.setCoolUser(coolUser);
        userGroup.setUser(user);
        userGroup.setUserCollection(Collections.singletonList(user));
        userGroup.setUserList(Arrays.asList(user));
        userGroup.setUserSet(new HashSet<>(Arrays.asList(user)));
        Map<String, User> map = new HashMap<>();
        map.put("map", user);
        userGroup.setUserMap(map);
        return userGroup;
    }

    /**
     * 构建测试用户对象
     *
     * @return 创建后的对象
     * @since 0.0.1
     */
    public static User buildUser() {
        User user = new User();
        user.setUsername("脱敏君");
        user.setPassword("1234567");
        user.setEmail("12345@qq.com");
        user.setIdCard("123456190001011234");
        user.setPhone("18888888888");
        return user;
    }

    /**
     * 构建用户-属性为列表，数组，对象、数组
     *
     * @return 对象
     * @since 0.0.6
     */
    public static UserCollection buildUserCollection() {
        UserCollection userCollection = new UserCollection();
        User user = buildUser();

        userCollection.setUserCollection(Collections.singletonList(user));
        userCollection.setUserList(Arrays.asList(user));
        userCollection.setUserSet(new HashSet<>(Arrays.asList(user)));
        userCollection.setUserArray(new User[]{user});
        Map<String, User> map = new HashMap<>();
        map.put("map", user);
        userCollection.setUserMap(map);
        return userCollection;
    }

    /**
     * 构建用户-属性为列表，数组，对象、数组
     *
     * @return 对象
     * @since 0.0.11
     */
    public static CustomUserCollection buildCustomUserCollection() {
        CustomUserCollection userCollection = new CustomUserCollection();
        User user = buildUser();

        userCollection.setUserCollection(Collections.singletonList(user));
        userCollection.setUserList(Arrays.asList(user));
        userCollection.setUserSet(new HashSet<>(Arrays.asList(user)));
        userCollection.setUserArray(new User[]{user});
        Map<String, User> map = new HashMap<>();
        map.put("map", user);
        userCollection.setUserMap(map);
        return userCollection;
    }

    /**
     * 构建用户列表
     *
     * @return 构建的列表
     * @since 0.0.7
     */
    public static List<User> buildUserList() {
        List<User> userList = new ArrayList<>();
        userList.add(buildUser());

        User user2 = buildUser();
        user2.setUsername("集合测试");
        userList.add(user2);
        return userList;
    }

}
