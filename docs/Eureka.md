# Eureka (Dynamic Service Discovery)

* Service registry
  * 서비스 탐색, 등록
  * (단점) 코드 변경 필요 
* DiscoveryClient
  * Spring Cloud에서 서비스 레지스트리 사용 부분을 추상화(Interface)
  * Eureka, Consul, Zokkeper, etcd 등의 쿠현체가 존재
* Ribbon을 Eureka와 결합하여 서버 목록을 자동으로 관리할 수 있다.
 
서비스(서버) 시작 시 Eureka Server에 자신의 상태를 등록(UP)
```properties
eureka.client.register-with-eureka: true # default
```
Eureka Server에는 자신의 설정한 이름이 등록됨
```properties
spring.application.name
```
주기적 HeartBeat로 Eureka Server에 자신이 살아 있음을 알림
 ```properties
 eureka.instance.lease-renewal-interval-in-seconds: 30 # default
 ```
서비스(서버) 종료 시 Eureka Server에 자신의 상태 변경(DOWN) 또는 자신의 목록 삭제

## Eureka Server 설정
의존성 추가
````gradle
implementation 'org.springframework.cloud:spring-cloud-starter-netflix-eureka-server'
````
`@EnableEurekaServer` 어노테이션 추가
````java
@SpringBootApplication
@EnableEurekaServer
public class EurekaServer {
````

## 접속
기본 포트는 8761
> http://localhost:8761

## Eureka Client
Client는 Eureka Server로부터 다른 서비스 주소를 가져오는 기능, 자신의 주소를 등록하는 기능 모두 수행할 수 있다.

의존성 추가
 ````gradle
 implementation 'org.springframework.cloud:spring-cloud-starter-netflix-eureka-client'
 ````
 `@EnableEurekaClient` 어노테이션 추가
 ````java
@SpringBootApplication
@EnableCircuitBreaker
@EnableEurekaClient
public class DisplayApplication {
 ````
application.yml 수정
````yaml
eureka:
  instance:
    prefer-ip-address: true # OS에서 제공하는 hostname 대신 자신의 ip address를 사용 (local 용)
  client:
      service-url:
        defaultZone: http://localhost:8761/eureka
````

## RestTemplate에 Eureka 적용
`listOfServers` 삭제 또는 주석 처리  
해당 값이 없으면 유레카 서버에서 가져와서 사용한다.
````yaml
product:
  ribbon:
#    listOfServers: http://localhost:9100,http://localhost:9101
    MaxAutoRetries: 0
    MaxAutoRetriesNextServer: 1 # 실패시 다른 서버로 재시도하는 횟수
````