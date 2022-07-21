# YAML으로 Property 만들기

## YAML이란? (Yet Another Markup Language)

(YAML Ain’t Markup Language)

가벼운 마크업 언어! 라고 생각해도 됨.

어플리케이션의 Property를 사용하기 위해 쓰인다.

1. application.yaml만들기

```java
kdt:
  version: "v1.0"
  minimum-order-amount: 1
  support-vendors:
    - a
    - b
    - c
    - d
  description: |
    line 1 hello world
    line 2 xxxx
    line 3
```

위와 같이 : 으로 구분해주고 들여쓰기 중요. : 뒤에 무조건 띄어쓰기 하나 들어가야함!

여러 라인을 한꺼번해 하고싶다면 | 을 넣고, 리스트는 - 를 이용해 선언해준다.

2.Spring Framework에서는 Yaml을 지원하지 않고 Boot에서만 지원하기 때문에 따로 Yaml PropertiesFactory클래스를 만들어줘야한다.

```java
public class YamlPropertiesFactory implements PropertySourceFactory {

    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {
        var yamlPropertiesFactoryBean  = new YamlPropertiesFactoryBean();
        yamlPropertiesFactoryBean.setResources(resource.getResource());

        var properties = yamlPropertiesFactoryBean.getObject();
        return new PropertiesPropertySource(resource.getResource().getFilename(),properties);

    }
}
```

위처럼 PropertySourceFactory를 Implement로 해서 선언해준다. 

1. OrderProperty의 annotation을 @ConfigurationProperty로 바꿔서 yaml을 읽을 수 있도록 한다.

```java
@Configuration
@ConfigurationProperties(prefix = "kdt")
public class OrderProperties implements InitializingBean { 
```

위처럼 선언한다. (”kdt”밑의 애들을 읽어라)

1. OrderProperties의 변수들이 각각의 클래스처럼 되어버린것!

→Getter,Setter만들기

 쓰고자하는 곳에서 주입받아서 쓰면된다.

Property를 그룹화시킬때 많이 쓴다.

5.OrderTester에서 이것들을 주입받아서 쓰기 위해서는

`var orderProperties = applicationContext.getBean(OrderProperties.class);`

을 이용해서 주입받으면 된다.