[Quick start](#quick-start) | [Documentation]() | [Javadocs]() | [Changelog]() | [Code examples]() | [FAQs]() | [Report an issue]()

Cloud-base项目初衷是：
<p>希望提供一些"脚手架"式的工具或框架，让大家更容易地新建一个Spring boot/cloud项目

## Features
* [缓存(Cache)](#缓存(Cache))
* [声明式锁(Declarative Lock)](#声明式锁(Declarative Lock))
* [分布式可重试事件(Distributed Retryable Event)](#分布式可重试事件(Distributed Retryable Event))
* [可跟踪日志(Traceable Log)](#可跟踪日志(Traceable Log))
* [前端的后端(Backend for Front-end)JWT认证](#前端的后端(Backend for Front-end)JWT认证)
* [后端的后端(Backend for Backend)OAuth1认证](#后端的后端(Backend for Backend)OAuth1认证)
* [声明式Feign客户端(Declarative Feign Client)](#声明式Feign客户端(Declarative Feign Client))

## Quick start

### Maven 
#### Cloud-base的Maven依赖
```
    <dependency>
           <groupId>com.xyz</groupId>
           <artifactId>cloud-base</artifactId>
           <version>1.1-SNAPSHOT</version>
    </dependency>  
```
#### Common-clients的Maven依赖
```
    <dependency>
           <groupId>com.xyz</groupId>
           <artifactId>common-clients</artifactId>
           <version>1.1-SNAPSHOT</version>
    </dependency>  
```

### 缓存(Cache)
<li>优先推荐使用Spring的@Cacheable注解，仅在那些不方便使用注解的场合下使用ICache接口
<li>使用Cache的前提是：被缓存的对象要么是**不可以变的(Immutable)**，要么对象的**全生命周期**都在该应用中管理，否则会产生一致性(Consistency)问题

#### 1. 本地缓存(Local Cache)
```
    ICache<String, String> cache = CacheManager.getLocalCache("test");
    cache.put("1", "test1");
    String value = cache.get("1");
    cache.remove("1");
```

#### 2. Redis缓存(Redis Cache)
```
    ICache<String, String> cache = CacheManager.getRedisCache("test", redissonClient);
    cache.put("1", "test1");
    String value = cache.get("1");
    cache.remove("1");
```

### 声明式锁(Declarative Lock)

#### @EnableLock注解
在项目中引入@EnableLock注解，通常加在启动类上

#### @Lock注解
在需要锁保护的方法上面加@Lock注解
```
    @Lock(key = "'sample.'+#input")
    public void lockProtected(String input) {
        log.info("lock protected method execute {}", input);
        //do sth important here
    } 
```

### 分布式可重试事件(Distributed Retryable Event)
<li>在需要保证最终一致性(Eventually Consistent)的处理中引入可重试事件，通常是更新远程资源
<li>可重试的事件一定保证可以**幂等**执行
<li>通常是**异步事件**，一般不会在同步事件中引入重试机制
<li>分布式可重试事件依赖于一种分布式存储机制(默认是Redis)
<li>分布式可重试事件基于Spring Event框架

#### @EnableRetryableEvent注解
在项目中引入@EnableRetryableEvent注解，通常加在启动类上

#### @Retryable注解
在需要重试的事件处理方法上，加@Retryable注解
```
    @Retryable(maxAttempts = 5)
    @EventListener
    @Async
    public void handleTestEvent(SampleEvent event) {
        //handle event here
        throw new RetryableException("Failed to handle the event");
    }
```

#### RetryableException异常
事件处理的方法中如果抛出了RetryableException，将触发重试机制

#### 重试间隔(Retry Interval)
可在spring boot配置文件中指定重试间隔
```
    cloud:
      retry:
        interval-in-seconds: 30
```
目前仅支持固定间隔

### 可跟踪日志(Traceable Log)

#### @EnableTraceable注解
在项目中引入@EnableTraceable注解，通常加在启动类上

#### 记录RestController请求和响应
引入@EnableTraceable注解后，项目中RestController中的@GetMapping和@PostMapping标注的方法请求/响应会被记录到logback日志中：
```
13:42:40.047 INFO [http-nio-1008-exec-1] c.x.c.trace.ControllerLogAspect 12347- URI:/me, param:["test"]
13:42:40.085 INFO [http-nio-1008-exec-1] c.x.c.trace.ControllerLogAspect 12347- URI:/me, res:{"code":200,"data":"123","msg":"OK"}, took:147ms
```

#### DefaultGlobalExceptionHandler
引入@EnableTraceable注解后，项目中的异常会被记录到logback日记中：
```
3:47:29.904 ERROR [http-nio-1008-exec-6] c.x.c.e.DefaultGlobalExceptionHandler - Access exception, herders: {"content-length":"0","app-id":"10003","lng":"132.654321","cookie":"JSESSIONID=2C645526E4EE226C10D1284E6C5BB7FF","postman-token":"5d24a4fe-2514-44d1-84f8-1375bf070a96","accept":"*/*","trace-id":"12347","host":"localhost:1008","content-type":"application/json","connection":"keep-alive","cache-control":"no-cache","lat":"31.123457","accept-encoding":"gzip, deflate, br","user-agent":"Mozilla/5.0 (iPhone; CPU iPhone OS 8_0 like Mac OS X) AppleWebKit/600.1.4 (KHTML, like Gecko) Mobile/12A365 MicroMessenger/5.4.1 NetType/WIFI"}
com.xyz.exception.AccessException: Access token is required
	at com.xyz.cloud.jwt.JwtAspect.authWithJwt(JwtAspect.java:42)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
```

#### @Traceable注解
被@Traceable注解标注的方法的请求/响应会被记录到logback日志中
```
    @Traceable
    @PerformanceWatch
    public String echo(String input) throws Exception {
        log.info("this is a slow remote echo method, {}", input);
        TimeUnit.SECONDS.sleep(2);
        return input;
    }
```
输出的日志：
```
13:52:07.588 INFO [http-nio-1008-exec-10] c.x.cloud.trace.TraceableAspect 12347-123 SampleRemoteService.echo,param:["123"]
13:52:07.593 INFO [http-nio-1008-exec-10] c.x.c.s.s.SampleRemoteService 12347-123 this is a slow remote echo method, 123
13:52:09.594 WARN [http-nio-1008-exec-10] c.x.c.t.PerformanceWatchAspect 12347-123 Slow API:SampleRemoteService.echo,arg:["123"],took:2006ms
13:52:09.594 INFO [http-nio-1008-exec-10] c.x.cloud.trace.TraceableAspect 12347-123 SampleRemoteService.echo,res:"123",took:2006ms
```

#### 读取http header
```
    @Resource
    private HttpHeadersHolder<DomainHeadersHolder.DomainHeader> httpHeadersHolder;

    public void foo() {
        DomainHeadersHolder.DomainHeader headerObject = httpHeadersHolder.getHeaderObject();    
        String traceId = headerObject.getTraceId();
        String appId = headerObject.getAppId();
    }

```

#### Logback中记录TID/UID
logback配置中加入TID/UID：
```
    <property name="PATTERN" value="%d{HH:mm:ss.SSS} %p [%t] %C{32} %X{tid}-%X{uid} %m%n" />
```
这样日志文件中将输出上例中的：
```
12347-123
```
其中"12347"是http header中读取的trace-id, "123"是Jwt token中的user-id

#### TID/UID在异步线程中传播
引入@EnableTraceable会注入名为"taskExecutor"的ThreadPoolExecutor
```
    @Resource
    @Qualifier(("taskExecutor"))
    private Executor executor;
    
    public void foo() {
         executor.execute(()-> log.info("hello cloud-base"));
    }
```
TID/UID将被传播到异步线程中去，logback日志：
```
14:10:01.403 INFO [ task-pool-1] c.x.c.s.c.SampleController 12347-123 hello cloud-base
```

#### TID/UID在远程调用中传播
详见：声明式Feign客户端(Declarative Feign Client)，TraceHeaderPropagator拦截器

#### @PerformanceWatch注解
被@PerformanceWatch注解标注的方法，如果响应时间较慢(默认500毫秒)，将被记录在logback日志中:
```
14:10:13.664 WARN [http-nio-1008-exec-3] c.x.c.t.PerformanceWatchAspect 12347-123 Slow API:SampleRemoteService.echo,arg:["123"],took:2006ms
```

### 前端的后端(Backend for Front-end)JWT认证

#### @EnableJwt注解
在项目中引入@EnableJwt注解，通常加在启动类上

#### @CloudApplication注解
@CloudApplication注解也可以用来快速开启cloud base功能,
@CloudApplication注解等价于如下注解:
<li>@EnableJwt
<li>@EnableLock
<li>@EnableRetryableEvent
<li>@EnableTraceable

#### @JwtSecured注解
@RestController中的方法或者类上使用@JwtSecured注解
```
   @JwtSecured
   public ResultDto<String> foo() throws Exception {
        //do sth here
        return ResultDto.ok("hello cloud-base");
   }
```

#### Jwt证书配置
```
cloud:
  jwt:
    app-id: 1008
    secret: sample.ap.jwt.secret(replce me with a real jwt secret)
    ttl-in-hours: 24
    multi-login-check: true
```

### 后端的后端(Backend for Backend)OAuth1认证

#### @EnableOAuth1注解
在项目中引入@EnableOAuth1注解，通常加在启动类上

#### @OAuth1Secured注解
@RestController中的方法或者类上使用@OAuth1Secured注解
```
   @OAuth1Secured
   @GetMapping("/foo")
   public ResultDto<String> foo(@RequestParam String userName) throws Exception {
        //do sth here
        return ResultDto.ok(String.format("hello %s from cloud-base", userName));
   }
```

#### OAuth1证书配置
```
cloud:
  server:
    oauth:
      sample_service:
        app-id: 1008
        key: oauth1_consumer_key_of_sample_service
        secret: oauth1_consumer_secret_of_sample_service
```

### 声明式Feign客户端(Declarative Feign Client) 
首先引入common-clients maven依赖

#### @EnableFeignClient注解
在项目中引入@EnableFeignClient注解，通常加在启动类上

#### OAuth1Interceptor拦截器
通过OAuth1Interceptor拦截器调用@OAuth1Secred保护的后端的后端服务(Backend for Backend)，例如：
```
@FeignClient(name = "sample-oauth", url = "${cloud.client.oauth.foo.url}"
        ,configuration = {OAuth1Interceptor.class}
)
@GetMapping("/foo")
ResultDto<String> foo(@RequestParam String userName);
```
在Spring配置文件中配置相应的oauth1证书：
```
cloud:
  client:
    oauth:
      foo:
        url: http://localhost:1008/foo
        key: oauth1_consumer_key_of_sample_service
        secret: oauth1_consumer_secret_of_sample_service
```

#### OutboundLogger拦截器
OutboundLogger是个全局拦截器，所有的声明式Feign客户端发出的请求/响应/异常都会被记录在logback日志中

#### TraceHeaderPropagator拦截器
TraceHeaderPropagator是个全局拦截器，所有的声明式Feign客户端发出的请求头中会增加trace-id
