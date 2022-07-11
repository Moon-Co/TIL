# 1.Dependency

## 1.외부 라이브러리 사용하기

Gradle = 외부 라이브러리를 사용한다→ 외부 의존성이 있다.

실습해보자.

외부 라이브러리 가져오기

프로젝트 생성→Build.Gradle클릭→dependencies 부분에 인터넷에서 가져온 dependencies코드를 복붙하면 된다.

## lombok라이브러리 가져오기

구글에 lombok을 검색해, dependencies에 복붙하고, plugin에 들어가 Lombok plugin을 다운받는다.

### lombok의 기능

```java
public class User {
    private int age;
    private String name;
}
```

User라는 클래스를 선언했다고 하자.

`@AllArgsConstructor` 모든 Argument가 포함된 생성자를 만들어 준다.

`@NoArgsConstructor` → 기본생성자를 만들어준다. 

`@Setter` `@Getter` →Getter,Setter를 만들어 준다.

`@ToString` → String으로 출력해준다.

`@Data`->위에것들이 한방에 들어가있는 것들. 

이처럼 기본적으로 만들어야되는 메소드들을 자동으로 만들어주는 기능이 있다.