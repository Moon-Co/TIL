# Hamcrest

```java
public class HamcrestAssertiontests {
    @Test
    @DisplayName("여러 hamcrest matcher테스트")
    void hamcrestTest(){
        assertEquals(2, 1+1);
        assertThat(1+1,anyOf(is(1),is(2)) );

        assertThat(1+1,not(equalTo(6)));
    }
    @Test
    @DisplayName("컬렉션에 대한 matcher 테스트")
    void hamcrestListMatcherTest(){
        var prices = List.of(2,3,4);
        assertThat(prices,hasSize(3));
        assertThat(prices,everyItem(greaterThan(0)));
        assertThat(prices,containsInAnyOrder(3,4,2));

    }

}
```

위처럼 assertEquals으로도 써도 되지만, 밑의 메소드처럼 컬렉션이 있을때는 햄크래스트를 써도 좋다. (별건없음)