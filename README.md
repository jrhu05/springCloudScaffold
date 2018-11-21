# springCloudScaffold
springCloud脚手架工程，提供多个开箱即用的最简实例，子项目均通过实际运行测试。

包括：

1、[springCloudConfiure](https://github.com/jrhu05/springCloudScaffold/tree/master/springCloudConfigure)

配置中心server（以git为数据源）

2、[springCloudConfigureClient](https://github.com/jrhu05/springCloudScaffold/tree/master/springCloudConfigureClient)

配置中心Client端

3、[springCloudConsumer](https://github.com/jrhu05/springCloudScaffold/tree/master/springCloudConsumer)

注册到Eureka的消费者（使用feign进行远程调用、使用hystrix进行熔断、启用hystirx监控面板）

4、[springCloudEureka](https://github.com/jrhu05/springCloudScaffold/tree/master/springCloudEureka)

注册中心Eureka及其双活构建

5、[springCloudProducer](https://github.com/jrhu05/springCloudScaffold/tree/master/springCloudProducer)

注册到Eureka的服务提供者（启用zipKin分布式链路跟踪支持）

6、[springCloudZipKin](https://github.com/jrhu05/springCloudScaffold/tree/master/springCloudZipkin)

分布式链路跟踪中心

7、[springCloudZuul](https://github.com/jrhu05/springCloudScaffold/tree/master/springCloudZuul)

服务网关Zuul（启用token校验和路由熔断）