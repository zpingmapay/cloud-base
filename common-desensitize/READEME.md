#### 脱敏模块

##### 模块介绍

```
本模块主要提供对HTTP接口返回结果脱敏,日志脱敏等需要对对象信息进行脱敏处理的场合。
普通的基于工具类方法的方式，对代码的入侵性太强。编写起来又特别麻烦,本模块提供基于注解的方式，内置了常见的脱敏方式，便于开发。
```

##### 快速使用

###### springboot项目中对http接口返回结果进行脱敏

- 引入jar包

   ```xml
   <dependency>
     <groupId>com.xyz</groupId>
     <artifactId>common-desensitize</artifactId>
     <version>1.0.2</version>
   </dependency>
   ```

- 启动类开启脱敏总开关 (@EnableDesensitize注解)

   ```java
   @EnableDesensitize
   @SpringBootApplication
   public class SampleApplication {
       public static void main(String[] args) {
           SpringApplication.run(SampleApplication.class, args);
       }
   }
   ```
   
- HTTP接口注明返回结果需要脱敏 (@Desensitize注解)

   ```java
   /**
    * @author sxl
    * @since 2020/9/15 14:06
    */
   @Slf4j
   @RestController
   public class DesensitizeController {
   
       @Desensitize
       @PostMapping("/desensitize")
       public UserDto desensitize() {
           return buildBaseUser();
       }
   
       private UserDto buildBaseUser() {
           UserDto userDto = new UserDto();
           userDto.setName("张小明");
           userDto.setPassword("11111111111111");
           userDto.setIdCard("61252567384384473847384");
           userDto.setEmail("zhangxiaoming@51zy.com");
           userDto.setPhone("13000000000");
         	userDto.setPhones(Lists.newArrayList("13000000000", "18600000000"));
           return userDto;
       }
   }
   ```

   

- 给实体字段中添加指定注解标明脱敏方式

   ```java
   /**
    * @author sxl
    * @since 2020/9/15 14:08
    */
   @Data
   public class UserDto {
   		// 中文用户名脱敏
       @DesensitizeChineseName
       private String name;
     
   		// 卡ID脱敏
       @DesensitizeCardId
       private String idCard;
   
     	// 密码脱敏
       @DesensitizePassword
       private String password;
   
     	// 邮箱脱敏
       @DesensitizeEmail
       private String email;
   
     	// 手机号脱敏
       @DesensitizePhone
       private String phone;
     
     	/**
        * 对手机号做级联脱敏
        */
       @Desensitized
       @DesensitizePhone
       private List<String> phones;
   }
   ```


- 效果展示

   ```json
   {
       "name": "张*明",
       "idCard": "612525**********3847384",
       "password": "******",
       "email": "zha**********@51zy.com",
       "phone": "130****0000",
       "phones": [
           "130****0000",
           "186****0000"
       ]
   }
   ```



------



###### 使用DesensitizeUtil工具类进行手动脱敏

```java
// 返回脱敏后对象
DesensitizeUtil.desensitizeObj(T object);
// 返回脱敏后的对象json
DesensitizeUtil.desensitizeJson(Object object) 
```



##### 内置注解介绍

| 注解名称                | 使用位置               | 说明                                                     |
| ----------------------- | ---------------------- | -------------------------------------------------------- |
| @EnableDesensitize      | 启动类                 | 开启脱敏模块                                             |
| @Desensitize            | http接口方法           | 表明该接口返回值需要做脱敏处理                           |
| @DesensitizeChineseName | 属性                   | 中文用户名脱敏                                           |
| @DesensitizeCardId      | 属性                   | 卡号脱敏                                                 |
| @DesensitizePassword    | 属性                   | 密码脱敏                                                 |
| @DesensitizeEmail       | 属性                   | 邮箱脱敏                                                 |
| @DesensitizePhone       | 属性                   | 手机号脱敏                                               |
| @Desensitized           | 实体,集合,数组类型属性 | 级联脱敏,对实体内部或者集合,数组中的每个元素进行脱敏处理 |