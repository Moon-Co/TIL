# Transaction Annotation

Transaction Annotation을 이용해 CostumerService를 만들어보자.

CustomerService를 만들기 위해 Interface를 먼저 만들자

```java
public interface CustomerService {
    //Customer를 만드는데, 중간에 한개라도 잘못되면 전체가 Rollback 되도록!
    void createCustomers(List<Customer> customers);
}
```

인터페이스를 이용해 클래스를만들면

```java
public class CustomerServiceImpl implements CustomerService{
    //customer service는 customer Repository를 당연히 사용
    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
    //Customers를 전달받으면, customers를 순회하면서 insert를 호출한다.
    @Override
    @Transactional
    public void createCustomers(List<Customer> customers) {
        customers.forEach(customerRepository::insert);
    }
}
```

customer Service는 Repository를 사용할것이다. 

생성자 파라미터로 레포지토리를 넣어주고,

Customers를 전달받으면 customers를 순회하며 insert를 호출하는 방식으로 service를 진행할건데

Transaction을 annotation으로 선언한다

@Transactional 로 트랜젝션 처리를 한다.

 

이제 여러개를 Insert할 수 있는지 Test해주는 메소드를 선언해보자.

```java
@Autowired
CustomerService customerService;
```

위처럼 CustomerServiceTest클래스에 

CustomerService를 Autowired해주고,

```java
@Autowired
    CustomerRepository customerRepository;
```

위처럼 레포지토리도 autowired를 해줘야한다.

customerRepository를 autowired하려하는데, 

bean이 customerJdbcRepository랑 customerNamedJdbcRepository 두개 가 주입되고 있어서 안된다! →1개만 주입되어야하는데.

해결을 위해customerService, CustomerRepository를 component scan에서 빼버리고

 test할때 필요한것들만 Bean에 넣어버리는 방법 고른다.

Bean들 사이에

```java
@Bean
public CustomerRepository customerRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate){

    return new CustomerNamedJdbcRepository(namedParameterJdbcTemplate);
}
@Bean
public CustomerService customerService(CustomerRepository customerRepository){
    return new CustomerServiceImpl(customerRepository);
}
```

를 추가한다.

위처럼 선언하면 주입이 무엇이든지 Autowired가 잘 된다.

```java
@Test
    @DisplayName("여러건 추가 테스트")
    void multiInsertTest(){
        var customers = List.of(
                new Customer(UUID.randomUUID(),"A","a@gmail.com",LocalDateTime.now()),
                new Customer(UUID.randomUUID(),"B","b@gmail.com",LocalDateTime.now())
        );

        customerService.createCustomers(customers);
        var allCustomerRetrieved = customerRepository.findAll();
        assertThat(allCustomerRetrieved.size(),is(2));
        assertThat(allCustomerRetrieved,containsInAnyOrder(samePropertyValuesAs(customers.get(0)),samePropertyValuesAs((customers.get(1)))));

    }
}
```

테스트 코드는 위와 같다. 

customerService를 이용해 customer를 만들고, 

모든 customers가 잘 만들어졌는지 점검한다.

이거는 잘되었을 경우

이제 잘 안되었을 경우 Rollback되는지 테스트하는 코드를 해보자.

각각 반복시마나, 데이터를 지우는 코드를 먼저 추가해보자.

```java
@AfterAll
    static void cleanup() {
        embeddedMysql.stop();
    }
```

이제 테스트 코드를 적어보자.

```java
@Test
    @DisplayName("여러건 추가 실패시 전체 트렌젝션 롤백되어야한다는 테스트")
    void multiInsertRollbackTest(){
        var customers = List.of(
                new Customer(UUID.randomUUID(),"c","c@gmail.com",LocalDateTime.now()),
                new Customer(UUID.randomUUID(),"d","c@gmail.com",LocalDateTime.now())
        );
        try{
            customerService.createCustomers(customers);}
        catch(DataAccessException e){}
        var allCustomerRetrieved = customerRepository.findAll();
assertThat(allCustomerRetrieved.size(),is(0));
assertThat(allCustomerRetrieved,is(true));
assertThat(allCustomerRetrieved,not(containsInAnyOrder(samePropertyValuesAs(customers.get(0)),samePropertyValuesAs((customers.get(1))))));
    }
}
```

이처럼 같은 이메일을 2번 삽입하는 코드를 작성해보면, 트렌젝션에 의해 Rollback이 이루어져야함.

이걸 하기위해서는 Annotation이 필요한데, 

첫째로 Service에 있던 @Transactional

```java
@Service
public class CustomerServiceImpl implements CustomerService{
    //customer service는 customer Repository를 당연히 사용
    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
    //Customers를 전달받으면, customers를 순회하면서 insert를 호출한다.
    @Override
    @Transactional
    public void createCustomers(List<Customer> customers) {
        customers.forEach(customerRepository::insert);
    }
}

```

둘째로, Test클래스에 @EnableTransactionManagement

```java
@Configuration
    @EnableTransactionManagement
    static class Config {
        //Repository(JdbcCustomerRepository)에 있는 Datasource의 Bean을 찾을듯
        @Bean
        public DataSource dataSource() {
            HikariDataSource dataS
...
...
```

위처럼 어노테이션을 달아주면, 매번 트랜젝션 코드를 달아줄 필요 없이 자동으로 처리되어서 매우 편하다.