# SpringJPA 1

### JPA가 어떤것인지 알아고보, JPA를 Maven을 사용해 세팅해보자.

스프링 어플리케이션 통해서 DataBase계층에 접근하는법

- JDBC Tempate사용
- 쿼리매퍼 사용
- ORM(JPA)사용

## 실습을 하기 전에!

Dependency를 추가하자.

1. JDBC dependency

```java
<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>

        </dependency>
```

자바에서 데이터베이스에 접속할 수 있도록 하는 자바 API이다.

JDBC는 데이터베이스에서 자료를 쿼리하거나, 업데이트 하는 방법을 제공

(쿼리 : 데이터베이스에 정보를 요청하는것)

2.mybatis

```java
<dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>2.2.0</version>
        </dependency>
```

자바의 관계형 데이터베이스 프로그래밍을 좀 더 쉽게 할 수 있게 도와준다.

JDBC를 통해 데이터베이스에 접근하는 작업을 캡슐화하고, JDBC코드 및 매개변수의 중복작업을 제거.

1. h2

```java
<dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
        
```

자바기반의 오픈소스 관계형 데이터베이스 관리 시스템이다.(DBMS)

브라우저 기반의 콘솔모드를 이용할 수 있어서 이번에는 이걸쓴다.

4.lombok

```java
<dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
```

getter,setter 등의 메서드 작성코드를 줄여주는 라이브러리.

### JDBC가 잘 연결되었는지 테스트코드를 작성해보자

테스트 폴더에 JDBC테스트 폴더를 만들고, 다읔과같이 작성하자. 

```java
@Slf4j
public class JDBCTest {
    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_URL = "jdbc:h2:~/test";
    static final String USER = "sa";
    static final String PASS = "";
    
    @Test
    void jdbc_sample(){
        try {
            Class.forName(JDBC_DRIVER);
            Connection connection = DriverManager.getConnection(DB_URL,USER,PASS);
            log.info("GET CONNECTION");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
```

@Slf4j ⇒  lombok 이용할때, logging을 위해 넣어주는 annotation

h2로 연결하기 위해서는 DriverManager.getConnection에 URL과 USER,PASS를 위처럼 넣어줘야한다.

JDBC는 

1. Class.forName을 이용해 드라이버를 로드한다.

Class.forName(JDBC_DRIVER) ⇒ “org.h2.Driver”를 로드 하겠다.

1. DriverManager.getConnection을 통해 연결을 얻는다.
2. 만들어진 connection은 statement객체를 생성하는데 사용됨.
3. sql코드를 string에 저장하면!(`String sql = "select * from tablename";`이런거를 sql이라는 String에 저장하면)
4. 3번에서 만들어진 connection을 이용해 객체의 결과를 받는다.

```java
Class.forName("oracle.jdbc.driver.OracleDriver");
conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:orcl", "scott", "tiger");
stmt = conn.createStatement();
String sql = "select * from tablename";
rs = stmt.executeQuery(sql);
```

이게 JDBC기본 사용법이었다.

이걸 생각하면서, JDBC를 이용해 테이블을 하나 추가해보자.

```java
@Slf4j
public class JDBCTest {
    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_URL = "jdbc:h2:~/test";
    static final String USER = "sa";
    static final String PASS = "";

    static final String DROP_TABLE_SQL = "DROP TABLE customers IF EXISTS";
    static final String CREATE_TABLE_SQL = "CREATE TABLE customers(id SERIAL, first_name VARCHAR(255), last_name VARCHAR(255))";
    static final String INSERT_SQL = "INSERT INTO customers (id,first_name,last_name) VALUES(1,'honggu','kang')";
    @Test
    void jdbc_sample(){
        try {
            Class.forName(JDBC_DRIVER);
            Connection connection = DriverManager.getConnection(DB_URL,USER,PASS);
            log.info("GET CONNECTION");

            Statement statement = connection.createStatement();
            statement.executeUpdate(DROP_TABLE_SQL);
            //존재한다면 테이블을 날려라
            statement.executeUpdate(CREATE_TABLE_SQL);
            log.info("CREATE TABLE");
						statement.executeUpdate(INSERT_SQL);
            log.info("INSERTED CUSTOMER INFORMATION");
		
            ResultSet resultSet = statement.executeQuery("SELECT * FROM customers WHERE id = 1");

            while(resultSet.next()){
                String fullName = resultSet.getString("first_name")+" "+resultSet.getString("last_name");
                log.info("CUSTOMER FULL_NAME : {}", fullName);
            }

            statement.close();
            connection.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
```

SQL명령어를 String으로 처음에 선언을 하고

connection.createStatement를 이용해 statement를 선언한다.

그 상태에 executeUpdate를 통해 테이블을 삭제하고 생성한다. 

INSERT로 삽입까지 해보고, ResultSet을 생성해보자.

전달한 쿼리를 ResultSet이 받는다!!!

마지막엔 close까지 해줘야함!

# 너무 복잡하지 않나요?

데이터를 하나 넣기 위해 쿼리를 너무 많이 쓰고, 예외처리도 할게 정말 많다.

그래서 Spring은 JDBC Template을 통해 위의 코드(데이터계층 접근)를 더 단순화 할 수 있도록 했다.

Spring-boot-starter-data-jdbc를 dependency로 넣으면 jdbc템플릿을 이용할 수 있었다!

JDBC Template을 이용해보자.

JDBC Template을 이용할때는 Annotation을 많이 이용한다

통합 테스트를 위해 @SpringBootTest를 이용해보자.

통합테스트란?

실제 운영환경에서 사용될 클래스를 통합하여 테스트.

기능검증을 위한게 아닌, 플로우가 제대로 동작하는지 검증하기 위해 사용.

Bean을 전부다 load하기 때문에 운영환경과 가장 유사하다.

하지만 전부다 load하기 때문에 시간이 오래걸린다.

```java

@Slf4j
@SpringBootTest
public class JDBCTest {
  
    static final String DROP_TABLE_SQL = "DROP TABLE customers IF EXISTS";
    static final String CREATE_TABLE_SQL = "CREATE TABLE customers(id SERIAL, first_name VARCHAR(255), last_name VARCHAR(255))";
    static final String INSERT_SQL = "INSERT INTO customers (id,first_name,last_name) VALUES(1,'honggu','kang')";
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void jdbcTemplate_sample(){
        jdbcTemplate.update(DROP_TABLE_SQL);
        jdbcTemplate.update(CREATE_TABLE_SQL);
        log.info("CREATE TABLE USING JDBC TEMPLATE");
    }
}
```

위처럼 간단하게 선언할 수 있다.

jdbcTemplate은 Autowired로 Bean을 전부 가져오고, (의존성을 주입)

update라는 명령어로 간단히 할 수 있다.

근데 Driver연결은 어디서했나요?

resources에 application.yml이라는 파일을 만들어준 후, 

```yaml
spring:
  datasource:
    driver-class-name: org.h2.Driver
    url: jdbc:h2:~/test;
    username: sa
    password:
```

위처럼 야멀 파일을 입력해주면 자동으로 연결이 된다!!

INSERT도 같은 방식으로 해줄 수 있다.

```java
jdbcTemplate.update(INSERT_SQL);
        log.info("INSERTED CUSTOMER INFORMATION USING JDBC TEMPLATE");

```

위처럼 간단하게 해줄 수 있고,

다음은 쿼리를 전달하는 것이다.(데이터베이스에 요청) 

이거를 통해 데이터베이스에서 이름을 얻어올 수 있다!

```java
String fullName = jdbcTemplate.queryForObject(
                "SELECT * FROM customers WHERE id =1 ",
                (resultSet, i)-> resultSet.getString("first_name")+
                        " "+resultSet.getString("last_name"));

        log.info("FULL_NAME:{}", fullName);
    }
```