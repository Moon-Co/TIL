# Spring Boot(1)

날짜: 2022년 7월 18일

## 용어정리

- 도메인

사용자가 Application을 사용하는 대상 영역 →해당 application의 도메인

ex) 주문관리 어플리케이션 : 주문관리가 도메인

- Entity(객체의 종류중 하나)

다른 entity와 구별할 수 있는 식별자를 가지고 있다. 

시간의 흐름에 따라서 지속적으로 변경된다.

→식별자 존재, 시간에 따라 바뀜.

- Value Object

각 속성이 개별적으로 변하지 않고, 값 자체로 고유한 객체.(동전의 비유)

(Entity는 ID갖고있고, Value Object는 값이 고유함)

Entity들이 Value object를 속성을 가지고있다.(주문했을때의 주문자.)

## 예제코드 만들기

Order entity를 만들어보자(주문)

1. 모든 필드들을 선언한다.

(UUID → 고유 식별자)(Universally Unique Identifiers)

```sql
public class Order {
    private final UUID orderid;
    private final UUID customerid;
    private final List<OrderItem> orderItems;
    private long discountAmount;
    private OrderStatus orderStatus = OrderStatus.ACCEPTED;
```

2.생성자를 생성한다.

```sql
public Order(UUID orderid, UUID customerid, List<OrderItem> orderItems, long discountAmount, OrderStatus) {
        this.orderid = orderid;
        this.customerid = customerid;
        this.orderItems = orderItems;
        this.discountAmount = discountAmount;
    }
```

3.OrderItem을 선언한다.

변하지 않는것들은 final을 붙여준다.

```java
public class OrderItem {
    public final UUID products;
    public final long productPrices;
    public final long quantity;
		private long discountAmount;
	  private OrderStatus orderStatus = OrderStatus.ACCEPTED;

    public OrderItem(UUID products, long productPrices, long quantity) {
        this.products = products;
        this.productPrices = productPrices;
        this.quantity = quantity;
    }

    public UUID getProducts() {
        return products;
    }

    public long getProductPrices() {
        return productPrices;
    }

    public long getQuantity() {
        return quantity;
    }

}
```

1. Order Item의 불변객체들(생성자와 getter)가 있는것들은 record로 선언할 수 있다.

```java
public record OrderItem
        (UUID productId, long productPrice,
         long quantity){

}
```

위와 같이 심플하게 선언할 수 있다.

1. OrderStatus 는 enum을 이용해서 열거형으로 선언해준다.

```java
public enum OrderStatus {
    ACCEPTED,
    PAYMENT_REQUIRED,
    PAYMENT_CONFIGURED,
    PAYMENT_REJECTED,
    READY_FOR_DELIVERY,
    SHIPPED,
    SELLED,
    CANCELLED

}
```

6.할인율과 주문내역을 입력할 수 있게 Setter선언해준다.(변할 수 있기 때문에)

```java
public void setDiscountAmount(long discountAmount) {
        this.discountAmount = discountAmount;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
```

1. 총 가격을 선언해준다.

```java
public long totalAmount(){
        var beforeDiscount = orderItems.stream().map(v->v.productPrice()*v.quantity())
                .reduce(0L,Long::sum);
        return beforeDiscount-discountAmount;

    }
```

# 코드 바꿔보기.

### Spring개념과 함께 위의 코드를 바꿔보자.

의존성 을 이용해보자!

## 의존성

어떤 객체가 다른객체와 협력할 때, 두 객체사이에 ‘의존성’이 존재한다.

실행시점의 의존성,(컴파일타임 의존성)

구현시점의 의존성(런타임 의존성)

이 다르다. 

Order 가 discount해줄때, 특정 금액을 깎아주는 바우처를 만들어보자.(컴파일타임 의존성)

1.FixedAmountVoucher선언

```java
public class FixedAmountVoucher {
    private final long amount;

    public FixedAmountVoucher(long amount) {
        this.amount = amount;
    }
    //주어진 금액에 대해서 어떻게 discount할지에 대한 로직!
    public long discount(long beforeDiscount){
        return beforeDiscount - amount;
    }
}
```

얼마나 깎느냐에 대한 선언.

1. Tester선언(main함수)

```java
public class OrderTester {
    public static void main(String[] args) {
        var customerID  = UUID.randomUUID();
        var orderItems = new ArrayList<OrderItem>() {{
            add(new OrderItem(UUID.randomUUID(), 100L, 1));
        }};
        //아래와 같이 hard coding되어있으면, FixedAmountVoucher의 로직이 변하면
        //Order도 바꿔줘야함.
        var order = new Order(UUID.randomUUID(), customerID, orderItems, 10L);
        Assert.isTrue(order.totalAmount()==90L, MessageFormat.format("totalAmount {0}", order.totalAmount()));
    }
}
```

100원*1개 라서 100원인데, 100-10 ≠90이면 예외 발생시키기!(Assert)

→FixedAmountVoucher와 Order는 의존성이 있다. 매우 단단함!

클래스 사이의 의존성은 (컴파일타임 의존성)→ 단단한 결합도.

느슨한 결합도로 바꾸고싶다! →런타임 의존성을 가지도록 한다.

## 런타임 의존성으로 바꾸기

런타임에 특정한 객체를 생성해서 전달하도록! (값이 변경되면, Order에서 변경 안하도록) 

1. Voucher라는 인터페이스를 선언한다.

```java
public interface voucher{
    UUID getVoucherID();

    long discount(long beforeDiscount);

}
```

1. FixedAmountVoucher에 Voucher를 임플리먼트해서 넣는다.
(바우처 아이디와 할인률을 넣어)

```java
public class FixedAmountVoucher implements voucher {
    private final UUID voucherId;
    private final long amount;

    public FixedAmountVoucher(UUID voucherId, long amount) {
        this.voucherId = voucherId;
        this.amount = amount;
    }

    @Override
    public UUID getVoucherID() {
        return null;
    }

    //주어진 금액에 대해서 어떻게 discount할지에 대한 로직!
    public long discount(long beforeDiscount){
        return beforeDiscount - amount;
    }
}
```

1. Tester코드를 살펴보자.

```java
public class OrderTester {
    public static void main(String[] args) {
        var customerID  = UUID.randomUUID();
        var orderItems = new ArrayList<OrderItem>() {{
            add(new OrderItem(UUID.randomUUID(), 100L, 1));
        }};
        //아래와 같이 hard coding되어있으면, FixedAmountVoucher의 로직이 변하면
        //Order도 바꿔줘야함.
        var FixedAmountVoucher = new FixedAmountVoucher(UUID.randomUUID(),10L);
        var order = new Order(UUID.randomUUID(), customerID, orderItems, FixedAmountVoucher);
        Assert.isTrue(order.totalAmount()==90L, MessageFormat.format("totalAmount {0}", order.totalAmount()));
    }
}
```

Order자체의 파라미터에 하드코드가 들어가지 않는다!

→ 조건이나 값 변경시, Order를 건드릴 필요가 없다는 것. 다른 바우처들만 건드리면 됨.