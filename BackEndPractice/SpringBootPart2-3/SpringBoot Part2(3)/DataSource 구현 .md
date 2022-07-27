# DataSource 구현

방금 구현했던 CustomerClass에 이은. Repository를 구현해보자.

1. Interface를 구현한다.

```java
public interface CustomerRepository {
    Customer insert(Customer customer);

    Customer update(Customer customer);

    //Customer save(Customer customer);<- 위에 두개를 묶어서 이렇게 쓰기도 한다.

    List<Customer> findAll();

    Optional<Customer> findById(UUID customerId);
    //찾으면 Customer반환, 못찾으면? NULL 반환하기때문에 Null반환할거면 Optional쓰기로했었음
    Optional<Customer> findByName(String name);
    Optional<Customer> findByEmail(String email);

    void deleteAll();
}
```

위처럼 인터페이스(나는 클래스의 뼈대라고 생각한다.)를 구현한다.

그걸 바탕으로, 레포지토리 클래스를 구현한다.

```java
public class CustomerJdbcRepository implements CustomerRepository {
    //DataSource에서 Connection 가져오기
    private static final Logger logger = LoggerFactory.getLogger(CustomerJdbcRepository.class);

    private final DataSource dataSource;
    public CustomerJdbcRepository(DataSource dataSource){
        this.dataSource = dataSource;
    }
    @Override
    public Customer insert(Customer customer) {
        return null;
    }
    @Override
    public Customer update(Customer customer) {
        return null;
    }
    @Override
    public List<Customer> findAll() {
        return null;
    }
    @Override
    public Optional<Customer> findById(UUID customerId) {
        return Optional.empty();
    }
    @Override
    public Optional<Customer> findByName(String name) {
        return Optional.empty();
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        return Optional.empty();
    }

    @Override
    public void deleteAll() {

    }
}
```

자동으로 만들어줌

## FindAll메소드를 만들어보자.

이전에 DriverManager를 이용해 만들었던걸 참고한다.

```java
@Override
public List<Customer> findAll() {
    List<Customer> allCustomers = new ArrayList<>();
    try(
            var connection = dataSource.getConnection();
            var statement = connection.prepareStatement("select * from customers");

            var resultSet = statement.executeQuery()
    )
```

connection선언을 dataSource를 이용해 해준다

statement, resultSet을 선언하는데 statement는 그냥 명령어 그대로 해준다.

```java
{
    while(resultSet.next()){
        var customerName = resultSet.getString("name");
        var email = resultSet.getString("email");
        var customerId = UUID.nameUUIDFromBytes(resultSet.getBytes("customer_id"));
        var lastLoginAt = resultSet.getTimestamp("last_login_at")!=null?
                resultSet.getTimestamp("last_login_at").toLocalDateTime(): null;

        var createdAt = resultSet.getTimestamp("Created_at").toLocalDateTime();
        allCustomers.add(new Customer(customerId, customerName,email,lastLoginAt,createdAt));
    }
}
```

각각 변수를 선언해준다.

그리고 클래스에 객체 자체를 add해준다.

```java
catch(SQLException throwable){
                logger.error("Got error while closing connection",throwable);
                throw new RuntimeException(throwable);
        }
            return allCustomers;

        }
```

에러를 잡아준다.

## 테스트해보자!!

```java
@SpringJUnitConfig

class CustomerJdbcRepositoryTest {
    @Configuration
    @ComponentScan(
            basePackages = {"org.prgrms.kdt.customer"}
    )
```

SpringJUnitConfig을 이용해서 테스트 해보자!

Configuration해주고, customer패키지를 scan하자.

```java
static class Config {
        //Repository(JdbcCustomerRepository)에 있는 Datasource의 Bean을 찾을듯
        @Bean
        public DataSource dataSource(){
            HikariDataSource dataSource = DataSourceBuilder.create()
                    .url("jdbc:mysql://localhost/order_mgmt")
                    .username("root")
                    .password("1234")
                    .type(HikariDataSource.class)
                    .build();
```

HikariDataSource를 이용한 DataSource를 리턴한다.

## FindAll 테스트

```java
@Test
    @DisplayName("전체고객을 조회할 수 있다람쥐.")
    public void testFindAll() throws InterruptedException {
        var customers = customerJdbcRepository.findAll();
        assertThat(customers.isEmpty(),is(false));
       
    }
```

customers에 findall한거를 넣어준다.

비어있는지 아닌지 확인한다!