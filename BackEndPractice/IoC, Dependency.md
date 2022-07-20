# Spring boot(2)

날짜: 2022년 7월 19일

# Inversion of Control(제어의 역전)

저번시간에서는, Order를 선언하고, 그 안에서 Fixed Amount를 선언했다.

Order 라는 Entity가 사용할 Class를 선언하고, 객체를 어떻게 사용할건지 정한다.

Order가 전부다 선언!

→일반적인 제어의 구조

### 제어의 역전 = 일반적인 제어의 구조와는 반대!

IOC상황에서는

- 객체가 자신이 사용할 객체를 스스로 선택하지 않고, 스스로 생성하지도 않는다.
    
    `public Order(UUID orderid, UUID customerid, List<OrderItem> orderItems, org.prgrms.kdt.voucher voucher)`
    
    Order가voucher를 전달받았다.(스스로 생성한게 아닌!)
    

### 대표 예시

서블릿, 스프링과 같은 Framework에서는, 제어 권한이 Framework에 있다.

Framework ⇒ 미리 만들어놓은 반제품이나 추상 라이브러리가 아닌, 전체 흐름의 권한을 갖게 된다!

 프레임워크가 흐름을 주도하며, 개발자가 만든 애플리케이션을 사용한다.

→Hollywood Principle (호출하지 말고, 프레임워크가 호출할때까지 기다려!)

# IOC 실습

Order과정을 Ordercontext가 대신 할 수 있도록 선언해주자.

1. Voucher Repository, Voucher Service를 만들기.

```java
public interface VoucherRepository {
    //Optional : voucher가 없을수도 있다.
    Optional<voucher> findById(UUID voucherId);
    
}
```

- Repository는 interface로 만든다.
- voucher가 없을 수도 있기때문에 Optional로 선언한다.
- 영속성을 보장하기 위해 Repository를 쓴다. (한번 써도 남아있도록)

```java
public class VoucherService {
    private final VoucherRepository voucherRepository;

    public VoucherService(VoucherRepository voucherRepository){
        this.voucherRepository =voucherRepository;
    }
    public static void useVoucher(voucher voucher) {

    }
    public voucher getVoucher(UUID voucherId){

        return voucherRepository.
                findById(voucherId)
                .orElseThrow(()->new RuntimeException("cannotfind a voucher for"+ voucherId));
    }
}
```

- VoucherService는 Voucher getter를 선언해주는데, voucherRepository에서 꺼내서 사용할 수 있도록 한다.
- voucher를 리턴해준다.

2.Order Repository, OrderService선언

```java
public interface OrderRepository {
    void insert(Order Order);
}
```

- Order를 저장할 수 있도록 한다.

```java
public class OrderService {
    //Voucher서비스와 Order에 대한 정보를 기록, 조회할 수 있는
    // Repository에 대해서 의존성을 가진다.
    private final VoucherService voucherservice;

    private final OrderRepository orderRepository;

    //orderservice 생성할 때, 이 서비스에 대한 객체를 외부에서 주입할 수 있도록.
    //->생성자를 이용
    public OrderService(VoucherService voucherservice, OrderRepository orderRepository) {
        this.voucherservice = voucherservice;
        this.orderRepository = orderRepository;
    }
    //생성에 대한 책임을 갖게 됨.
    public Order createOrder(UUID customerId, List<OrderItem> orderItems){
        var order = new Order(UUID.randomUUID(), customerId, orderItems);
        orderRepository.insert(order);
        return order;
    }
}
```

- 바우처 서비스와, 주문에 대한 정보를 기록, 조회할 수 있도록한다.
- 새로운 주문을 생성하고, 받은 주문을 저장소에 넣는다.

1. OrderContext선언

```java
public class OrderContext {
    public VoucherRepository voucherRepository(){
        return new VoucherRepository() {
            @Override
            public Optional<voucher> findById(UUID voucherId) {
                return Optional.empty();
            }
        };
    }
    public OrderRepository orderRepository(){

        return new OrderRepository() {
            @Override
            public void insert(Order Order) {

            }
        };
    }
    public VoucherService voucherService(){
        return new VoucherService(voucherRepository());
    }
    public OrderService orderService(){
        return new OrderService(voucherService(),orderRepository() );
    }
}
```

- OrderContext를 이용해 바우처 저장소와 바우처 서비스를 연결해준다.(서비스에 저장소를 넣은걸 리턴하도록 함)
- 마찬가지로 오더 서비스와  저장소를 연결해준다.

1. Order 변경

이제는 Order에서 값을 끌어내는게 아닌, voucher를 이용해 값을 끌어낸다.

```java
public long totalAmount(){
        var beforeDiscount = orderItems.stream().map(v->v.productPrice()*v.quantity())
                .reduce(0L,Long::sum);
        if(voucher.isPresent()) {
            return voucher.get().discount(beforeDiscount);
        }
        else{
            return beforeDiscount;
        }
    }
```

5.Order tester 변경

```java
public class OrderTester {
    public static void main(String[] args) {
        var customerId  = UUID.randomUUID();
        var orderContext = new OrderContext();
        var orderService = orderContext.orderService();
        var order= orderService.createOrder(customerId, new ArrayList<OrderItem>() {{
            add(new OrderItem(UUID.randomUUID(), 100L, 1));
        }});

        Assert.isTrue(order.totalAmount()==100L, MessageFormat.format("totalAmount {0}", order.totalAmount()));
    }
}
```

OrderService를 orderContext.orderService로 선언한걸 확인할 수 있다.

객체의 생성, 변경을 다른클래스에 넘긴모습 확인.

## Repository와 Service가 뭔가요?

### Aggregate란?

일종의 Entity이다. 

Entity의 집합이지만 각각의 Aggregate는 root가 존재한다. 

Entity의 집합이 Aggregate이다.

서비스는 상태가 없다.→ 프로세스와, 비즈니스 로직을 가지고 있다.(메소드만 갖고있다.)

서비스가 Aggregate, Aggregate root를 가지고 transaction을 일으킴.

transaction에 대한 보장을 서비스 전체로 할 수 있음.

오더를 저장하면, 오더 레포지토리에 오더의 상태를 저장.

오더 서비스는, 다른 서비스를 이용해서 그 서비스의 레포지토리를 이용해서 그 상태를 이용해서 오더의 상태를 변경.

## Application Context

Order Context에서 객체에 대한 생성과 조합이 이루어졌는데,

→ IoC Container라고 불렸다. 

IOC Container: 개별 객체들의 의존관계가 주어지고, 객체의 파괴, 조합이 이루어짐.

IoCContainer에 객체를 등록하면, 필요한 의존관계를 맺어주고, 생명주기를 관리하며 인스턴스 만든다.

→스프링에서 Application Context지원.

Bean : IoC Container에서 관리되어지는 객체.
       Annotation으로 관리된다.

## 코드를 바꿔보자.

OrderContext→ App Configuration으로 바꿔봅시다.

```java
@Configuration

public class AppConfiguration {
    @Bean
    public VoucherRepository voucherRepository(){
        return new VoucherRepository() {
            @Override
            public Optional<voucher> findById(UUID voucherId) {
                return Optional.empty();
            }
        };
    }
    @Bean
    public OrderRepository orderRepository(){

        return new OrderRepository() {
            @Override
            public void insert(Order Order) {

            }
        };
    }
    @Bean
    public VoucherService voucherService(){
        return new VoucherService(voucherRepository());
    }
    @Bean
    public OrderService orderService(){
        return new OrderService(voucherService(),orderRepository() );
    }

}
```

이름을 바꿨을 때, Annotation을 통해 선언한다.

@Configuration

@Bean같은걸 선언해준다.

위와 같이 선언했을 때, 

```java
var applicationContext = new AnnotationConfigApplicationContext(AppConfiguration.class);
var orderService = applicationContext.getBean(OrderService.class);
```

위처럼 applicationContext, AnnotationConfigApplication같은 함수들을 사용할 수 있게 되고, 컨텍스트 안에 객체들을 넣는 과정이 되는것이다.

## Dependency Injection

IoC를 구현하는 패턴.

- 전략패턴
- 서비스 로케이터 패턴
- 펙토리패턴
- 의존관계 주입패턴

오더가 어떤 바우처 객체를 선언할지, 오더 서비스가 어떤 오더 저장소 객체를 생성할지 스스로 결정하지 않았다.

생성자를 통해 객체를 주입받음.

생성자를 통해 주입받는 패턴 : 생성자 주입 패턴(Constructor-based Dependency injection)

스프링은 Constructor-base 와 Setterbase를 지원한다.