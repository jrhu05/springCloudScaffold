# springCloudZuul
## 作用

在微服务架构中，后端服务往往不直接开放给调用端，而是通过一个API网关根据请求的url，路由到相应的服务。当添加API网关后，在第三方调用端和服务提供方之间就创建了一面墙，这面墙直接与调用方通信进行权限控制，后将请求均衡分发给后台服务端。

在微服务架构模式下后端服务的实例数一般是动态的，对于客户端而言很难发现动态改变的服务实例的访问地址信息。因此在基于微服务的项目中为了简化前端的调用逻辑，通常会引入API Gateway作为轻量级网关，同时API Gateway中也会实现相关的认证逻辑从而简化内部服务之间相互调用的复杂度。

Spring Cloud Zuul路由是微服务架构的不可或缺的一部分，提供动态路由，监控，弹性，安全等的边缘服务。Zuul是Netflix出品的一个基于JVM路由和服务端的负载均衡器。

### Zuul配置

### 1、添加依赖

```
<properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <java.version>1.8</java.version>
        <spring-cloud.version>Greenwich.M1</spring-cloud.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-zuul</artifactId>
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
spring.application.name=gateway-service-zuul
server.port=8888

#手工指定路由(不配置的话会自动进行路由)
#zuul.routes.api-a.path=/producer/**
#zuul.routes.api-a.service-id=spring-cloud-producer

eureka.client.service-url.defaultZone=http://10.1.3.27:8002/eureka/

spring.zipkin.base-url=http://10.1.3.27:9099
spring.sleuth.sampler.probability=1.0#sleuth的采样率设为1，即所有的请求均进行采样
```

### 3、启动类

```java
@EnableZuulProxy
@SpringBootApplication
public class SpringCloudZuulApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringCloudZuulApplication.class, args);
    }

    //用于token校验
    @Bean
    public TokenFilter tokenFilter(){
        return new TokenFilter();
    }
}
```

### 4、自定义filter实现token校验

````java
public class TokenFilter extends ZuulFilter {

    private final Logger logger= LoggerFactory.getLogger(TokenFilter.class);

    @Override
    public String filterType() {
        return "pre"; // 可以在请求被路由之前调用
    }

    @Override
    public int filterOrder() {
        return 0; // filter执行顺序，通过数字指定 ,优先级为0，数字越大，优先级越低
    }

    @Override
    public boolean shouldFilter() {
        return true;// 是否执行该过滤器，此处为true，说明需要过滤
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext ctx=RequestContext.getCurrentContext();
        HttpServletRequest request=ctx.getRequest();
        logger.info("--->>> TokenFilter{},{}",request.getMethod(),request.getRequestURL().toString());
        String token=request.getParameter("token");//获取请求参数
        if (StringUtils.isNotBlank(token)){
            //对请求进行路由
            ctx.setSendZuulResponse(true);
            ctx.setResponseStatusCode(200);
            ctx.set("isSuccess",true);
            return null;
        }else {
            //不对请求进行路由
            ctx.setSendZuulResponse(false);
            ctx.setResponseStatusCode(400);
            ctx.setResponseBody("token is empty");
            ctx.set("isSuccess",false);
            return null;
        }
    }
}
````

### 5、路由熔断

````java
/**
 * producer服务路由熔断
 */
@Component
public class ProducerFallback implements FallbackProvider {

    private final Logger logger= LoggerFactory.getLogger(ProducerFallback.class);

    @Override
    public String getRoute() {
        return "spring-cloud-producer";
    }

    @Override
    public ClientHttpResponse fallbackResponse(String route, Throwable cause) {
        if (cause!=null&&cause.getCause()!=null){
            String reason=cause.getCause().getMessage();
            logger.info("Exception{}",reason);
        }
        return fallbackResponse();
    }

    public ClientHttpResponse fallbackResponse(){
        return new ClientHttpResponse() {
            @Override
            public HttpStatus getStatusCode() throws IOException {
                return HttpStatus.OK;
            }

            @Override
            public int getRawStatusCode() throws IOException {
                return 200;
            }

            @Override
            public String getStatusText() throws IOException {
                return "OK";
            }

            @Override
            public void close() {

            }

            @Override
            public InputStream getBody() throws IOException {
                return new ByteArrayInputStream("This service is unavailable".getBytes());
            }

            @Override
            public HttpHeaders getHeaders() {
                HttpHeaders httpHeaders=new HttpHeaders();
                httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                return httpHeaders;
            }
        };
    }

}
````

https://github.com/jrhu05/springCloudScaffold/tree/master/springCloudZuul)

## producer端配置

参见[springCloudProducer](https://github.com/jrhu05/springCloudScaffold/tree/master/springCloudProducer)

## 扩展

[服务网关zuul初级篇](http://www.ityouknow.com/springcloud/2017/06/01/gateway-service-zuul.html)

[服务网关Zuul高级篇](http://www.ityouknow.com/springcloud/2018/01/20/spring-cloud-zuul.html)

## 声明

以上所有内容均部分或全部来自ityouknow博客，仅供个人备忘归档和快速上手使用，进行了部分删减和错误修正（主要修复了错误的maven依赖），并进行了针对组内部署配置的个性化定制。

ityouknow系列文章列表：http://www.ityouknow.com/spring-cloud.html