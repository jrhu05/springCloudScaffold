# springCloudEureka
## 作用

Eureka是Netflix开源的一款提供服务注册和发现的产品，它提供了完整的Service Registry和Service Discovery实现。也是springcloud体系中最重要最核心的组件之一。服务中心又称注册中心，管理各种服务功能包括服务的注册、发现、熔断、负载、降级等，比如dubbo admin后台的各种功能。

### Eureka Server端配置

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
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
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

Eureka双活高可用配置，以10.1.3.26、10.1.3.27为例

.27的配置文件application-peer1.properties如下：

```
spring.application.name=spring-cloud-eureka
server.port=8002 #测试环境服务器的8001端口已经被configureServer占用，所以使用8002端口
eureka.instance.ip-address=10.1.3.27 #我是27哦
eureka.instance.prefer-ip-address=true
eureka.server.enableSelfPreservation=false
eureka.client.service-url.defaultZone=http://10.1.3.26:8001/eureka/ #我要注册到26
```

.26的配置文件application-peer2.properties如下：

```
spring.application.name=spring-cloud-eureka
server.port=8001
eureka.instance.ip-address=10.1.3.26 #我是26哦
eureka.instance.prefer-ip-address=true
eureka.server.enableSelfPreservation=false
eureka.client.service-url.defaultZone=http://10.1.3.27:8002/eureka/ #我要注册到27
```

启动的时候java -jar spring-cloud-eureka-0.0.1-SNAPSHOT.jar --spring.profiles.active=peer1 这样既可启用peer1或peer2的配置文件

### 3、启动类

```
@SpringBootApplication
@EnableEurekaServer
public class SpringCloudEurekaApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudEurekaApplication.class, args);
    }
}

```

## 测试

访问对应的端口进行测试。

## producer端配置

参见 [springCloudProducer](https://github.com/jrhu05/springCloudScaffold/tree/master/springCloudProducer)

## consumer端配置

参见[springCloudConsumer](https://github.com/jrhu05/springCloudScaffold/tree/master/springCloudConsumer)

## 扩展

[注册中心 Consul 使用详解](http://www.ityouknow.com/springcloud/2018/07/20/spring-cloud-consul.html)

## 声明

以上所有内容均部分或全部来自ityouknow博客，仅供个人备忘归档和快速上手使用，进行了部分删减和错误修正（主要修复了错误的maven依赖），并进行了针对组内部署配置的个性化定制。

ityouknow系列文章列表：http://www.ityouknow.com/spring-cloud.html