# UUID

customer생성시 나타나는 UUID와 조회시 나타나는 UUID가 다르다는 문제점이 생겼다.

(UUID는 16비트인데 조회시 byte단위로 처리하기때문에 바뀐다.)

해결을 위해 byteBuffer를 사용해서 byte단위로 UUID를 넣어준 후 getLong()을 2번 하는 방법으로 처리한다.

/코드

```java
static UUID toUUID(byte[] bytes){
    var byteBuffer = ByteBuffer.wrap(bytes);

    return new UUID(byteBuffer.getLong(),byteBuffer.getLong());

}
```

위와같은 메소드를 하나 만들어주고, 

```java
var customerId = toUUID(resultSet.getBytes("customer_id"));
```

위처럼 ID를 바꿔준다. 끝.