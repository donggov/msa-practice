# Zuul (API Gateway)

## API Gateway
- 클라이언트와 백엔드 서버 사이의 출입문
- 라우팅(라우팅, 필터링, API변환, 클라이언트 어댑터 API, 서비스 프록시)
- 횡단 관심사 cross-service concerns
  - 보안, 인증
  - 일정량 이상의 요청 제한(rate limiting)
  - metering

## Zuul
Zuul에는 Hystrix, Ribbon가 내장되어 있다. (Eureka Client는 직접 추가하여 사용) 
1. Zuul의 모든 API 요청은 HystrixCommand로 구성되어 전달된다.
    * 하나의 서버군이 장애를 일으켜도 다른 서버군의 서비스에는 영향이 없다.
    * CircuitBreaker / ThreadPool의 다양한 속성을 통해 서비스 별 속성에 맞는 설정 가능
2. Ribbon을 통해 로드밸런싱을 수행한다.
3. Eureka Client를 사용하여 주어진 URL의 호출을 전달할 서비스(서버)를 찾는다.
4. Eureka + Ribbon에 의해서 결정된 서비스로 HTTP 요청
    * Apache Http Client가 기본. OKHttp Client 사용 가능
5. 선택된 첫 서버로 호출이 실패할 경우, Ribbon에 의해서 자동으로 Retry 수행

## 기본 설정
build.gradle
````gradle
implementation 'org.springframework.cloud:spring-cloud-starter-netflix-zuul'
implementation 'org.springframework.cloud:spring-cloud-starter-netflix-eureka-client'
implementation 'org.springframework.retry:spring-retry'
````

`@EnableZuulProxy`, `@EnableDiscoveryClient` 추가
````java
@SpringBootApplication
@EnableZuulProxy
@EnableDiscoveryClient
public class ZuulApplication {
````

yml 설정
````yaml
server:
  port: 9999

spring:
  application:
    name: zuul

zuul:
  routes:
    product:
      path: /products/**
      serviceId: product
      stripPrefix: false # false인 경우 uri를 모두 보냄 (/products/{...}), true인 경우 matching된 값을 제외하고 보낸다(/{...})
    display:
      path: /display/**
      serviceId: display
      stripPrefix: false

eureka:
  instance:
    non-secure-port: ${server.port}
    prefer-ip-address: true # OS에서 제공하는 hostname 대신 자신의 ip address를 사용 (local 용)
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka
````

# Hystrix, Ribbon 설정 추가
Hystrix 설정
````yaml
hystrix:
  command:
    default:
      execution:
        isolation:
          thread:
            timeoutInMilliseconds: 1000
    product:
      execution:
        isolation:
          thread:
            timeoutInMilliseconds: 5000 # Ribbon의 각 timeout 보다 커야 정상 동작함
````

Ribbon 설정
````yaml
 product:
   ribbon:
     MaxAutoRetriesNextServer: 1
     ReadTimeout: 3000
     ConnectTimeout: 1000
     MaxTotalConnections: 300
     MaxConnectionsPerHost: 100
````
