# Spring 트랜잭션 매니저 실습

스프링은 Platform Transaction Manager를 통해서 트랜잭션 관리를 한다.

트랜잭션 계층은 JDBC를 이용해서 commit, rollback이런식으로 했었다.

Transaction을 DataSource를 이용해서 사용해보자.

Test 클래스에

```java
@Bean
        public PlatformTransactionManager platformTransactionManager(DataSource dataSource){
            return new DataSourceTransactionManager(dataSource);
        }
```

위처럼 PlatformTransactionManager를 Bean으로 설정해두고, 

```java
private final PlatformTransactionManager transactionManager;
```

위와같이 CustomerNamedJdbcRepository에 PlatformTransactionManager를 선언하자.

```java
public CustomerNamedJdbcRepository(PlatformTransactionManager transactionManager, TransactionTemplate transactionTemplate, NamedParameterJdbcTemplate jdbcTemplate){
        this.transactionManager = transactionManager;
        this.transactionTemplate = transactionTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }
```

위처럼 생성자에도 추가해주고,

이제 트랜젝션을 이용해서 UPDATE해보자. 

```java
public void testTransaction(Customer customer){

        var transaction = transactionManager.getTransaction(new DefaultTransactionDefinition());
        //트랜젝션을 이용해서 Name바꾸고, email바꾸고 해보자.
        try{
            jdbcTemplate.update("UPDATE customers SET name = :name, email = :email,WHERE customer_id= UUID_TO_BIN(:customerId)",
                    toparamMap(customer));
            jdbcTemplate.update("UPDATE customers SET name = :name, email = :email,WHERE customer_id= UUID_TO_BIN(:customerId)",
                    toparamMap(customer));
            //업데이트가 성공되면 커밋!
            transactionManager.commit(transaction);
        }catch(DataAccessException e){
            logger.error("got Error", e);
            //에러가 나면 롤백!
            transactionManager.rollback(transaction);
        }

    }
```

transction객체를 선언햐주고,

try-catch를 이용해서 업데이트 두개 다 성공해야 커밋하고, 예외가 나올경우, Rollback되도록 한다.

# Transaction Template 사용

Transaction Template를 사용하기 위해 Test클래스의 Bean으로 등록해준다.

```java
@Bean
        public TransactionTemplate transactionTemplate(PlatformTransactionManager platformTransactionManager){
            return new TransactionTemplate(platformTransactionManager);
        }
```

Template을 이용하면 코드가 훨씬 간단해진다.

```java
public void testTransaction(Customer customer){
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus transactionStatus) {
                jdbcTemplate.update("UPDATE customers SET name = :name, email = :email,WHERE customer_id= UUID_TO_BIN(:customerId)",
                        toparamMap(customer));
                jdbcTemplate.update("UPDATE customers SET name = :name, email = :email,WHERE customer_id= UUID_TO_BIN(:customerId)",
                        toparamMap(customer));
            }
        });
        //리턴하는게 없으면, without result

  }
```

위처럼 선언해주는데

`transactionTemplate.execute(new TransactionCallbackWithoutResult() {`

이렇게 transactionTemplate 인터페이스를 실행해준다.

이렇게 하면, 다음과 같은 테스트를 했을때, transaction에 의해 에러가 난다.

```java
@Test
    @DisplayName("트랜젝션 테스트")
    @Order(7)
    public void testTransaction() throws InterruptedException {
        var prevOne = customerJdbcRepository.findById(newCustomer.getCustomerId());
        assertThat(prevOne.isEmpty(), is(false));
        var newOne = new Customer(UUID.randomUUID(),"a","A@gmail.com",LocalDateTime.now());
        var insertedOne = customerJdbcRepository.insert(newOne);
        customerJdbcRepository.testTransaction(
                new Customer(insertedOne.getCustomerId(),
                        "b",prevOne.get().getEmail(),
                        newOne.getCreatedAt()));
        var maybeNewOne = customerJdbcRepository.findById(insertedOne.getCustomerId());
        assertThat(maybeNewOne.isEmpty(),is(false));
        assertThat(maybeNewOne.get(),samePropertyValuesAs(newOne));

    }
```

왜냐면, 

`assertThat(maybeNewOne.isEmpty(),is(false));`

에 의해, maybeNewOne이 생성되지 않았다는걸 확인할 수 있다.