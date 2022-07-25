# JUnit

### JAVA에서 실제 테스트코드를 어떻게 작성하는지 보자.

JUnit이라는 OpenSource Framework를 이용할거다.

- 매 단위 테스트마다, 테스트 클래스의 인스턴스 생성됨. → 독립적인 테스트 가능
- Annotation을 제공, 테스트 라이프사이클을 관리하게 해주고, 테스트코드를 간결하게 해준다.
- Test Runner를 사용, 인텔리제이/이클립스/메이븐 등에서 테스트코드 쉽게 실행하게 해줌.
- assert로 테스트케이스 수행결과 판별시켜준다.
- 결과는 성공, 실패중 하나로 표현된다.

FixedAmountVoucher에 대한 Testcode를 작성해보자.

class이름에 마우스를 대로 option+enter치면 Create Test나온다.

- setUp @Before → 테스트를 시작하기 전에 해야하는 셋업
- teatDown@After →리소스를 정리하거나 cleanup해야하는 일.

테스트 코드를 만들었을때, Test Method가 나오는데

```java
@Test
void nameAssertEqual() {
    assertNotEquals(2,1+1);
}
```

AssertNotEqual에 대해 알아보자.

첫번째 인자 → 기대되는값(2)

두번째 인자 →실제 값(1+1)

이게 같으면 에러가 난다.

```java
@Test
void discount() {
    var sut = new FixedAmountVoucher(UUID.randomUUID(),100);
assertEquals(900,sut.discount(1000));
}
```

객체를 만들어주고, 바우처 넘버와 할인양을 넣어준다.

assertEquals를 이용해 다르면 에러가 나게끔! (기댓값과 실제값이)

## 근데, 실패하는경우도 무조건 생각해야한다.

```java
@Test
    @DisplayName("할인금액은 마이너스가 될 수 없다.")
    void testWithMinus() {
        assertThrows() 
        var sut = new FixedAmountVoucher(UUID.randomUUID(),-100);

    }
```

할인금액이 -가 되는 경우를 생각해보자. 

→ assertThrows를 사용해야한다.

```java
@Test
    @DisplayName("할인금액은 마이너스가 될 수 없다.")
    @Disabled
    void testWithMinus() {
        assertThrows(IllegalArgumentException.class,()->
                new FixedAmountVoucher(UUID.randomUUID(),-100));

    }
```

이런식으로 assertThrows 객체를 하나 만들어준다.

이상태로만 실행하면, Throw된게 하나도 없다고 나온다.

```java
public FixedAmountVoucher(UUID voucherId, long amount) {
    if(amount<0) throw new IllegalArgumentException("Amount should be positive");
    this.voucherId = voucherId;
    this.amount = amount;
  }
```

그래서 테스트 하는 대상의 생성자에 위와같은 뭐를 throw할건지 선언해줘야한다.

`@Disabled`-> 테스트메소드를 스킵할때 사용하는 어노테이션

## BeforeEach, BeforeAll

테스트 해보자.

```java
class FixedAmountVoucherTest {
    private static final Logger logger = LoggerFactory.getLogger(FixedAmountVoucher.class);
    @BeforeAll
    static void setup(){
        logger.info("@BeforeAll-단 한번 실행");
    }
    @BeforeEach
    void init(){
        logger.info("@BeforeEach -매 테스트마다 실행");
    }
```

위처럼 setup을 해주고, (static으로) annotation을 각각 다르게 붙여준다.

(BeforeAll, Before Each)

BeforeEach는 테스트메소드가 실행될때마다 실행되는거고,

BeforeAll은 테스트 클래스가 실행될때 단 한번만 실행된다.

 Logger를 통해 결과를 볼건데, 그러기 위해서는 logback.xml을 메인폴더에서 복사해와야한다.

### 결과

```markdown
2022-07-25 12:49:46.323  INFO   --- [           main] o.prgrms.kdt.voucher.FixedAmountVoucher  :
 @BeforeAll-단 한번 실행
2022-07-25 12:49:46.337  INFO   --- [           main] o.prgrms.kdt.voucher.FixedAmountVoucher  :
 @BeforeEach -매 테스트마다 실행
2022-07-25 12:49:46.362  INFO   --- [           main] o.prgrms.kdt.voucher.FixedAmountVoucher  :
 @BeforeEach -매 테스트마다 실행
2022-07-25 12:49:46.366  INFO   --- [           main] o.prgrms.kdt.voucher.FixedAmountVoucher  :
 @BeforeEach -매 테스트마다 실행
```

런을 하면 결과창에 위와같이 나온다.

테스트 매소드가 3개라서 beforeEach가 3개가 나온거임.

# EdgeCase

할인되는 금액이, 할인금액보다 크면?(예외상황 가정) (1000원짜리사야하는데, 바우처 2000원일떄) →0원이 나와야함.

```java
@Test
    @DisplayName("할인된 금액은 마이너스가 될 수 없다.")
    void testMinusDiscountAmount() {
        var sut = new FixedAmountVoucher(UUID.randomUUID(),1000);
        assertEquals(0,sut.discount(900));
    }
```

위처럼 900원짜리를 1000원깎는경우를 보자.

결과가 -100이 나오므로 에러가 나오는데, 

```java
public long discount(long beforeDiscount) {
  var discountedAmount = beforeDiscount-amount;

  return (discountedAmount<0)?0:discountedAmount;

}
```

위처럼 바우처 메소드를 고쳐주는걸로 해결할 수 있다.

⇒다양한 예외를 예상해서 테스트를 만들어줘야함.

다양한 예외를 assertAll을 이용해서 한꺼번에 적용시켜보자.

```java
@Test
    @DisplayName("유효한 할인 금액으로만 생성할 수 있다.")
    void testVoucherCreation() {
        assertAll("FixedAmountVoucher creation",
                ()-> assertThrows(IllegalArgumentException.class,()->
                                new FixedAmountVoucher(UUID.randomUUID(),0)),
                ()-> assertThrows(IllegalArgumentException.class,()->
                                new FixedAmountVoucher(UUID.randomUUID(),-100)),

                ()-> assertThrows(IllegalArgumentException.class,()->
                        new FixedAmountVoucher(UUID.randomUUID(),100000))
        );
```

위처럼 assertAll과 람다함수를 통해서 다양한 경우들을 동시에 예외처리할 수 있다. 이에 따라 원래 클래스의 생성자에도  throw 할게 필요하다.

```java
public FixedAmountVoucher(UUID voucherId, long amount) {
    if(amount<0) throw new IllegalArgumentException("Amount should be positive");
    if(amount==0) throw new IllegalArgumentException("Amount can't be 0");
    if(amount>MAX_VOUCHER_AMOUNT) throw new IllegalArgumentException("Amount should be small");
```