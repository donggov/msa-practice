# Feign
* Interface 선언을 통해 자동으로 http Client를 생성
* RestTemplate은 concrete 클래스라 테스트하기 어렵다.
* 관심사의 분리
  * 서비스의 관심 - 다른 리소스, 외부 서비스 호출과 리턴값
  * 관심 X - 어떤 URL, 어떻게 파싱할 것인가
* Spring Cloud에서 Open-Feign 기반으로 wrapping한 것이 Spring Cloud Feign

## 설정
의존성 추가
````gradle
implementation 'org.springframework.cloud:spring-cloud-starter-openfeign'
````

`@EnableFeignClients` 추가
````java
@SpringBootApplication
@EnableCircuitBreaker
@EnableEurekaClient
@EnableFeignClients
public class DisplayApplication {
````

## Feign 클라이언트 사용하기

Interface 생성 및 annotation 작성
````java
@FeignClient(name = "product", url = "http://localhost:9100")
public interface ProductServiceProxy {

    @GetMapping("/{id}")
    public String getProduct(@PathVariable long id);

}
````

생성한 Feign Client 사용
````java
@RestController
@RequestMapping("/display")
public class DisplayController {

    private final DisplayService displayService;

    private final ProductServiceProxy productServiceProxy;

    public DisplayController(DisplayService displayService, ProductServiceProxy productServiceProxy) {
        this.displayService = displayService;
        this.productServiceProxy = productServiceProxy;
    }


    @GetMapping("/{id}")
    public String getDisplay(@PathVariable long id) {
//        return displayService.getProduct(id);
        return productServiceProxy.getProduct(id);
    }

}
```` 

url을 제거하면 유레카를 사용하게 된다. Feign Client는 내부적으로 Ribbon을 사용한다.  
````java
@FeignClient(name = "product")
public interface ProductServiceProxy {
````

Feign은 Ribbon, Eureka, Hystrix가 통합되어 있다.
* `@FeignClient`에 URL 명시 O
  * 순수 Feign Client로서만 동작
* `@FeignClient`에 URL 명시 X
  * Feign + Ribbon + Eureka 모드로 동작
  * @FeignClient name으로 지정한 서비스 호출
  
## Hystrix 설정
Hystrix 설정 시, 메소드가 모두 Hystrix Command로서 호출된다.
````yaml
feign:
  hystrix:
    enabled: true
````

Feign으로 정의한 Interface를 구현 후 Bean으로 정의
````java
@Component
public class ProductServiceProxyFallback implements ProductServiceProxy {
    @Override
    public String getProduct(long id) {
        return "[This Product is sold out]";
    }
}
````

`@FeignClient`에 `fallback` 명시
````java
@FeignClient(name = "product", fallback = ProductServiceProxyFallback.class)
public interface ProductServiceProxy {
````

에러 원인을 출력하기 위해서는 FallbackFactory를 사용한다.
````java
@Component
@Slf4j
public class ProductServiceProxyFallbackFactory implements FallbackFactory<ProductServiceProxy> {
    @Override
    public ProductServiceProxy create(Throwable cause) {
        log.error("Error", cause);
        return id -> "[This Product is sold out]";
    }
}
````

`@FeignClient`에 `fallback` 대신에 `fallbackFactory` 명시
````java
@FeignClient(name = "product", fallbackFactory = ProductServiceProxyFallbackFactory.class)
public interface ProductServiceProxy {
````
