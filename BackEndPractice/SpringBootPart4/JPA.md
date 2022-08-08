# JPA

Mybatis는 직접 쿼리를 작성해서, 자바 객체와 mapping시켜 객체로 변환해서 가져올 수 있었다.

JPA를 통해 똑같이 해보고 Mybatis와의 차이를 보자.

JPA는 Entity를 통해 Table과 객체를 Mapping시킬 수 있다.

1. Entity객체를 만들어보자.

```java
@Entity
@Table(name="customers")
public class CustomerEntity {
    @Id
    private long id;
    private String firstName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    private String lastName;
}
```

위처럼 Entity를 선언하고, Getter, Setter만 선언 해주면 , 쉽게 테이블과 객체를 Mapping 할 수 있다. 어떻게 선언만으로 Mapping할 수 있는지 보자!

레포지토리를 생성하는데, JPARepositrory를 상속한 인터페이스를 선언한다.

```java
public interface CustomerRepository extends JpaRepository<CustomerEntity,Long> {

}
```

JpaRepository는 CRUD연산을 할 수 있도록 구성되어있다.

이런 방식으로 객체를 만들었다면, 테스트를 통해 매핑이 잘 되었는지 확인해보자.

JPA테스트 클래스 시작

```java
@SpringBootTest
@Slf4j
public class JPATest {
    @Autowired
    CustomerRepository repository;

    //각 테스트 끝에 한번씩
    @AfterEach
    void tearDown(){
        repository.deleteAll();
    }
```

선언했었던 CustomerRepository를 AutoWired시키면서 CRUD연산을 가능하게 한다.

tearDown은 각 테스트가 끝날때마다 진행되는건데, 데이터가 쌓이는걸 방지하기 위해 delete하는것.

```java
@Test
    void INSERT_TEST(){
        //Given
        CustomerEntity customer = new CustomerEntity();
        customer.setId(1L);
        customer.setFirstName("honggu");
        customer.setLastName("kang");

        //when
        repository.save(customer);
        //Then

        CustomerEntity entity = repository.findById(1L).get();
        log.info("{} {}",entity.getFirstName(),entity.getLastName());
    }
```

선언했던 Entity를 이용해 객체들을 Table에 Mapping할 수 있따.

Entity와 JpaRepository를 이용해 Table과 편하게 매핑할 수 있을 뿐만아니라,

새로운 객체를 추가하고싶다? → Entity에 추가하고 getter setter 넣기만 하면 됨!! 훨씬 간단해짐.

(Mybatis였다면 xml바꾸고 해야해서 더 복잡함)

테이블이 Entity로 관리되기때문에 훨씬 보기 편하고 좋다.