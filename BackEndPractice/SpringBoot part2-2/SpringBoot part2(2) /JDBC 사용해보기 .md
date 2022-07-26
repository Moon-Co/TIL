# JDBC 사용해보기

JDBC를 이용해서 Java Application과 데이터베이스를 연결해보자.

연결 후 null일때 connection을 꼭 close하는게 포인트!

예외가 날 수 있으니 try catch문으로 넣어주자.

```java
public class JdbcCustomerRepository {
    private static final Logger logger = LoggerFactory.getLogger(JdbcCustomerRepository.class);
    public static void main(String[] args) throws SQLException {
```

클래스 선언하기. 로그를 확인하기 위해 로거를 선언해주자.

```java
Connection connection = null;
Statement statement = null;
ResultSet resultSet = null;
```

Connection, Statement, ResultSet을 선언할건데 초기값을 null로 놓고 시작하자.

```java
try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt","root", "1234");
            statement = connection.createStatement();
            resultSet = statement.executeQuery("select *from customers");
```

try-catch를 사용하기 위해 try를 넣어주고, 

connection : DriverManager를 통해 연결이 되기때문에, DriverManager.getConnection 그리고 로컬 저장소 링크를 입력한다.

statement : connection 후 상태를 생성한다.

resultSet : 생성된 상태에 쿼리를 실행하는데, customers기반으로 실행

```java
while(resultSet.next()){
    var name  = resultSet.getString("name");
    var customerId = UUID.nameUUIDFromBytes(resultSet.getBytes("customer_id"));
logger.info("customername ->{}, customer id -> {}",name,customerId);
}
```

name과 손님아이디에 접근한후, 로거를 이용해 출력한다.

```java
catch (SQLException e) {
            logger.error("Got Error while closing connection",e);
            throw new RuntimeException(e);
        }
```

예외가 발생했을때의 행동을 지정한다.

```java
finally{
            try {
                if (connection != null) connection.close();
                if (statement != null) statement.close();
                if(resultSet != null) resultSet.close();
            }
```

변수들이 null이 된다면, close 해준다 . (중요!) 계속 접속유지하고있으면 메모리 사용됨

```java
catch(SQLException exception){
                logger.error("Got error while closing connection",exception);
            }
```

이것도 에러를 감지한다.

너무 복잡하기때문에 간단하게 바뀜! (자바 10부터)

```java
public class JdbcCustomerRepository {
    private static final Logger logger = LoggerFactory.getLogger(JdbcCustomerRepository.class);
    public static void main(String[] args) throws SQLException {

        try (
            var connection = DriverManager.getConnection("jdbc:mysql://localhost/order_mgmt","root", "1234");
            var statement = connection.createStatement();
            var resultSet = statement.executeQuery("select *from customers");
        ){
        while(resultSet.next()){
            var name  = resultSet.getString("name");
            var customerId = UUID.nameUUIDFromBytes(resultSet.getBytes("customer_id"));
            logger.info("customername ->{}, customer id -> {}",name,customerId);
            }}
        catch (SQLException e) {
            logger.error("Got Error while closing connection",e);
            throw new RuntimeException(e);
        }

        }
    }
```

위처럼 선언해도 된다. (Autoclosable)