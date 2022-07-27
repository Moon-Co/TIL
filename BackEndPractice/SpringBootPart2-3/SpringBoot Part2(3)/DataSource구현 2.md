# DataSource구현 2

# mapToCustomer

```java
private void mapToCustomer(List<Customer> allCustomers, ResultSet resultSet) throws SQLException {
        var customerName = resultSet.getString("name");
        var email = resultSet.getString("email");
        var customerId = toUUID(resultSet.getBytes("customer_id"));
        var lastLoginAt = resultSet.getTimestamp("last_login_at")!=null?
                resultSet.getTimestamp("last_login_at").toLocalDateTime(): null;

        var createdAt = resultSet.getTimestamp("Created_at").toLocalDateTime();
        allCustomers.add(new Customer(customerId, customerName,email,lastLoginAt,createdAt));
    }
```

반복되는 부분을 메소드로 선언해준다.

# findByName

```java
public Optional<Customer> findByName(String name){
List<Customer> allCustomers = new ArrayList<>();
        try (
                var connection = dataSource.getConnection();
                var statement = connection.prepareStatement("select*from customers WHERE name = ?");
        ) {
            statement.setString(1, name);
            try (var resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    mapToCustomer(allCustomers, resultSet);
                }
            }
        } catch (SQLException e) {
            logger.error("Got Error while closing connection", e);
            throw new RuntimeException(e);
        }
        return allCustomers.stream().findFirst();

    }
```

방식은 DriverManager를 사용할때랑 비슷하다. 

# findByName Test

```java
@Test
    @DisplayName("이름으로ㅓ 조회할 수 있다람쥐.")
    public void testFindByName() throws InterruptedException {
        var customer = customerJdbcRepository.findByName("new-user");
        assertThat(customer.isEmpty(),is(false));

        var unknown = customerJdbcRepository.findByName("unknown-user");
        assertThat(unknown.isEmpty(), is(true));
    }
```

이런식으로 테스트 해줄 수 있다. 

# Insert

```java
@Override
public Customer insert(Customer customer) {
    try (
            var connection = dataSource.getConnection();
            var statement = connection.prepareStatement("INSERT INTO customers(customer_id,name,email,created_at) VALUES(UUID_TO_BIN(?), ?, ?, ?)");
    ) {
        statement.setBytes(1,customer.getCustomerId().toString().getBytes());
        statement.setString(2,customer.getName());
        statement.setString(3,customer.getEmail());
        statement.setTimestamp(4, Timestamp.valueOf(customer.getCreatedAt()));
        var executeUpdate = statement.executeUpdate();
        if(executeUpdate != 1){
            throw new RuntimeException("Nothing was inserted");
        }
        return customer;
    } catch (SQLException e) {
logger.error("Got Error while closing connection", e);
        throw new RuntimeException(e);
    }

}
```

4개의 요소 (아이디, 이름, 이메일, 생성시기)를 파라미터로넣어서 statement를 생성해준다.

# InsertTest

```java
@Test
    @DisplayName("고객을 추가할 수 있습니까불이")
    public void testInsert() throws InterruptedException {
        customerJdbcRepository.deleteAll();
        var newCustomer = new Customer(UUID.randomUUID(),"newnew-user","newnewuser@gmail.com", LocalDateTime.now());
        customerJdbcRepository.insert(newCustomer);
        var retrieveCustomer = customerJdbcRepository.findById((newCustomer.getCustomerId()));
        assertThat(retrieveCustomer.isEmpty(), is(false));
        assertThat(retrieveCustomer.get(), samePropertyValuesAs(newCustomer));
    }
```

이런식으로 테스트 한다. (타입을 잘 생각하면서 해야한다.)

# Update

```java
@Override
    public Customer update(Customer customer) {
        try (
                var connection = dataSource.getConnection();
                var statement = connection.prepareStatement("UPDATE customers SET name = ?, email = ?, last_login_at=? WHERE customer_id= UUID_TO_BIN(?)");
        ) {
            statement.setString(1,customer.getName());
            statement.setString(2,customer.getEmail());
            statement.setTimestamp(3,customer.getLastLoginAt()!=null?Timestamp.valueOf(customer.getLastLoginAt()):null);
            statement.setBytes(4,customer.getCustomerId().toString().getBytes());
            var executeUpdate = statement.executeUpdate();
            if(executeUpdate != 1){
                throw new RuntimeException("Nothing was updated");
            }
            return customer;
        } catch (SQLException e) {
            logger.error("Got Error while closing connection", e);
            throw new RuntimeException(e);
        }

    }

```

코드는 위와같다.

# TestUpdate

```java
@Test
    @DisplayName("고객을 수정할 수 있다..")
    @Order(6)
    public void testUpdate() throws InterruptedException {
        newCustomer.changeName("updated-user");
        customerJdbcRepository.update(newCustomer);

        var all = customerJdbcRepository.findAll();
        assertThat(all, hasSize(1));
        assertThat(all,everyItem(samePropertyValuesAs(newCustomer)));

        var retrievedCustomer = customerJdbcRepository.findById(newCustomer.getCustomerId());
        assertThat(retrievedCustomer.isEmpty(),is(false));
        assertThat(retrievedCustomer.get(),samePropertyValuesAs(newCustomer));

    }
```

테스트는위처럼해준다.

### 만약 테스트 전에 모든걸 지우고, Customer객체를 하나 생성해놓고 싶다면!

```java

@BeforeAll
//클래스 자체를 인스턴스로 만들어버리면 static안써도 됨!
void setup(){
    newCustomer = new Customer(UUID.randomUUID(),"newnew-user","newnewuser@gmail.com", LocalDateTime.now());
    customerJdbcRepository.deleteAll();
}
```

이건 클래스 바로 밑에 모든 메소드에 적용되도록 해야하므로, static을 쓰는게 맞다! 하지만

static으로 해서 deleteAll이 안된다.(deleteAll은 static이 아니니까) 그럴때는

`@TestInstance(TestInstance.Lifecycle.*PER_CLASS*)`

이 코드를 클래스 상단에 Annotation화 해서 달아놓으면 클래스자체가 Instance가 되면서 모든 메소드가 하나의 인스턴스로 묶여 static을 쓰지 않아도 된다.

클래스를 이상태에서 실행하면, 순서가 뒤죽박죽으로 되기때문에 안된다.

`@TestMethodOrder(MethodOrderer.OrderAnnotation.class)`

이 Annotation을 달아줘서 순서를 정해준다. 

클래스마다

```java
@Test
@Order(1)
public void testHikariConnectionPool(){
assertThat(dataSource.getClass().getName(),is("com.zaxxer.hikari.HikariDataSource"));
}
```

이런식으로 @Order를 달아주면 순서를 정할 수 있다.