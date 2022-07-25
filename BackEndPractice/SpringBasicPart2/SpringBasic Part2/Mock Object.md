# Mock Object

Mock Object(모의 객체)

Testdouble→ 테스트 하고자하는 객체와 의존관계를 사용할 수 없을때, SUT와 상호작용을 하기 위한 객체

Testdouble은 Mock, Stub으로 이루어져있다.

Mock객체 : 호출을 기대하는 객체.(행위에 집중을 한다.)

Stub객체 : 가짜객체.

상태검증: 메소드 수행 후, 객체의 상태를 확인하여 올바르게 동작했는지 확인

행위검증 : 메소드의 리턴값으로 판단할 수 없을떄! 특정 동작을 수행하는지 확인(Mock object를 쓰는 목적)

### stub으로 Order생성하기.

1-1. Given을 준다. (주어진 상황)

```java
    @Test
    @DisplayName("오더가 생성되어야한다(stub)")
    void createOrder() {
        //GIVEN (주어진 상황을 표현!)
        //Stub구현하기
        var voucherRepository = new MemoryVoucherRepository();
        var fixedAmountVoucher= new FixedAmountVoucher(UUID.randomUUID(),100);
        voucherRepository.insert(fixedAmountVoucher);
var sut = new OrderService(new VoucherService(voucherRepository),new OrderRepositoryStub());
```

메모리 바우처 저장소에서 저장소를 끌어오고, fixedAmountVoucher에서 바우처도 끌어온다. 저장소에 바우처를 넣고, 오더서비스에 바우처서비스와 오더 저장소를 넣는다. → SUT완성

1-2 When을 준다.

```java
var order = sut.createOrder(UUID.randomUUID(), List.of(new OrderItem(UUID.randomUUID(),200,1)),fixedAmountVoucher.getVoucherId());
```

GIvend을 바탕으로 order 생성

1-3 Then을 만든다.

```java
assertThat(order.totalAmount(), is(100L));
        assertThat(order.getVoucher().isEmpty(),is(false));
        assertThat(order.getVoucher().get().getVoucherId(),is(fixedAmountVoucher.getVoucherId()));
        assertThat(order.getOrderStatus(),is(OrderStatus.ACCEPTED));
```

테스트할것들을 테스트해준다.

### Mock을 이용한 Test

1. Given

```java
@Test
    @DisplayName("Mock으로 오더 생성")
    void createOrderByMock(){
        //Given
        var voucherServiceMock = mock(VoucherService.class);
        var orderRepositoryMock = mock(OrderRepository.class);
        var fixedAmountVoucher= new FixedAmountVoucher(UUID.randomUUID(),100);
        
        when(voucherServiceMock.getVoucher(fixedAmountVoucher.getVoucherId())).thenReturn(fixedAmountVoucher);
        var sut = new OrderService(voucherServiceMock,orderRepositoryMock);
```

- mock 을 이용해서 바우처서비스, orderRepo를 만든다.
- voucher를 만들어준다.
- when을 이용해, voucherService가 fixedAmountVoucher를 이용할때, fixedAmountVoucher를 리턴하도록 해준다.
- 왜? mock객체는 0,null,False밖에 리턴 못하기때문에, Mock에 함수를 지정해주었을때 우리가 예상한 값을 리턴하도록 사전에 만들어줘야한다.
- 오더서비스인 sut를 만들어준다.

2.When

```java
var order = sut.createOrder(
                UUID.randomUUID(),
                List.of(new OrderItem(UUID.randomUUID(),
                        200,1)),
                fixedAmountVoucher.getVoucherId());
```

order를 만들어준다.

3.Then

```java
//Then
        //행위가 일어났는지 확인(바우처서비스Mock객체와 orderRepository Mock객체에 대해서
        //어떤 메소드가 정상적으로 호출되었는가를 verify
        var inOrder = inOrder(voucherServiceMock);
        inOrder.verify(voucherServiceMock).getVoucher(fixedAmountVoucher.getVoucherId());
        //getvoucher가 fixedamountvoucher의 getVoucher로 실행되었는가!
        inOrder.verify(orderRepositoryMock).insert(order);
        //insert에 order가 실제로 잘 들어갔는가?
        verify(voucherServiceMock).useVoucher(fixedAmountVoucher);
        //fixedAmountVoucher가 사용되었는가?
```

inOrder→ 순서대로 실행되었는지 확인해주는것,

verify = 안에 적혀있는 명령어 대로 잘 실행이 되었는지 확인한다.