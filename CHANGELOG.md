v1.0.2
- 重复时间窗口(recurring time window)
- 添加对象信息脱敏模块(desensitize)

v1.0.1
- 枚举验证(Enum Validation)
- Lombok-plus依赖

v1.0.0
- 缓存(Cache)
- 声明式锁(Declarative Lock)
- 分布式可重试事件(Distributed Retryable Event)
- 可跟踪日志(Traceable Log)
- 前端的后端(Backend for Front-end)JWT认证
- 后端的后端(Backend for Backend)OAuth1认证
- 声明式Feign客户端(Declarative Feign Client)

mvn命令
- mvn versions:set -DnewVersion=1.0.0 -DprocessAllModules
- mvn test
- mvn package -Dmaven.test.skip=true
- mvn deploy -Dmaven.test.skip=true 
