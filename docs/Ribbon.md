# Ribbon
- Client Load Balancer  
- Client(API Caller)에 탑재되는 S/W 모듈
- 주어진 서버 목록에 대해서 Load Balancing 수행
- H/W 필요없이 S/W로만 Load Balancing 가능
- Retry 기능 내장
- 기본 설정은 Round Robin

## 설정
의존성 추가
````gradle
implementation 'org.springframework.cloud:spring-cloud-starter-netflix-ribbon'
````

Ribbon 설정
````yaml
product-api:
  ribbon:
    listOfServers: http://localhost:9100
````

RestTemplate 빈에 `@LoadBalanced` 어노테이션 추가 및 주소를 ribbon 값으로 변경
````java
@Bean
@LoadBalanced
public RestTemplate productRestTemplate() {
    // return restTemplateBuilder.rootUri("http://localhost:9100")
    return restTemplateBuilder.rootUri("http://product-api")
            .setConnectTimeout(Duration.ofMinutes(3))
            .build();
}
````

## Retry
`listOfServers` 추가 및 Retry 설정  
```yaml
product-api:
  ribbon:
    listOfServers: http://localhost:9100,http://localhost:9101
    MaxAutoRetries: 0
    MaxAutoRetriesNextServer: 1 # 실패시 다른 서버로 재시도하는 횟수
```
- 단, Retry를 시도하더라도 HystrixTimeout이 발생하면 즉시 에러가 리턴 될 수 있음 (Hystrix로 Ribbon을 감싸서 호출한 상태이기 때문)
- Retry를 끄거나, 재시도 횟수를 0으로 하여도 해당 서버로의 호출이 항상 동일한 비율로 실패하지 않는다. (실패한 서버로의 호출은 특정 시간동안 skip 되고 그 간격은 조정된다. BackOff)