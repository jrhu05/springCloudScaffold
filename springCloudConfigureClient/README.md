# springCloudConfigureClient
## 作用

在业务项目中去获取server端的配置信息。

## client端配置

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
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-config</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <!-- 引入监控节点方便后续手动refresh -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
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

需要配置两个配置文件，application.properties和bootstrap.properties

application.properties如下：

```
spring.application.name=spring-cloud-configure-client
server.port=8002
management.endpoints.web.exposure.include=refresh
```

bootstrap.properties如下：

```
spring.cloud.config.name=jerry-config
spring.cloud.config.profile=dev
#spring.cloud.config.uri=http://cloud.hytcshare.com:8001/ #非高可用eureka时需要这么配置
spring.cloud.config.label=master
#Eureka注册中心配置
spring.cloud.config.discovery.enabled=true
spring.cloud.config.discovery.service-id=SPRING-CLOUD-CONFIG-SERVER
eureka.client.service-url.defaultZone=http://cloud.hytcshare.com:8002/eureka/
```

- spring.application.name：对应{application}部分
- spring.cloud.config.profile：对应{profile}部分
- spring.cloud.config.label：对应git的分支。如果配置中心使用的是本地存储，则该参数无用
- spring.cloud.config.uri：配置中心的具体地址
- spring.cloud.config.discovery.service-id：指定配置中心的service-id，便于扩展为高可用配置集群。

> 特别注意：上面这些与spring-cloud相关的属性必须配置在bootstrap.properties中，config部分内容才能被正确加载。因为config的相关配置会先于application.properties，而bootstrap.properties的加载也是先于application.properties。

### 3、启动类

启动类添加`@EnableDiscoveryClient`，激活对Eureka注册中心的支持

```
@SpringBootApplication
@EnableDiscoveryClient
public class SpringCloudConfigureClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudConfigureClientApplication.class, args);
    }
}
```

到此client端相关配置已经完成

### 4、web测试

使用`@Value`注解来获取server端参数的值

```java
@RestController
@RefreshScope//使用该注解的类，会在接到SpringCloud配置中心配置刷新的时候，自动将新的配置更新到该类对应的字段中。
public class ConfigureTestController {
    @Value("${jerry.hello}")
    private String test;

    @RequestMapping("test")
    public String from(){
        return this.test;
    }
}
```

启动项目后访问：`http://localhost:8002/test`返回配置好的字段信息，配置获取成功。修改git对应配置文件信息后，用POST方式访问`http://localhost:8002/refresh`就可以更新配置client的配置。可以使用github的hook机制自动调用，或者直接上springCloudBus。

## server端配置

参见 [springCloudConfiure](https://github.com/jrhu05/springCloudScaffold/tree/master/springCloudConfigure)

## 声明

以上所有内容均部分或全部来自ityouknow博客，仅供个人备忘归档和快速上手使用，进行了部分删减和错误修正（主要修复了错误的maven依赖），并进行了针对组内部署配置的个性化定制。

ityouknow系列文章列表：http://www.ityouknow.com/spring-cloud.html