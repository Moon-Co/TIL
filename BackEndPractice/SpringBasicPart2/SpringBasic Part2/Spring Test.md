# Spring Test

SpringJUnitConfig를 이용해서 Context에서 Bean을 빼내와서 테스트하는 방법

```java
public class kdtSpringContextTests {
    @Configuration
    @ComponentScan(
            basePackages = {"org.prgrms.kdt.voucher", "org.prgrms.kdt.order","org.prgrms.kdt.configuration"}
    )
    //ComponentScan: 저기 안에 넣으면 , 각각의 서비스와 레포지토리가 bean으로 등록된다.
    static class Config{
    }
```

@Configuration을 작성해줘서 자바 클래스파일을 설정파일로 만들어줌.

@Component Scan을 이용해 어노테이션 알아서 찾아서 컨테이너에 등록한다.

@Autowired = 주입대상이 되는 bean을 컨테이너에 찾아 주입한다.

```java
@Autowired
    OrderService orderService;

    @Autowired
    ApplicationContext context;

    @Autowired @Qualifier("memory")
    VoucherRepository voucherRepository;
```

주입 대상이 되는것들을 컨테이너에 주입한다.

```java
@Test
    @DisplayName("applicationContext 만들어져야함")
    public void testApplicationContext(){
        assertThat(context,notNullValue());
    }

    @Test
    @DisplayName("VoucherRepository가 bean으로 등록되어있어야해")
    public void testVoucherRepositoryCreation(){
        var bean = context.getBean(MemoryVoucherRepository.class);
        assertThat(bean,notNullValue());
    }
    @Test
    @DisplayName("OrderService를 사용해서 주문을 생성할 수 있다.")
    public void testOrderService(){
        var fixedAmountVoucher= new FixedAmountVoucher(UUID.randomUUID(),100);
        voucherRepository.insert(fixedAmountVoucher);
```

테스트는 위와 같이 해준다.hamcrest를 이용해서 해주는데, 

context에서 bean을 뽑아서 해주는게 포인트

```java
var order = orderService.createOrder(
                UUID.randomUUID(),
                List.of(new OrderItem(UUID.randomUUID(),
                        200,1)),
                fixedAmountVoucher.getVoucherId());

        //Then(상태에 집중)
        assertThat(order.totalAmount(), is(100L));
        assertThat(order.getVoucher().isEmpty(),is(false));
        assertThat(order.getVoucher().get().getVoucherId(),is(fixedAmountVoucher.getVoucherId()));
        assertThat(order.getOrderStatus(),is(OrderStatus.ACCEPTED));

    }
}
```

생성과 테스트도 위처럼 해주는건 똑같다.