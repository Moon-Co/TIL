# Spring Profile

# Profile

특징이나 공통점을 찾아서 그룹화.

설정,Bean들을 그룹화해서 하나의 Profile로 정리하고, 원하는 Profile로 어플리케이션을 구동할 수 있다.

ex) dB접속정보, Staging환경, Dev개발환경, local환경… 각각 환경에따른 환경설정 변수들을 정해주며 변경

# Profile을 이용해서 Bean을 정의해보자.

1. Annotation에 Profile을 넣어준다.

JdbcVoucherRepository의 프로파일과, MemoryVoucherRepository의 프로파일을 각각 지정해줬다. 

```java

@Repository
@Profile("local")
public class MemoryVoucherRepository implements VoucherRepository {
```

```java
@Repository
@Profile("dev")
public class JdbcVoucherRepository implements VoucherRepository{
```

1. Tester에서 environment가 profile에 따라 바뀔 수 있도록 설정해준다.

```java
var applicationContext = new AnnotationConfigApplicationContext();
applicationContext.register(AppConfiguration.class);
        var environment = applicationContext.getEnvironment();
        environment.setActiveProfiles("local");
        applicationContext.refresh();
```

ApplicationContext를 정의하고, regist한다. 

environment를 프로파일로 등록한후, refresh해주는게 포인트!

바뀌었는지 체크는 다음과 같이 했다.

```java
System.out.println(MessageFormat.format("isJdbcRepo->{0}",voucherRepository instanceof JdbcVoucherRepository));
        System.out.println(MessageFormat.format("isJdbcRepo->{0}",voucherRepository.getClass().getCanonicalName()));
```

결과는 다음처럼 나온다.

```java
isJdbcRepo->false
isJdbcRepo->org.prgrms.kdt.voucher.MemoryVoucherRepository
```

AppconFiguration에서도 Profile을 달 수 있다.

Profile Annotation말고, Profile을 작성할때! 

yaml이 Profile에 의해서 동작하도록 하기.

```java
servers:
  - dev.bar.com
  - foo.bar.com

---
spring.config.activate.on-profile: local

kdt:
  version: "v1.0"
  minimum-order-amount: 1
  support-vendors:
    - a
    - b
    - c
    - d
  description: |
    line 1 hello world
    line 2 xxxx
    line 3

---
spring.config.activate.on-profile: dev
kdt:
  version: "v1.0"
  minimum-order-amount: 1
  support-vendors:
    - a
    - b
    - c
    - d
  description: |
    line 1 hello world
    line 2 xxxx
    line 3
```

위처럼 local과 dev를 spring.config.activate.on-profile을 통해 나눠준다.

##이거는 Spring Boot에서만 쓸 수 있기때문에, kdtApplication에서 실행을 해준다.

```java
@SpringBootApplication
@ComponentScan(basePackages = {"org.prgrms.kdt.order","org.prgrms.kdt.voucher","org.prgrms.kdt.configuration"})

public class KdtApplication {

	public static void main(String[] args){
		var springApplication = new SpringApplication(KdtApplication.class);
		springApplication.setAdditionalProfiles("local");
		var applicationContext = springApplication.run(args);
		//var applicationContext = SpringApplication.run(KdtApplication.class, args);
		var orderProperties = applicationContext.getBean(OrderProperties.class);
		System.out.println(MessageFormat.format("version->{0}",orderProperties.getVersion()));
		System.out.println(MessageFormat.format("minimumOrderAmount->{0}", orderProperties.getMinimumOrderAmount()));
		System.out.println(MessageFormat.format("supportVendors->{0}", orderProperties.getSupportVendors()));
		System.out.println(MessageFormat.format("description->{0}",orderProperties.getDescription()));
		var customerId  = UUID.randomUUID();
		var voucherRepository = applicationContext.getBean(VoucherRepository.class);
		var voucher = voucherRepository.insert(new FixedAmountVoucher(UUID.randomUUID(),10L));

		System.out.println(MessageFormat.format("isJdbcRepo->{0}",voucherRepository instanceof JdbcVoucherRepository));
		System.out.println(MessageFormat.format("isJdbcRepo->{0}",voucherRepository.getClass().getCanonicalName()));
	}
```

SpringApplication을 kdtApplication에서 선언해주고, Applicationcontext를 run으로 해준다.