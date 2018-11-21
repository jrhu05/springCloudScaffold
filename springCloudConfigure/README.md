# springCloudConfigure
## 作用

一个配置中心提供的核心功能应该有

- 提供服务端和客户端支持
- 集中管理各环境的配置文件
- 配置文件修改之后，可以快速的生效
- 可以进行版本管理
- 支持大的并发查询
- 支持各种语言

Spring Cloud Config项目是一个解决分布式系统的配置管理方案。它包含了Client和Server两个部分，server提供配置文件的存储、以接口的形式将配置文件的内容提供出去，client通过接口获取数据、并依据此数据初始化自己的应用。Spring cloud使用git或svn存放配置文件，默认情况下使用git。

## 配置数据源准备

首先在github上面创建了一个文件夹config-repo用来存放配置文件，为了模拟生产环境，我们创建以下三个配置文件：

```
// 开发环境
jerry-config-dev.properties
// 测试环境
jerry-config-test.properties
// 生产环境
jerry-config-pro.properties
```

每个配置文件中都写一个属性neo.hello,属性值分别是 hello im dev/test/pro 。

组内同事参见内部gitlab对应repo：http://git.hytcshare.com/root/config-repo

## server端配置

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
            <artifactId>spring-cloud-config-server</artifactId>
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

```
server:
  port: 8001
spring:
  application:
    name: spring-cloud-config-server
  cloud:
    config:
      server:
        git:
          uri: http://10.1.3.26/root/config-repo.git # 配置git仓库的地址，注意以.git结尾
          search-paths: config-repo #git仓库地址下的相对地址，可以配置多个，用,分割。
          username: root #git仓库的账号
          password: Passw0rd123!@#  #git仓库的密码
eureka:
  client:
    service-url:
      defaultZone: http://cloud.hytcshare.com:8002/eureka/ #注册中心Eureka地址
```

### 3、启动类

启动类添加`@EnableConfigServer`，激活对配置中心的支持

```
@EnableConfigServer
@EnableDiscoveryClient//注册到eureka
@SpringBootApplication
public class ConfigServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ConfigServerApplication.class, args);
	}
}
```

到此server端相关配置已经完成

### 4、测试

启动项目后访问http://localhost:8001/jerry-config/dev

仓库中的配置文件会被转换成web接口，访问可以参照以下的规则：

- /{application}/{profile}[/{label}]
- /{application}-{profile}.yml
- /{label}/{application}-{profile}.yml
- /{application}-{profile}.properties
- /{label}/{application}-{profile}.properties

以jerry-config-dev.properties为例子，它的application是jerry-config，profile是dev。client会根据填写的参数来选择读取对应的配置。

## client端配置

参见 [springCloudConfigureClient](https://github.com/jrhu05/springCloudScaffold/tree/master/springCloudConfigureClient)

## 扩展

[配置中心svn示例和refresh](http://www.ityouknow.com/springcloud/2017/05/23/springcloud-config-svn-refresh.html)

[配置中心服务化和高可用](http://www.ityouknow.com/springcloud/2017/05/25/springcloud-config-eureka.html)

[配置中心和消息总线（配置中心终结版）](http://www.ityouknow.com/springcloud/2017/05/26/springcloud-config-eureka-bus.html)

## 声明

以上所有内容均部分或全部来自ityouknow博客，仅供个人备忘归档和快速上手使用，进行了部分删减和错误修正（主要修复了错误的maven依赖），并进行了针对组内部署配置的个性化定制。

ityouknow系列文章列表：http://www.ityouknow.com/spring-cloud.html