# WebApplicationContext

Application Context를 상속하고있다, Servlet Context에 접근할 수 있는 기능이 추가됨.

→서블렛들에 접근할 수 있는 Application Context

디스패처 서블릿은 WebApplicationContext를 이용하여 자신을 설정함.

Servlet Context : 여러 서블릿이 사용할 수 있는 공용자원

디스패처 서블릿과, 서블릿은 모두 Servlet Context에 접근 가능.

모든 서블릿이 ServletContext에 접근 가능하듯이,

모든 Application Context에 접근 가능한 root Application Context가 필요함.

Servlet Context가 만들어질 때, root Application Context도 만들어짐.

Root WebApplication Context는 

서로 다른 ServletContext에서 같이 쓸 빈 들을 등록 후 같이 사용할 수 있음.

Root Application context 만드는방법.

1. web.xml로 만들기
2. 자바 코드기반으로 만들기

스프링은, Servlet Context Listener를 구현한, Context LoaderListener를 구현한다.

## 웹 환경에서 스프링 어플리케이션이 동작하는 방식

서블릿 컨테이너 안에 웹 어플리케이션이 동작하고 있고, 그 내부에 Servlet, Servlet Context 존재

웹 어플리케이션에서, Web Application Context를 생성하고,

만약 Dispatcher Servlet을 요청했다면, Dispatcher Servlet은 Web APplication에서 컨트롤러를 조회한다.

Application Context에는 Bean을 생성하기위한 메타정보가 있다. 

이거를 계층구조로 살펴보면, 

프레젠테이션 계층, 서비스계층, 데이터 엑세스 계층 으로 나뉨

- 프레젠테이션 계층

사용자의 요청을 처리하는 부분이다. 

외부와 소통하는 역할

- 서비스 계층

실제 비즈니스 로직

- 데이터 엑세스 계층

DAO(Data Access Object)나 Repository를 이용해서 DB에 접근한다.

Spring MVC 는 프레젠테이션 계층을 나타낸다. 

AOP,MODEL 을 서비스계층

JDBC template 을 데이터엑세스 계층으로

이걸 application Context로 나누면, 

Dispatcher Servlet에서 사용하는 Controller를 Servlet Application context로 놓는다.

⇒ Servlet Application Context에 Controller를 등록한다!

Root Application Context는 여러곳에서 다같이 사용하니까

서비스계층, 데이터엑세스 계층을 포함한다.

---

# 실습해보자.

WebApplication Context 컨테이너에 모든 Bean이 들어가있는데, 

실제로 Root Application Context를 만들고, 거기 내부의 service와 데이터 엑세스(레포지토리)를 관리하도록 해보고, 

Dispatcher Servlet에 들어간 application Context에는 MVC관련된 내용만 등록하고, 

Root Application Context와 Dispatcher Servlet을 부모자식으로 연결시켜보자.

1. Root Application Context를 만들기 위해 , onStartUpclass 에

Context Loader Listener를 추가해보자.

new ContextLoaderListener를 넣으면, 파라미터로 WebApplicationContext의 context를 넣어줘야함.

2.RootConfig를 따로 만들어보자.

데이터소스, JdbcTemplate, Transaction Manager가 들어간 Config를 만든다.

이렇게되면, 서블렛 컨텍스트,  루트 컨텍스트가 따로 생김!

1. 실행해보자.

```java
@Override
    public void onStartup(ServletContext servletContext)  {

        var rootApplicationContext = new AnnotationConfigWebApplicationContext();
        rootApplicationContext.register(RootConfig.class);
        var loaderListner = new ContextLoaderListener(rootApplicationContext);
        servletContext.addListener(loaderListner);

        var applicationContext = new AnnotationConfigWebApplicationContext();
        applicationContext.register(ServletConfig.class);
        //직접 applicationContext만들고, 이제 디스패치 서블릿 전달!
        var dispatcherServlet = new DispatcherServlet(applicationContext);

        logger.info("starting Server..");
        var servletRegistration = servletContext.addServlet("test",dispatcherServlet);
        servletRegistration.addMapping("/");
        servletRegistration.setLoadOnStartup(0);

    }

}
```

3줄씩 나뉜다.

root context는 loadListener에 넣어주고,

서블렛은 application Context에 넣어준다.

그 context를 dispatcherServlet에 넣어서 컨트롤러를 선택해줌.