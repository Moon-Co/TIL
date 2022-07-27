# DataSource

이전시간에는 DriverManager를 이용해서  getConnection 명령어와 함께 DB에 접근했었다.→Connection객체 반환

매번 Connection을 생성하고close하면 과정에서 많은 Resource를 소모함.

→Connection Pool을 이용하면 됨.

Spring에서는 **DataSource** 를 이용해 Connection을 얻거나 반납할 수 있다.

Connection Pool: 데이터베이스와 연관된 Connection을 미리 Pool에 저장해놓고, 필요할때마다 가져와서 쓰고 반납하는 방법.

Connection Pool에서 데이터를 가져오는 행위 : DataSource가 한다!

Close를 Connection Pool에 반환하는 거라고 생각하면 됨 이경우에는! 

이걸 사용하기 위해서는 SimpleDriverDataSource가 필요한데

(테스트용으로)

pom.xml에서 Dependency를 추가해줘야한다.

```
<dependency>
   <groupId>org.springframework.boot</groupId>
   <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
```

실제로는 다른거 쓸거임!

# HikariCP

사용하기전 데이터베이스를 가져오는 작업.

1. Customer Class 만들기.

이전시간에 이용했던 데이터베이스(customer_id.. name..)이번엔 자바로 표현해보자.

```java
public class Customer {
    private final UUID customerId;
    private String name;
    private final String email;
    private  LocalDateTime lastLoginAt;
    private final LocalDateTime createdAt;
```

클래스를 선언해주고, 절대 안변할것같은것들은 final을 붙여준다.

```java
public Customer(UUID customerId,String name, String email, LocalDateTime createdAt) {
        this.customerId = customerId;
        this.email = email;
        this.createdAt = createdAt;
        this.name = name;
    }

    public Customer(UUID customerId, String name, String email, LocalDateTime lastLoginAt, LocalDateTime createdAt) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.lastLoginAt = lastLoginAt;
        this.createdAt = createdAt;
    }
```

생성자를 만드는데, 다양한 형태로 만들 수 있음.(용도에따라)

```java
public UUID getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
```

Getter를 다 만들어주는데, Setter는 아무렇게나 만드는것보다는 

좀더 용도가 잘 보이게끔 만들어주는게 좋다.

SetName보다는 → changeName이 더 보기좋으니,

```java
public void changeName(String name){
		this.name = name;
}
```

인데, name이 blank면 안된다는 조건도 넣으면, 

 

```java
public void changeName(String name){
	if(name.isBlank()){
				throw new RuntimeException("Name should not be blank");
		}
		this.name = name;
}
```

이렇게 된다.

Customer 생성시에도 name이 비어있으면 안되므로, 

```java
public void validate(String name){
    if(name.isBlank()){
        throw new RuntimeException("Name should not be blank");
    }
}
```

이 메소드를 만들어 준뒤, 

```java
public Customer(UUID customerId, String name, String email, LocalDateTime lastLoginAt, LocalDateTime createdAt) {
        validate(name);
...
...
```

생성자에 한줄 추가한다.