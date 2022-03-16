## Introduction

Dubbo Gateway是一个基于Java语言的Dubbo网关实现。基于Dubbo注册中心的元数据，通过将HTTP请求转换为Dubbo协议，泛化调用的方式返回请求结果。它具有如下的特性：

1. 使用简单，开箱即用，非常适用于一些接口测试场景；
2. 灵活可拓展。通过Interceptor的实现，可非常方便实现熔断、限流、路由、定制响应、用户授权等功能。系统也内置了部分Interceptor实现；
3. 支持仅调用声明为*对外开放的Dubbo服务*，安全保证。（基于Feature：https://github.com/apache/dubbo/pull/7660 ）

## Requirements

Dubbo 版本：Dubbo 2.7.14+  Dubbo 3.X即将支持

Java 版本：1.8+

## Guides

以Springboot项目方式，启动Dubbo Gateway网关：

#### 1、添加依赖

新建Springboot工程，添加Dubbo Gateway的核心依赖：

```xml
<dependency>
	<groupId>com.kalman03</groupId>
	<artifactId>gateway-core</artifactId>
	<version>1.1.0</version>
</dependency>
```
当然，还需要添加Dubbo的注册中心依赖，以Zookeeper为例：

```xml
<dependency>
	<groupId>org.apache.zookeeper</groupId>
	<artifactId>zookeeper</artifactId>
	<version>${zookeeper_version}</version>
	<exclusions>
		<exclusion>
			<groupId>log4j</groupId>
			<artifactId>log4j</artifactId>
		</exclusion>
		<exclusion>
			<groupId>org.slf4j</groupId>
			<artifactId>slf4j-log4j12</artifactId>
		</exclusion>
	</exclusions>
</dependency>
<dependency>
	<groupId>org.apache.curator</groupId>
	<artifactId>curator-framework</artifactId>
	<version>${curator_version}</version>
</dependency>
<dependency>
	<groupId>org.apache.curator</groupId>
	<artifactId>curator-recipes</artifactId>
	<version>${curator_version}</version>
</dependency>
```

#### 2、参数配置

```properties
gateway.netty.server.port=80
gateway.netty.server.host=127.0.0.1
gateway.netty.server.connect-timeout=3000
gateway.netty.business.thread-count=50
gateway.dubbo.registry.address=zookeeper://127.0.0.1:2181
gateway.dubbo.openservice=true
```

#### 3、启动服务

```java
@SpringBootApplication
@EnableAutoConfiguration
public class DubboGatewayTest {

	public static void main(String[] args) {
		try {
			SpringApplication.run(DubboGatewayTest.class, args);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

    //可选自定义拦截器
	@Component
	@Order(100)
	@InterceptorRule(routeRuleType = RouteRuleType.PATH, excludePatterns = { "/api/**" })
	class CustomInterceptor implements HandlerInterceptor {
		@Override
		public boolean preHandle(GatewayHttpRequest request, GatewayHttpResponse response) throws Exception {
			System.out.println("preHandle");
			return true;
		}
		@Override
		public void afterCompletion(GatewayHttpRequest request, GatewayHttpResponse response, Exception ex)
				throws Exception {
			System.out.println("afterCompletion");
		}
	}
}
```



#### 4、访问目标服务

系统内置了`PATH路由`与`MIX路由`，也同时支持自定义路由实现。下面演示如何通过不同的路由规则访问对应的服务。

- ###### PATH路由（推荐）

  PATH路由是系统内置默认的路由，也是系统推荐的路由规则，其拥有较多适用性（既可正常调用，也可以满足一些第三方系统的调用要求，比如支付消息回调等），路由规则如下

  **HTTP URL:**

  ```api
  http(s)://{domain}:{port}/{appName}/{interfaceName}/{method}/{group}/{version}
  ```

   **HTTP Body** (payload or form-data)：

  ```
  {
    "id": 23,
    "username": "testUser"
  }
  ```

- ###### MIX路由

  考虑到有些参数相对于固定不变，且暴露在URL中不尽友善，系统内置了一种MIX的路由规则，将部分路由参数以Header的参数形式进行传递。

  **HTTP URL:**
  
  ```api
  http(s)://{domain}:{port}/{interfaceName}/{method}
  ```

   **HTTP Header:**
  
  ```api
  x-app-name={appName}
  x-group={group}
  x-version={version}
  x-route-rule=mix
  ```
  
   **HTTP Body** (payload or form-data)：
  
  ```
  {
    "id": 23,
    "username": "testUser"
  }
  ```
  
- ###### CUSTOM自定义路由
  
  `CUSTOM路由`专为一些对`PATH路由`与`MIX路由`都不满意的开发者准备，只需要继承[AbstractRouteHandlerInterceptor](https://github.com/jingxiang/dubbo-gateway/blob/main/gateway-core/src/main/java/com/kalman03/gateway/interceptor/AbstractRouteHandlerInterceptor.java) 类，即可轻松实现自定义的路由规则。
  

## Param Transmission

用户端通用参数（诸如请求UA/Referer/IP等）以及Token用户信息等，需要传递到服务提供方。Dubbo Gateway与服务提供者之间内置的参数传递走Dubbo的RpcContext。

Dubbo网关内置了`gatewayConsumerFilter`与`gatewayProviderFilter`，可作为Dubbo服务提供者的默认Filter实现，通过该Filter可获取用户端请求的一些参数以及自定义拦截器封装的参数。当然，开发者也可自行在服务提供方读取RpcContext中的传递参数。

更多使用方式，参考：[gateway-samples](https://github.com/jingxiang/dubbo-gateway/tree/master/gateway-samples) 与 [gateway-samples-provider](https://github.com/jingxiang/dubbo-gateway/tree/master/gateway-samples-provider)

## Attention

Dubbo Gateway对Dubbo服务提供者提供的对外服务有一条要求：**只能有一个服务入参，且为对象类型（非普通Java数据类型）**。


## TODO

- Dubbo 3.x支持 （开发中）
- Dubbo可视化文档自动化系统（即将开放，将以独立项目）

欢迎贡献pull request......

## License

ImageHosting is released under the Apache License Version 2.0. See the [LICENSE](https://github.com/jingxiang/dubbo-gateway/blob/master/LICENSE) file for details.





