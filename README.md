# cloud-base - <br/>Utils and tools for cloud projects.

[Quick start](https://github.com/zpingmapay/cloud-base#quick-start) | [Documentation]() | [Javadocs]() | [Changelog]() | [Code examples]() | [FAQs]() | [Report an issue]()

## Features

* Redission distributed locks
* cache  
* spring cloud feign 
* OAuth1
* JWT token
* retry


## Quick start

#### Maven 
     <!--cloud  base-->  
    <dependency>
           <groupId>com.xyz</groupId>
           <artifactId>cloud-base</artifactId>
           <version>1.1-SNAPSHOT</version>
    </dependency>  

#### 分布式锁

```
 您需要使用@EnableLock 注解在您的启动类上

 @Lock(key = "'sample.'+#input")
     public void execute(String input) {
         log.info("sample service execute {}", input);
         try {
             TimeUnit.SECONDS.sleep(1);
         } catch (InterruptedException e) {
             e.printStackTrace();
         }
     } 
```
#### 缓存

```

  //1 local cache
     public void should_all_success() {
            ICache<String, String> cache = CacheManager.getLocalCache("test");
            cache.put("1", "test1");
            cache.put("2", "test2");
            String value = cache.get("1");
            cache.remove("1");
        }

//2 redission cache
     public void testRedisCache() {
             RedissonClient redissonClient = Redisson.create();
             String value = CacheManager.getFromRedisOrCreate(CacheTest.class.getName(), "1", redissonClient, (k) -> "test1");
             Assert.isTrue("test1".equals(value), "value is not test1");
             value = CacheManager.getFromRedisOrCreate(CacheTest.class.getName(), "1", redissonClient, (k) -> "test2");
             Assert.isTrue("test1".equals(value), "value is not test1");
         }

```

#### 基于JWT token 验签

```

 您需要使用@EnableJwt 注解在您的启动类上;
 在您需要验签的类或者方法上使用JwtSecured注解，容器会自动为您验签
     @JwtSecured
         public ResultDto<String> doPost() throws Exception {
             DomainHeadersHolder.DomainHeader headerObject = httpHeadersHolder.getHeaderObject();
             String userId = headerObject.getUserId();
             remoteService.echo(userId);
             ValidationUtils.isTrue(userId.equals(this.getUserId()), "Incorrect user id found");
             log.info("do post");
             eventPublisher.publish(new SampleEvent(userId, headerObject.getTraceId()));
             return ResultDto.ok("post ok");
         }

您可以在yml自定义属性

cloud:
  jwt:
    app-id: 1008
    secret: sample.app20200808qa1z3838wsxedc3rfv4tgbyhnujm5ikolp9ttt$QA0ZWSXED2CRFV8TGB2YHNU7JM3IKO8LPa12*@(cloud.jwt)
    ttl-in-hours: 24
    multi-login-check: true
```
#### 基于OAuth1 验签

```
  你可以直接使用OAuth1HttpClient来进行oauth1验签并调用
  
```

#### retry

```
 您需要使用@EnableRetryableEvent 注解在您的启动类上;

         @Retryable(maxAttempts = 5)
         @EventListener
         @Async
         @Traceable
         public void handleTestEvent(SampleEvent event) {
             throw new RetryableException("Failed to handle sample event");
         }
```


```
你也可以使用@CloudApplication来开启cloud base功能；
@CloudApplication包含:@EnableJwt
                     @EnableLock
                     @EnableRetryableEvent
                     @EnableTraceable



```

#### spring cloud feign


#### Maven 

    <dependency>
       <groupId>com.xyz</groupId>
       <artifactId>common-clients</artifactId>
          <version>1.1-SNAPSHOT</version>
    </dependency>  
    
```

  您需要使用@EnableFeignClient注解在您的启动类上

@FeignClient(name = "sample-signer", url = "${cloud.client.signer.sample-signer.url}",
        configuration = {FeignFormSupportConfig.class, SimpleRequestSigner.class})
public interface SampleSignerServiceSpi {

    @PostMapping(value = "/", headers = "method=station.setDiyPrice", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ResultDto<DiyPriceDto.Response> setStationDiyPrice(DiyPriceDto.Request request);
}

配置文件.yml
  cloud:
    client:
      oauth:
        sample-oauth:
          url: http://localhost:1008
          key: oauth1_consumer_key_of_sample_service
          secret: oauth1_consumer_secret_of_sample_service
  
      signer:
        sample-signer:
          url: cloud.client.sample-signer.url
          app-id: cloud.client.sample-signer.app-id
          app-key: cloud.client.sample-signer.app-key
     
```
