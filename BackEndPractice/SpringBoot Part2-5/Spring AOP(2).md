# Spring AOP(2)

# AOP용어 살펴보기

- 타겟(Target)
AOP를 적용할 대상. 핵심기능을 담고있는 모듈
- 조인 포인트(Join Point)
Application 실행중에 부가기능을 적용할 수 있는 지점.
보통 메소드로 구분함. (AOP를 적용할 수 있는 위치)
- 포인트컷(PointCut)
Aspect를 이용할때 사용하는 표현식. 여러 조인포인트중에 어디에 무엇을 적용시킬지!
- 애스펙트(Aspect)
어드바이스와 포인트컷으로 이루어진 클래스를 지칭한다.
스프링에서는 Aspect를 빈으로 등록해서 사용.
- 어드바이스(Advice)
부가기능에 대한 코드
타겟의 조인트포인트에 포인트컷을 이용해서 만드는것!
(AOP를 적용할 대상에, 부가기능을 적용할 수 있는 지점에, 정해진 표현식을 이용해서 만드는것)
- 위빙
타겟의 조인포인트에 어드바이스를 적용하는 과정.

# AOP 만들어보기

```java
@Aspect
@Component
public class LoggingAspect {
    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class)
    @Around("execution(public void org.prgrms.kdt..*Service.*(..))")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable{
        log.info("before method called=>{}",joinPoint.getSignature().toString() );
        var result = joinPoint.proceed();
        log.info("after aspect=>{}", result);

        return result;
    }
}
```

클래스 하나를 @Aspect를 통해 Aspect로 만들어준다.

Component Scan시 스캔할 수 있도록 만들기 위해 @Component 를 선언해준다.

클래스 선언후 @Around()의 괄호 내부에 포인트컷을 넣어준다.

`execution(public void org.prgrms.kdt..*Service.*(..))`이런식으로 선언했는데,

execution : 지정자.  이 어드바이스를 어떤식으로 적용시킬것인가?

public : 접근지정자. 접근지정자가 public인것만 처리해준다.(상관없으면 *)

void : 리턴타입. 리턴타입이 이것과 같을때만 처리해준다.(상관없으면 *)

org.prgrms.kdt.. : 위치. 위치를 선언해준다. 패키지의 풀네임을 적어줘야한다.(상관없으면*)

*Service: 메소드 이름. 

(..) : Argument

포인트컷의 내용에 따라 실행되는것들이 다르다.

포인트컷은 따로 메소드로 선언해줘도 되고, 따로 클래스를 만들어서 선언해도 된다.

클래스로 선언한다면

```java
public class CommonPointcut {
    @Pointcut("execution(public * org.prgrms.kdt..*Service.*(..))")
    public void servicePublicMethodPointCut(){};

    @Pointcut("execution(* org.prgrms.kdt..*Repository.*(..))")
    public void repositoryMethodPointcut(){};
}
```

위처럼 선언해줄 수 있다.

그럴때 Aspect의 around는 

```java
@Around("org.prgrms.kdt.aop.CommonPointcut.servicePublicMethodPointCut()")
```

이런식으로 선언해주면 된다.

# 테스트

```java

@SpringJUnitConfig
@ActiveProfiles("test")
public class AopTests {

  @Configuration
  @ComponentScan(
    basePackages = {"org.prgrms.kdt.voucher","org.prgrms.kdt.aop"}
  )
  @EnableAspectJAutoProxy //aop적용을 위해
  static class Config {
  }

  @Autowired
  ApplicationContext context;

  @Autowired
  VoucherRepository voucherRepository;
  @Autowired
  VoucherService voucherService;
  @Test
  @DisplayName("Aop test.")
  public void testOrderService() {
    // Given
    var fixedAmountVoucher = new FixedAmountVoucher(UUID.randomUUID(), 100);
    voucherRepository.insert(fixedAmountVoucher);

    voucherService.getVoucher(fixedAmountVoucher.getVoucherId());

  }

}
```

테스트의 풀코드는 위와같다.

사용할것들은 Autowired로 bean으로 추가해주고, 

 @EnableAspectJAutoProxy 을 통해 연결해준다.

물론 컴포넌트 스캔도 해줘야함.