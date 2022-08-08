# Mybatis

쿼리 매퍼인 Mybatis를 이용하면, Query와, 자바 코드를 분리할 수 있다.

(JDBC의 반복적인 작업을 쿼리매퍼 Mybatis가 대신 수행해준다.

Mybatis는 Annotation을 이용하는 방식과 xml을 이용하는 방식 2가지가 있는데,

xml을 이용하는 방식으로 연습해봅시다!!

1. XML파일 만들기

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.example.kdtchapter1.CustomerMapper">
    <insert id="save">
        INSERT INTO customers (id, first_name, last_name)
        VALUES (#{id}, #{firstName}, #{lastName})
    </insert>

    <update id="update">
        UPDATE customers
        SET first_name=#{firstName},
            last_name=#{lastName}
        WHERE id = #{id}
    </update>

    <select id="findById" resultType="customers">
        SELECT *
        FROM customers
        WHERE id = #{id}
    </select>

    <select id="findAll" resultType="customers">
        SELECT *
        FROM customers
    </select>
</mapper>
```

이렇게 각각 명령어(findById, find All등등..)에 해당하는 sql명령어를 지정해준다.

`<mapper namespace="com.example.kdtchapter1.CustomerMapper">`

위 명령어를 이용해 어디에있는 mapper를 쓸건지 지정해준다.(경로 적어줌)

mybatis사용을 위해 yaml코드도 작성해준다.

```yaml
mybatis:
  type-aliases-package: com.example.kdtchapter1.domain
  configuration:
    map-underscore-to-camel-case: true
    default-fetch-size: 100
    default-statement-timeout: 30
  mapper-locations: classpath:mapper/*.xml
```

mapper코드를 입력해주고,

underscore 입력형식을 카멜케이스로 바꿔주고,

mapper를 넣어준다.

이렇게 되면, CustomerMapper 인터페이스를 만들 수 있다.

save,findById,update,findAll을 적어줄 수 있는데,

```java
@Mapper
public interface CustomerMapper {
    void save(Customer customer);
    Customer findById(long id);
}
```

위처럼 두개만 적어보았다. 그리고 테스트를 해보자.

(xml을 이용해서 쿼리를 전달할 수 있는 상태.)  직접 코드를 적을 필요가 없다는 장점

```java
@Test
    void save_test(){
        jdbcTemplate.update(DROP_TABLE_SQL);
        jdbcTemplate.update(CREATE_TABLE_SQL);

        customerMapper.save(new Customer(1L,"honggu","kang"));
        Customer customer =customerMapper.findById(1L);

        log.info("fullName : {} {}",customer.getFirstName(),customer.getLastName());
    }
```

save를 할때 객체를 생성해서, 전달할 수 있다는 큰 장점을 얻게된다.