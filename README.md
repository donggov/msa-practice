# MSA Practice
Spring Cloud를 활용한 MSA Practice

## Application
1. Spring Cloud Config Server
2. Eureka
3. Display (Product API 사용하여 Product 정보 조회)
4. Product
5. Zuul

## Run
RabbitMQ 설치 및 실행
````text
rabbitmq-service.bat stop   // Stop
rabbitmq-service.bat start  // Start
````

RabbitMQ 기본 접속 정보
````text
URL : http://localhost:15672
Username : guest
Password : guest
````

Config Server 실행
````text
java -jar -Dserver.port=8888 config-server-1.0-SNAPSHOT.jar
````

Eureka 실행
````text
java -jar -Dserver.port=8761 eureka-1.0-SNAPSHOT.jar
````

Display Application 실행
````text
java -jar -Dserver.port=9000 display-1.0-SNAPSHOT.jar
````

Product Application 실행
````text
java -jar -Dserver.port=9100 product-1.0-SNAPSHOT.jar
````

Zuul 실행
````text
java -jar -Dserver.port=9999 zuul-1.0-SNAPSHOT.jar
````

## Test
````text
http://localhost:9000               -- Spring Cloud Config Test
http://localhost:8761               -- Eureka Console
http://localhost:9000/display/1     -- Display Test
http://localhost:9100/products/1    -- Product Test
http://localhost:9999/display/1     -- Zuul Test
````

## 정리
- [Spring Cloud Config](https://github.com/donggov/msa-practice/blob/master/docs/SpringCloudConfig.md)
- [Spring Cloud Bus](https://github.com/donggov/msa-practice/blob/master/docs/SpringCloudBus.md)
- [Hystrix (Circuit Breaker)](https://github.com/donggov/msa-practice/blob/master/docs/Hystrix.md)
- [Ribbon](https://github.com/donggov/msa-practice/blob/master/docs/Ribbon.md)
- [Eureka](https://github.com/donggov/msa-practice/blob/master/docs/Eureka.md)
- [Feign](https://github.com/donggov/msa-practice/blob/master/docs/Feign.md)
- [Zuul (API-Gateway)](https://github.com/donggov/msa-practice/blob/master/docs/Zuul.md)
