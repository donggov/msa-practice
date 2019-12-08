# Spring Cloud Bus
Spring Cloud Bus는 분산 시스템에 존재하는 서비스들과 메시지 브로커와의 연결을 지원해준다.
[Spring Cloud Config](https://github.com/donggov/msa-practice/blob/master/docs/SpringCloudConfig.md)를 통해 설정값을 반영할 경우 서비스가 N개라면 /refresh를 N번 호출해야하는 단점을 `Spring Cloud Bus`를 사용하요 1번만에 전파할 수 있다.  

## Spring Cloud Config Server 설정
RabbitMQ 사용한 예제이다. RabbitMQ가 설치되어 있어야한다.

build.gradle
````gradle
implementation 'org.springframework.cloud:spring-cloud-starter-bus-amqp'
implementation 'org.springframework.cloud:spring-cloud-config-monitor'
````

`RabbitMQ` 설정 추가
````yaml
spring:
  application:
    name: config-server
  cloud:
    config:
      server:
        git:
          uri: https://github.com/donggov/spring-config-repository
  rabbitmq:
    host: 127.0.0.1
    port: 5672
    username: guest
    password: guest
````

`actuator` 설정 추가. 테스트 목적이니까 actuator `include`를 `*`로 하였다.
````yaml
management:
  endpoints:
    web:
      exposure:
        include: "*" # 전체 endpoint 노출
#        include: ['bus-refresh'] # 노출할 endpoint 지정
````

## Client 설정
build.gradle
````gradle
implementation 'org.springframework.cloud:spring-cloud-starter-bus-amqp'
````

`RabbitMQ` 설정 추가
````yaml
spring:
  application:
    name: display
  cloud:
    config:
      uri: http://localhost:8888
      fail-fast: false #Config Server와 연결이 되지 않아도 실행
  rabbitmq:
    host: 127.0.0.1
    port: 5672
    username: guest
    password: guest
````

## 테스트
RabbitMQ stop / install / start 명령어
````text
rabbitmq-service.bat stop
rabbitmq-service.bat install
rabbitmq-service.bat start
````

RabbitMQ 기본 접속 정보
````text
URL : http://localhost:15672
Username : guest
Password : guest
````

Display Client 빌드 후, 9000, 9001 포트로 각각 실행
````text
java -jar -Dserver.port=9000 display-1.0-SNAPSHOT.jar
java -jar -Dserver.port=9001 display-1.0-SNAPSHOT.jar
````

메시지 확인
> http://localhost:9000
````text
Local, Welcome to Display!! Updated ^o^
````

> http://localhost:9001
````text
Local, Welcome to Display!! Updated ^o^
````

`display-default.yml` 파일을 아래처럼 수정
````yaml
application:
  env: "Local"
greeting: "Welcome to Display!! Updated 2 ^o^"
````

Client의 /actuator/bus-refresh 호출
> POST `http://localhost:8888/actuator/bus-refresh`

Spring Config Server log
````text
2019-12-08 20:26:02.562  INFO 11600 --- [nio-8888-exec-4] trationDelegate$BeanPostProcessorChecker : Bean 'org.springframework.cloud.autoconfigure.ConfigurationPropertiesRebinderAutoConfiguration' of type [org.springframework.cloud.autoconfigure.ConfigurationPropertiesRebinderAutoConfiguration$$EnhancerBySpringCGLIB$$c50f118b] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying)
2019-12-08 20:26:05.335  INFO 11600 --- [nio-8888-exec-4] o.s.boot.SpringApplication               : No active profile set, falling back to default profiles: default
2019-12-08 20:26:05.357  INFO 11600 --- [nio-8888-exec-4] o.s.boot.SpringApplication               : Started application in 4.392 seconds (JVM running for 921.241)
2019-12-08 20:26:05.786  INFO 11600 --- [nio-8888-exec-4] o.s.cloud.bus.event.RefreshListener      : Received remote refresh request. Keys refreshed []
````

Client log
````text
2019-12-08 20:26:03.258  INFO 3612 --- [bi3_nc1BCynEw-1] trationDelegate$BeanPostProcessorChecker : Bean 'org.springframework.cloud.autoconfigure.ConfigurationPropertiesRebinderAutoConfiguration' of type [org.springframework.cloud.autoconfigure.ConfigurationPropertiesRebinderAutoConfiguration$$EnhancerBySpringCGLIB$$ecc658fb] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying)
2019-12-08 20:26:05.621  INFO 3612 --- [bi3_nc1BCynEw-1] c.c.c.ConfigServicePropertySourceLocator : Fetching config from server at : http://localhost:8888
2019-12-08 20:26:08.746  INFO 3612 --- [bi3_nc1BCynEw-1] c.c.c.ConfigServicePropertySourceLocator : Located environment: name=display, profiles=[default], label=null, version=cdfce2fb5bca8bbe76412926c754785e696995ef, state=null
2019-12-08 20:26:08.746  INFO 3612 --- [bi3_nc1BCynEw-1] b.c.PropertySourceBootstrapConfiguration : Located property source: CompositePropertySource {name='configService', propertySources=[MapPropertySource {name='configClient'}, MapPropertySource {name='https://github.com/donggov/spring-config-repository/display-default.yml'}]}
2019-12-08 20:26:08.748  INFO 3612 --- [bi3_nc1BCynEw-1] o.s.boot.SpringApplication               : No active profile set, falling back to default profiles: default
2019-12-08 20:26:08.760  INFO 3612 --- [bi3_nc1BCynEw-1] o.s.boot.SpringApplication               : Started application in 7.77 seconds (JVM running for 825.281)
2019-12-08 20:26:08.922  INFO 3612 --- [bi3_nc1BCynEw-1] com.netflix.discovery.DiscoveryClient    : Shutting down DiscoveryClient ...
2019-12-08 20:26:11.924  INFO 3612 --- [bi3_nc1BCynEw-1] com.netflix.discovery.DiscoveryClient    : Unregistering ...
2019-12-08 20:26:11.929  INFO 3612 --- [bi3_nc1BCynEw-1] com.netflix.discovery.DiscoveryClient    : DiscoveryClient_DISPLAY/PANSUK:display:9000 - deregister  status: 200
2019-12-08 20:26:11.944  INFO 3612 --- [bi3_nc1BCynEw-1] com.netflix.discovery.DiscoveryClient    : Completed shut down of DiscoveryClient
2019-12-08 20:26:11.945  INFO 3612 --- [bi3_nc1BCynEw-1] o.s.c.n.eureka.InstanceInfoFactory       : Setting initial instance status as: STARTING
2019-12-08 20:26:11.953  INFO 3612 --- [bi3_nc1BCynEw-1] com.netflix.discovery.DiscoveryClient    : Initializing Eureka in region us-east-1
...
````

Endpoint 호출
> http://localhost:9000
````text
Local, Welcome to Display!! Updated 2 ^o^
````

> http://localhost:9001
````text
Local, Welcome to Display!! Updated 2 ^o^
````