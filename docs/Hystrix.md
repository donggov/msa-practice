# Hystrix (Circuit Breaker)

## 설정
의존성 추가
````gradle
implementation 'org.springframework.cloud:spring-cloud-starter-netflix-hystrix'
````

`@EnableCircuitBreaker` 어노테이션 추가
````java
@SpringBootApplication
@EnableCircuitBreaker
public class DisplayApplication {
````

## Fallback 적용
Hystrix가 Exception을 탐지하여 Fallback을 실행한다.  
`@HystrixCommand` 어노테이션 추가 및 `fallbackMethod` 작성
````java
@HystrixCommand(fallbackMethod = "getProductFallback")
public String getProduct(long id) {
    return productRestTemplate.getForObject("/products/" + id, String.class);
}

public String getProductFallback(long id) {
    return "Sold out T.T";
}
````

## Fallback 원인 출력
Fallback 함수의 마지막 파라메터에 Throwable을 추가하면 Exception을 받을 수 있다. 
````java
public String getProductFallback(long id, Throwable t) {
    log.error("getProductFallback", t);
    return "Sold out T.T";
}
````

## Timeout
3,000ms 내에 응답이 안오면 fallback 처리 
````yaml
hystrix:
  command:
    default:
      execution:
        isolation:
          thread:
            timeoutInMilliseconds: 3000 # default 1000
````

## Circuit Open
10초 동안 1개 이상의 호출이 발생했을 때 50% 이상의 호출에서 에러가 발생하면 Circuit Open  
````yaml
hystrix:
  command:
    default:
      execution:
        isolation:
          thread:
            timeoutInMilliseconds: 3000 # default 1000
      circuitBreaker:
        requestVolumeThreshold: 1     # Minimum number of requests in calculate circuit breaker's health. default 20
        errorThresholdPercentage: 50  # Error percentage to open circuit. default 50
````

Circuit Open 되었을때 로그
````
java.lang.RuntimeException: Hystrix circuit short-circuited and is OPEN
	at com.netflix.hystrix.AbstractCommand.handleShortCircuitViaFallback(AbstractCommand.java:979) ~[hystrix-core-1.5.18.jar:1.5.18]
	at com.netflix.hystrix.AbstractCommand.applyHystrixSemantics(AbstractCommand.java:557) ~[hystrix-core-1.5.18.jar:1.5.18]
	at com.netflix.hystrix.AbstractCommand.access$200(AbstractCommand.java:60) ~[hystrix-core-1.5.18.jar:1.5.18]
````

## 참고
- https://github.com/Netflix/Hystrix/wiki

