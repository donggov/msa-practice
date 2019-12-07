# Spring Cloud Config
스프링 클라우드 컨피그는 분산 시스템에서 설정파일을 외부로 분리하는 것을 지원하고, 애플리케이션 속성값을 중앙에서 관리할 수 있게 해준다.
- Spring Cloud Server : 설정값 제공 주체
- Spring Cloud Client : 설정값 사용자

## Server 설정
build.gradle
````gradle
implementation 'org.springframework.boot:spring-boot-starter-actuator'
````

application.yaml
````yaml
server:
  port: 8888

spring:
  application:
    name: config-server
  cloud:
    config:
      server:
        git:
          uri: https://github.com/donggov/spring-config-repository # 설정값 저장되어있는 저장소
````

`@EnableConfigServer` 추가
````java
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {
````

## Server 데이터 조회 테스트
Git 저장소에 `display-default.yml`과 'display-test.yml' 파일을 생성 후, 아래와 같이 입력한다.
display-default.yml
````yaml
application:
  env: "Local"
greeting: "Welcome to Display!!"
````

display-test.yml
````yaml
application:
  env: "Test"
greeting: "Hello!"
````

`/{appliation-name}/{profile}/{label}` 형식으로 조회해볼 수 있다.
> http://localhost:8888/display/default
````json
{
    "name": "display",
    "profiles": [
        "default"
    ],
    "label": null,
    "version": "33220fff261cba326d981f2c55843b76db005400",
    "state": null,
    "propertySources": [
        {
            "name": "https://github.com/donggov/spring-config-repository/display-default.yml",
            "source": {
                "application.env": "Local",
                "greeting": "Welcome to Display!!"
            }
        }
    ]
}
````

> http://localhost:8888/display/test
````json
{
    "name": "display",
    "profiles": [
        "test"
    ],
    "label": null,
    "version": "33220fff261cba326d981f2c55843b76db005400",
    "state": null,
    "propertySources": [
        {
            "name": "https://github.com/donggov/spring-config-repository/display-test.yml",
            "source": {
                "application.env": "Test",
                "greeting": "Hello!"
            }
        }
    ]
}
````

## Client 설정
build.gradle
````gradle
implementation 'org.springframework.boot:spring-boot-starter-web'
implementation 'org.springframework.boot:spring-boot-starter-actuator'
implementation 'org.springframework.cloud:spring-cloud-starter-config'
````

application.yaml
````yaml
spring:
  application:
    name: display
  cloud:
    config:
      uri: http://localhost:8888
      fail-fast: false #Config Server와 연결이 되지 않아도 실행
````

아래와 같이 컨트롤러를 만든다.
`@RefreshScope`이 적용된 클래스는 설정파일 변경 후 refresh 시, 애플리케이션에 반영된다.
````java
@RestController
@RefreshScope
public class GreetingController {

    @Value("${application.env}")
    private String env;

    @Value("${greeting}")
    private String greeting;

    @GetMapping("")
    public String getGreeting() {
        return env + ", " + greeting;
    }
}
````

## Client 테스트
Client는 Display 애플리케이션이다.
> http://localhost:9000/
````text
Local, Welcome to Display!!
````

## 설정값 변경 테스트
`display-default.yml` 파일을 아래처럼 수정
````yaml
application:
  env: "Local"
greeting: "Welcome to Display!! Updated ^o^"
````

Client의 /actuator/refresh 호출
> POST `http://localhost:9000/actuator/refresh`
````text
2019-12-07 23:12:34.042  INFO 9904 --- [nio-9000-exec-5] c.c.c.ConfigServicePropertySourceLocator : Fetching config from server at : http://localhost:8888
2019-12-07 23:12:35.835  INFO 9904 --- [nio-9000-exec-5] c.c.c.ConfigServicePropertySourceLocator : Located environment: name=display, profiles=[default], label=null, version=9d9fa7681fba2305dc129ef3fb06f72901f22373, state=null
2019-12-07 23:12:35.835  INFO 9904 --- [nio-9000-exec-5] b.c.PropertySourceBootstrapConfiguration : Located property source: CompositePropertySource {name='configService', propertySources=[MapPropertySource {name='configClient'}, MapPropertySource {name='https://github.com/donggov/spring-config-repository/display-default.yml'}]}
2019-12-07 23:12:35.837  INFO 9904 --- [nio-9000-exec-5] o.s.boot.SpringApplication               : No active profile set, falling back to default profiles: default
````

Endpoint 호출
> http://localhost:9000/
````text
Local, Welcome to Display!! Updated ^o^
````


