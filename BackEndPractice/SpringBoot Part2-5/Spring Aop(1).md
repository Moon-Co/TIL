# Spring Aop(1)

# 관점 지향 프로그래밍

관점지향 프로그래밍(Aspect Oriented Programing)(AOP)

핵심기능과 부가기능을 분리하는 프로그래밍 방식( 비즈니스 로직에 부가기능을 추가할 수 있어)

중복되어있는 부가기능을 분리. (비즈니스 로직에만 집중 할 수 있다.) 

AOP를 적용하는 시점

- 컴파일 시점 : 컴파일전에 부가기능을 소스에 삽입하는 방식
- 클래스 로딩 시점 : 바이트코드에 부가기능을 삽입하는 방식(바이너리에 부가기능 삽입해서 load)
- 런타임 시점 : →스프링에서 제공하는 AOP방식
객체의 프록시 객체를 만들어서 프록시 객체가 처리하는 방힉

# 프록시 사용 연습

AOP는 프록시 기반으로 사용되기 때문에, 다이나믹 프록시 사용에 대해 알아둘 필요가 있다. 연습해보자.

1. 인터페이스를 선언해보자.

```java
interface Calculator{
    int add(int a, int b);
}
```

Calculator라는 인터페이스를 선언했다고 하자.

이 인터페이스를 구현한 클래스를 선언한다.

```java
class CalculatorImpl implements Calculator{
    //여기가 타겟 클래스
    @Override
    public int add(int a, int b) {
        return a+b;
    }
}
```

add란 매소드를 선언했다.

InvocationHandler를 선언해야한다. 다음과 같다.

```java
class LoggingInvocationHandler implements InvocationHandler {

    private static final Logger log =LoggerFactory.getLogger(LoggingInvocationHandler.class);
    private final Object target;
    public LoggingInvocationHandler(Object target) {
        this.target = target;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.info("{} method executed", method.getName());
        return method.invoke(target,args);
    }
}
```

로거를 선언하고, InvocatioHandler 인터페이스를 통해, LoggingInvocationHandler를 구현했다.

Object 객체인 target을 가리키는 메소드를 선언.

InvocationHandler인터페이스에 있는 invoke 메소드를 구현한다.

invoke(프록시 객체, 클라이언트가 호출한 메소드, 메소드에 전달한 인자) 

메소드를 invoke한 결과를 리턴해준다. return invoke(target, args)

메인함수

```java
public class JdkProxyTest {
    private static final Logger log =LoggerFactory.getLogger(LoggingInvocationHandler.class);

    public static void main(String[] args) {
        var calculator = new CalculatorImpl();
        Calculator proxyInstance = (Calculator)Proxy.newProxyInstance(LoggingInvocationHandler.class.getClassLoader(),
                new Class[]{Calculator.class,},
        new LoggingInvocationHandler(calculator));
        var add = proxyInstance.add(1,2);
        log.info("ADD->{}",add);
    }
}
```

프록시 테스트를 메인함수에서 해보자.

calculator 객체를 생성하고,

Proxy.newProxyInstance를 통해 프록시 인스턴스를 만들었다. 

 <프록시 인스턴스 선언법>

Object Proxy.newProxyInstance(ClassLoader, Interface,InvocationHandler)

의 구조임

프록시 인스턴스는 무조건 invoke를 실행하게 되어있다.

ClassLoader : 클래스이름.Class.ClassLoader를 통해 선언(LoggingInvocationHandler.class.getclassLoader)

Interface: 인터페이스 이름을 선언 (new Class[]{Calculator.class}

InvocationHandler : InvocationHandler클래스에 사용하고자하는 객체 넣어서 실행.

이렇게되면? ProxyInstance는  calculator를 LoggingInvocationHandler로 감싼 형태가 됨.