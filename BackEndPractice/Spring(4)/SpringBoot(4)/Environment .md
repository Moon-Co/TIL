# Environment

# Environment

Application Context에서는 Enviorment를 제공한다.

Enviorment= 해당 Application이 처한 상황.(개발환경, test환경)

무슨상황?

Application Context는 Bean들을 관리한다. 

Bean→ 영항을 주는 무언가들.

## Properties

Application개발할때,  DB 접속정보, 서버포트등을 Application속성 properties로 정해진다.

코드에 직접 작성을 하면, 배포할때마다 변경해야하고, 보안정보들은 코드에 노출되면 안되기 때문에 외부 속성으로 관리하고 Application이 읽어오도록 한다.

## 파일 작성해보기.

Property파일은 대체로 Key-Value방식으로 작성한다. 

```java
version = v1.0.0

kdt.version = v1.0.0

kdt.support-vendors = a, b, c, d, e, f, g

kdt.minimum-order-amout = 1
```

이런식으로!

적용은 Appconfiguration 들어가서 

```java
@PropertySource("application.properties")

public class AppConfiguration {

}
```

이런식으로 한다!

Tester에서 Property가 잘 들어갔는지 출력해보자.

```java
public class OrderTester {
    public static void main(String[] args) {

        var applicationContext = new AnnotationConfigApplicationContext(AppConfiguration.class);

        var environment = applicationContext.getEnvironment();
        var version =  environment.getProperty("kdt.version");
        var minimumOrderAmount = environment.getProperty("kdt.minimum-order-amount",Integer.class);
        var supportVendors = environment.getProperty("kdt.support-vendors", List.class);
        System.out.println(MessageFormat.format("version->{0}",version));
        System.out.println(MessageFormat.format("minimumOrderAmount->{0}",minimumOrderAmount));
        System.out.println(MessageFormat.format("supportVendors->{0}",supportVendors));
        System.out.println();
```

위처럼 getEnvironment를 이용해 Environment객체를 만들 수 있고, 거기서 getProperty를 이용해 Property를 볼 수 있다.

```java
version->v1.0.0
minimumOrderAmount->1
supportVendors->[a, b, c, d, e, f, g]
```

결과는 위처럼나온다.

만약 Property를 OrderService에서 사용하고 싶거나, 다른 Component에서 Property를 가져다 쓰고싶으면?

Enviroment를 주입해도 되지만,

Property key를 field에 주입시킬수도 있다. → Value annotation을 사용

OrderProperty Class를 만들어보자.

```java
@Component
//자동 bean등록 위함
public class OrderProperties implements InitializingBean {
    @Value("v1.1.1")
    private String version;
    @Value("123")

    private int minimumOrderAmount;
    @Value("아아")
    private List<String> supportVendors;

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println(MessageFormat.format("[OrderProperty]version->{0}", version));
        System.out.println(MessageFormat.format("[OrderProperty]minimumOrderAmount->{0}", minimumOrderAmount));
        System.out.println(MessageFormat.format("[OrderProperty]supportVendors->{0}", supportVendors));

    }
}
```

위와같이 @Value를 이용하면 Propertyr값을 필드 내에서 고칠 수 있다.

```java
@Value("${kdt.version}")
    private String version;

```

위처럼 application property에 있는 좌표를 그대로 입력해준다면, 그곳에 있는 값이 나온다.

(v1.0.0)

```java
@Value("${kdt.version2}")
    private String version;
```

생뚱맞은 이름을 입력할경우 

`[OrderProperty]version->${kdt.version2}` 처럼 이름이 그대로 출력되는데,

```java
@Value("${kdt.version2:v0.0.0}")
    private String version;
```

위처럼 :v0.0.0 콜론 뒤에 값을 넣어주면, 이상한 값을 넣을때 디폴트값으로 v0.0.0을 출력해준다.

특정한 환경변수, 특정한 시스템 속성들,Application에 필요한 속성을 제공해주는 목적을 갖는 Class를 만들어서 거기에 Property Source를 define할 수 있다.

VersionProvider라는 클래스를 만들어 보자.

```java
@Component
@PropertySource("version.properties")
public class VersionProvider {
    private final String version;

    public VersionProvider(@Value("${version:v0.0.0}")String version) {
        this.version = version;
    }
    public String getVersion(){
        return version;
    }
```

위처럼 버전을 리턴하는 클래스를 만들어, 다른 클래스에서 버전을 불러오고 싶을때 이 클래스를 이용하게끔 만들수 있다.