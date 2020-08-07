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
    <!--feign client-->
    <dependency>
       <groupId>com.xyz</groupId>
       <artifactId>common-clients</artifactId>
          <version>1.1-SNAPSHOT</version>
    </dependency>  
     <!--cloud  base-->
    <dependency>
           <groupId>com.xyz</groupId>
           <artifactId>cloud-base</artifactId>
           <version>1.1-SNAPSHOT</version>
    </dependency>  

#### Gradle
    compile 'org.redisson:redisson:3.13.3'  

#### SBT
    libraryDependencies += "org.redisson" % "redisson" % "3.13.3"

#### Java

```java
// 1. Create config object
Config config = new Config();
config.useClusterServers()
       // use "rediss://" for SSL connection
      .addNodeAddress("redis://127.0.0.1:7181");

// or read config from file
config = Config.fromYAML(new File("config-file.yaml")); 
```

```java
// 2. Create Redisson instance

// Sync and Async API
RedissonClient redisson = Redisson.create(config);

// Reactive API
RedissonReactiveClient redissonReactive = Redisson.createReactive(config);

// RxJava2 API
RedissonRxClient redissonRx = Redisson.createRx(config);
```

```java
// 3. Get Redis based Map
RMap<MyKey, MyValue> map = redisson.getMap("myMap");

RMapReactive<MyKey, MyValue> mapReactive = redissonReactive.getMap("myMap");

RMapRx<MyKey, MyValue> mapRx = redissonRx.getMap("myMap");
```

```java
// 4. Get Redis based Lock
RLock lock = redisson.getLock("myLock");

RLockReactive lockReactive = redissonReactive.getLock("myLock");

RLockRx lockRx = redissonRx.getLock("myLock");
```

```java
// 4. Get Redis based ExecutorService
RExecutorService executor = redisson.getExecutorService("myExecutorService");

// over 50 Redis based Java objects and services ...

```

Consider __[Redisson PRO](https://redisson.pro)__ version for advanced features and support by SLA.

## Downloads
   
[Redisson 3.13.3](https://repository.sonatype.org/service/local/artifact/maven/redirect?r=central-proxy&g=org.redisson&a=redisson&v=3.13.3&e=jar),
[Redisson node 3.13.3](https://repository.sonatype.org/service/local/artifact/maven/redirect?r=central-proxy&g=org.redisson&a=redisson-all&v=3.13.3&e=jar)  

## FAQs

[Q: I saw a RedisTimeOutException, What does it mean? What shall I do? Can Redisson Team fix it?](https://github.com/redisson/redisson/wiki/16.-FAQ#q-i-saw-a-redistimeoutexception-what-does-it-mean-what-shall-i-do-can-redisson-team-fix-it)

[Q: When do I need to shut down a Redisson instance, at the end of each request or the end of the life of a thread?](https://github.com/redisson/redisson/wiki/16.-FAQ#q-when-do-i-need-to-shut-down-a-redisson-instance-at-the-end-of-each-request-or-the-end-of-the-life-of-a-thread)

[Q: In MapCache/SetCache/SpringCache/JCache, I have set an expiry time to an entry, why is it still in Redis when it should be disappeared?](https://github.com/redisson/redisson/wiki/16.-FAQ#q-in-mapcachesetcachespringcachejcache-i-have-set-an-expiry-time-to-an-entry-why-is-it-still-in-redis-when-it-should-be-disappeared)

[Q: How can I perform Pipelining/Transaction through Redisson?](https://github.com/redisson/redisson/wiki/16.-FAQ#q-how-can-i-perform-pipeliningtransaction-through-redisson)

[Q: Is Redisson thread safe? Can I share an instance of it between different threads?](https://github.com/redisson/redisson/wiki/16.-FAQ#q-is-redisson-thread-safe-can-i-share-an-instance-of-it-between-different-threads)

[Q: Can I use different encoder/decoders for different tasks?](https://github.com/redisson/redisson/wiki/16.-FAQ#q-can-i-use-different-encoderdecoders-for-different-tasks)

