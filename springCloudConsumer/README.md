# springCloudConsumer
## 作用

注册到Eureka的消费者，通过feign进行远程调用，调用producer提供的服务，使用hystrix进行熔断，同时启用了hystirx监控面板。

## consumer端配置

### 1、添加依赖

```
 <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <java.version>1.8</java.version>
        <spring-cloud.version>Finchley.SR1</spring-cloud.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-hystrix</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-hystrix-dashboard</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

    </dependencies>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

```

### 2、配置文件

application.properties如下：

```
spring.application.name=spring-cloud-consumer
server.port=9002
eureka.client.service-url.defaultZone=http://10.1.3.27:8002/eureka/
feign.hystrix.enabled=true
#解决hystrix.stream 404
management.endpoints.web.exposure.include=hystrix.stream
management.endpoints.web.base-path=/
```

### 3、启动类

```
@SpringBootApplication
@EnableDiscoveryClient//启用服务注册与发现
@EnableFeignClients//启用feign进行远程调用
@EnableHystrixDashboard//启用hystrix监控面板
@EnableCircuitBreaker
public class SpringCloudConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudConsumerApplication.class, args);
    }
}

```

Feign是一个声明式Web Service客户端。使用Feign能让编写Web Service客户端更加简单, 它的使用方法是定义一个接口，然后在上面添加注解，同时也支持JAX-RS标准的注解。Feign也支持可拔插式的编码器和解码器。Spring Cloud对Feign进行了封装，使其支持了Spring MVC标准注解和HttpMessageConverters。Feign可以与Eureka和Ribbon组合使用以支持负载均衡。

### 4、feign调用实现

```java
//fallback即为熔断扩展所需类
@FeignClient(name="spring-cloud-producer",fallback = HelloRemoteHystrix.class)
public interface HelloRemote {
    @RequestMapping(value="/hello")
    public String hello(@RequestParam(value="name") String name);
}
```

- name:远程服务名，及spring.application.name配置的名称

此类中的方法和远程服务中contoller中的方法名和参数需保持一致。

### 5、创建熔断hystrix回调

````java
/**
 * 熔断器备用类
 */
@Component
public class HelloRemoteHystrix implements HelloRemote{
    @Override
    public String hello(@RequestParam(value="name") String name) {
        return "hello,"+name+", this message is from hystrix!";
    }
}
````

当上述远端接口调用失败的时候，系统返回相应提示。

### 6、web层调用远程服务

将HelloRemote注入到controller层，像普通方法一样去调用即可。

```java
@RestController
public class ConsumerController {
    @Autowired
    HelloRemote helloRemote;
    @RequestMapping("/hello/{name}")
    public String index(@PathVariable("name") String name){
        return helloRemote.hello(name);
    }
}

```

到此，最简单的一个服务注册与调用的例子就完成了。

## 测试

访问对应的controller进行测试。

## server端配置

参见[springCloudEureka](https://github.com/jrhu05/springCloudScaffold/tree/master/springCloudEureka)

## producer端配置

参见 [springCloudProducer](https://github.com/jrhu05/springCloudScaffold/tree/master/springCloudProducer)

## 扩展

[熔断器Hystrix](http://www.ityouknow.com/springcloud/2017/05/16/springcloud-hystrix.html)

[熔断监控Hystrix Dashboard和Turbine](http://www.ityouknow.com/springcloud/2017/05/18/hystrix-dashboard-turbine.html)

## 声明

以上所有内容均部分或全部来自ityouknow博客，仅供个人备忘归档和快速上手使用，进行了部分删减和错误修正（主要修复了错误的maven依赖），并进行了针对组内部署配置的个性化定制。

ityouknow系列文章列表：http://www.ityouknow.com/spring-cloud.html