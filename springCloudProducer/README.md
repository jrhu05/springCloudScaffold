# springCloudProducer
## 作用

服务提供者生产服务并注册到服务中心中，假设服务提供者有一个hello方法，可以根据传入的参数，提供输出“hello xxx，this is first messge”的服务。

### producer端配置

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
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

		<!-- 增加对分布式链路跟踪的支持 -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-zipkin</artifactId>
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

```
spring.application.name=spring-cloud-producer
server.port=9001
eureka.client.service-url.defaultZone=http://10.1.3.27:8002/eureka/
spring.zipkin.base-url=http://10.1.3.27:9099
spring.sleuth.sampler.probability=1.0 #sleuth的采样率设为1，即所有的请求均进行采样
```

### 3、启动类

```java
@SpringBootApplication
@EnableDiscoveryClient
public class SpringCloudProducerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudProducerApplication.class, args);
    }
}

```

### 4、controller

普通的controller写法

````java
@RestController
public class HelloController {
    @RequestMapping("/hello")
    public String index (@RequestParam String name){
        return "hello "+name+", this is first message! server1!";
    }
}

````

## server端配置

参见 [springCloudEureka](https://github.com/jrhu05/springCloudScaffold/tree/master/springCloudEureka)

## consumer端配置

参见[springCloudConsumer](https://github.com/jrhu05/springCloudScaffold/tree/master/springCloudConsumer)

## 扩展

[使用Spring Cloud Sleuth和Zipkin进行分布式链路跟踪](http://www.ityouknow.com/springcloud/2018/02/02/spring-cloud-sleuth-zipkin.html)

## 声明

以上所有内容均部分或全部来自ityouknow博客，仅供个人备忘归档和快速上手使用，进行了部分删减和错误修正（主要修复了错误的maven依赖），并进行了针对组内部署配置的个性化定制。

ityouknow系列文章列表：http://www.ityouknow.com/spring-cloud.html