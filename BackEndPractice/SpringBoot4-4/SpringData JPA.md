# SpringData JPA

## Spring DATA JPA

스프링에서 JPA를 편리하게 사용할 수 있도록 지원하는 프로젝트

- 데이터소스를 JavaConfiguration으로 다 만들었었는데 AutoConfiguration으로 처리됨.
- 데이터 저장계층에 대한 인터페이스를 지원해줘서 CRUD편하게 할 수 있다.

## SPRING DATA JPA 실습

1. application.yml에 미리 Auto configuration을 해놓는다.

```yaml
spring:
  datasource:
    driver-class-name: org.h2.Driver
    url: jdbc:h2:~/order;DB_CLOSE_ON_EXIT=FALSE
    username: sa
    password:
  jpa:
    open-in-view: false
    hibernate:
      ddl-auto: create-drop
    show-sql: true
    properties:
      hibernate.format_sql: true
```

1. 인터페이스에서 JpaRepository를 넣으면 사용할 수 있다.

```java
public interface OrderRepository extends JpaRepository<Order, String> {
    

}
```

스프링 JPA에서 CRUD의 다양한 메소드를 지원한다.

테스트를 해보자

1. 테스트

```java
@Slf4j
@SpringBootTest
public class OrderRepositoryTest {
    @Autowired
    OrderRepository orderRepository;

    @Test
    void test(){
        String uuid = UUID.randomUUID().toString();
        Order order = new Order();
        order.setUuid(uuid);
        order.setOrderStatus(OrderStatus.OPENED);
        order.setOrderDatetime(LocalDateTime.now());
        order.setMemo("---");
        order.setCreatedBy("Guppy.kang");
        order.setCratedAt(LocalDateTime.now());

        orderRepository.save(order);
```

따로 트랜젝션이나 emf나 선언을 해주지 않아도, save를 쓸 수 있는 모습을 확인할 수 있다. 

1. 다른 메소드를 선언해줄 수도 있다.

```java
public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findAllByOrderStatus(OrderStatus orderStatus);
    // SELECT * FROM orders WHERE order_status = 'OPEND'
    List<Order> findAllByOrderStatusOrderByOrderDatetime(OrderStatus orderStatus);
    // 		// SELECT * FROM orders WHERE order_status = 'OPEND' ORDER BY order_datetiem
   

}
```

스프링 JPA에서 지원하는 다른 메소드들을 인터페이스 내부에서 선언해서 사용할 수 도 있다.

1. 테스트해보자.

```java
orderRepository.findAllByOrderStatus(OrderStatus.OPENED);
orderRepository.findAllByOrderStatusOrderByOrderDatetime(OrderStatus.OPENED);

```

이렇게 사용 가능!!

1. 쿼리를 자체적으로 선언해서 사용할 수 있다.

```java
public interface OrderRepository extends JpaRepository<Order, String> {
    @Query("SELECT o FROM Order AS o WHERE o.memo LIKE %?1%")
    Optional<Order> findByMemo(String memo);

}
```

쿼리를 커스텀해서 사용할 수도 있다. 매우편리