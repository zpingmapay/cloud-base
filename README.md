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

#### Java

```java
// 1. redission distributed locks
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

```java
// 2. cache
