# JDBC Template

지금까지의 코드를 보면, connection을 맺는부분, 예외처리 하는부분이 계속 반복되었다.

스프링은 반복되는 부분과 변경이 되는 부분을 Template, Callback패턴을 이용해서 해결한다.

JDBC Template을 이용해서 코드를 변경해보자.

JDBC 탬플릿은 데이터소스만 있으면 됨.

하기 전! Test에서 JdbcTemplate을 @Bean으로 추가해주자.

```java
@Bean
public JdbcTemplate jdbcTemplate(DataSource dataSource) {
    return new JdbcTemplate(dataSource);
}
```

JDBC Repository를 들어가서

```java
private static final RowMapper<Customer> rowMapper = (resultSet, i) -> {
    var customerName = resultSet.getString("name");
    var email = resultSet.getString("email");
    var customerId = toUUID(resultSet.getBytes("customer_id"));
    var lastLoginAt = resultSet.getTimestamp("last_login_at") != null ?
            resultSet.getTimestamp("last_login_at").toLocalDateTime() : null;

    var createdAt = resultSet.getTimestamp("Created_at").toLocalDateTime();
    return new Customer(customerId, customerName, email, lastLoginAt, createdAt);

};
```

mapToCustomer 메소드에는 List<Customer>가 들어가있는데 그게 필요 없어서 따로 RowMapper를 넣어준다.

모든 객체들을 만들어주는 Mapper

# Find All

```java
@Override
    public List<Customer> findAll() {
        return jdbcTemplate.query("select * from customers", rowMapper);
    }
```

코드는 이렇게 간단한 한줄로 끝나버린다.

# Find By Name

```java
@Override
    public Optional<Customer> findById(UUID customerId) {
        try{
        return Optional.ofNullable(jdbcTemplate.queryForObject("select*from customers WHERE customer_id = UUID_TO_BIN(?)",rowMapper,
                customerId.toString().getBytes()));
        }catch(EmptyResultDataAccessException e){
            logger.error("Got Empty Result",e);
            return Optional.empty();
        }
    }
```

코드는 위와 같은데, 리턴값이 optional이기 때문에, Optional.ofNullable()을 사용하고

`jdbcTemplate.queryForObject("select*from customers WHERE customer_id = UUID_TO_BIN(?)",rowMapper,
                customerId.toString().getBytes()));`

위와같이 쓴 이유는, 인자가 안에 들어가면, queryForObject를 사용하는데, 

queryForObject(SQL명령어,타입)적어주면, 인자가 하나인건 자동으로 해준다.

이런식으로 Count도 매우 쉽게 만들 수 있는데,

```java
public int count(){
        return jdbcTemplate.queryForObject("select count(*) from customers",Integer.class);

    }
```

이렇게 짧은 명령어로 만들어 줄 수 있다.