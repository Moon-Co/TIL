# JDBC사용해보기 2

WHERE 절을 추가해보자!

sql 문장

`var resultSet = statelet.executeQuery(”select *from customers”)`

을 문자열 조합으로 바꿔준다.

`var resultSet = statement.executeQuery(”SELECT_SQL”)`

로 바꾸고!

`void SELECT_SQL = “select * from customers”`로 바꿔준다.

### 이름을 찾는 방식으로 만들어보자.

```java
public List<String> findNames(String name) {
        var SELECT_SQL = "select*from customers WHERE name = '%s'".formatted(name);

        List<String> names = new ArrayList<>();
```

위와같이 메소드를 하나 선언!

```java
try (
                var connection = DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt", "root", "1234");
                var statement = connection.createStatement();
                var resultSet = statement.executeQuery(SELECT_SQL);
        ) {
            while (resultSet.next()) {
                var customerName = resultSet.getString("name");
                var customerId = UUID.nameUUIDFromBytes(resultSet.getBytes("customer_id"));
                var createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();
                names.add(customerName);
                logger.info("customername ->{}, customer id -> {}, createdAt ->{}", name, customerId, createdAt);
            }
} catch (SQLException e) {
            logger.error("Got Error while closing connection", e);
            throw new RuntimeException(e);
        }
        return names;
```

메인에 있던 코드들을 전부 저 메소드로 올려준다.

이름을 이용해서 찾고싶으면

```java
 public static void main(String[] args) throws SQLException {
        var names = new JdbcCustomerRepository().findNames("tester01')
        names.forEach(v ->logger.info("Found name:{}", v));
    }
}
```

이름을 이용해서 데이터가 있는지 찾을 수 있다.

근데! SQL INJECTION이 OR을 이용해서 발생할 수 있음.

```java
public static void main(String[] args) throws SQLException {
        var names = new JdbcCustomerRepository().findNames("tester01' OR 'a'='a");
        names.forEach(v ->logger.info("Found name:{}", v));
    }
}
```

위처럼, a=a같은 무조건 참인거를 넣어주면

tester01, tester02,tester03이 다 찾아짐. 이걸 없애기 위해서,

Prepare Statement를 이용한다. → Query를 중간에 바꿀 수 없다. , 성능상의 이점이 있다.

웬만하면 Prepare Statement를 이용하자!

```java
public class JdbcCustomerRepository {
    private static final Logger logger = LoggerFactory.getLogger(JdbcCustomerRepository.class);

    public List<String> findNames(String name) {
        var SELECT_SQL = "select*from customers WHERE name = ?";

        List<String> names = new ArrayList<>();
        try (
                var connection = DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt", "root", "1234");
                var statement = connection.prepareStatement(SELECT_SQL);
        ) {
            statement.setString(1,name);
            try(var resultSet = statement.executeQuery()){
                while (resultSet.next()) {
                    var customerName = resultSet.getString("name");
                    var customerId = UUID.nameUUIDFromBytes(resultSet.getBytes("customer_id"));
                    var createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();
                    names.add(customerName);
                    logger.info("customername ->{}, customer id -> {}, createdAt ->{}", name, customerId, createdAt);
                }
            }
        } catch (SQLException e) {
            logger.error("Got Error while closing connection", e);
            throw new RuntimeException(e);
        }
        return names;

    }
```

위처럼 만드는데, statement를 prepareStatement로 만들어 주고,

setString(1,name) 이런식으로 해주면 좋다.

다양한 메소드를 만들어서 데이터베이스를 사용할 수 있다.

## 전부 출력하기

`private final String SELECT_ALL_SQL = "select*from customers";`

를 클래스내에 선언한다.

```java
public List<String> findAllNames() {

        List<String> names = new ArrayList<>();
        try (
                var connection = DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt", "root", "1234");
                var statement = connection.prepareStatement(SELECT_ALL_SQL);
                var resultSet = statement.executeQuery();
        ) {
            while (resultSet.next()) {
                var customerName = resultSet.getString("name");
                var customerId = UUID.nameUUIDFromBytes(resultSet.getBytes("customer_id"));
                var createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();
                names.add(customerName);

            }
        } catch (SQLException e) {
            logger.error("Got Error while closing connection", e);

        }
        return names;

    }
```

위와같이 SELECT_ALL_SQL을 prepareStatement에 넣는다. 

끝까지 쭉 훑으면서 이름, id, 생성시기를 변수로 선언한다.

이름만 출력할거니까 names를 리턴해준다.

메인함수에 

```java
var names = customerRepository.findAllNames();
names.forEach(v ->logger.info("Found name:{}", v));
```

위처럼 메인함수에 선언해주면 된다.

## Insert Customer

`private final String INSERT_SQL = "INSERT INTO customers(customer_id,name,email) VALUES(UUID_TO_BIN(?), ?, ?)";`

위와같이 3개의 파라미터로 이루어진 요소를 클래스 내에 선언해준다.

```java
public int insertCustomer(UUID customerId, String name, String email) {
        try (
                var connection = DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt", "root", "1234");
                var statement = connection.prepareStatement(INSERT_SQL);
        ) {
            statement.setBytes(1,customerId.toString().getBytes());
            statement.setString(2,name);
            statement.setString(3,email);
            return statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Got Error while closing connection", e);
        }
        return 0;
    }
```

위처럼, prepareStatement에 INSERT_SQL을 넣어주고, 연결해 statement를 만들어준다.

statement에 파라미터들을 넣어준다. 

메인함수에는

```java
customerRepository.insertCustomer(UUID.randomUUID() ,"new-user","new-user@gmail.com");
```

위처럼 파라미터를 정해서 넣어준다.

## DELETE All Customers

클래스에 변수 선언은 다음과 같이 해준다.

```java
private final String DELETE_ALL_SQL = "DELETE FROM customers";
```

```java
public int  deleteAllCustomers(){
        try (
                var connection = DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt", "root", "1234");
                var statement = connection.prepareStatement(DELETE_ALL_SQL);
        ) {
            return statement.executeUpdate();
        }catch(SQLException e){
            logger.error("got error while closing connection",e);
        }
        return 0;
    }
```

업데이트만 해주면 다 삭제된다! 몇번 삭제했는지 리턴해준다.

메인함수에는

```java
var count = customerRepository.deleteAllCustomers();
```

## UpdateCustomerName

클래스내에서는 

```java
private final String UPDATE_BY_ID_SQL = "UPDATE customers SET name = ? WHERE customer_id= UUID_TO_BIN(?)";
```

위처럼 선언해주고

메소드는 

```java
public int updateCustomerName(UUID customerId, String name){
        try (
                var connection = DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt", "root", "1234");
                var statement = connection.prepareStatement(UPDATE_BY_ID_SQL);
        ) {
            statement.setString(1,name);
            statement.setBytes(2,customerId.toString().getBytes());
            return statement.executeUpdate();
        } catch (SQLException e) {
            logger.error("Got Error while closing connection", e);
        }
        return 0;
    }
```

위처럼 선언해준다.

```java
customerRepository.updateCustomerName(customer2, "updated-user2");
```

메인함수는 위처럼  사용해 업데이트 해준다.