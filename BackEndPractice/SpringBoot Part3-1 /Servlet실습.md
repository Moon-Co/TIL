# Servlet실습

Servlet을 연결하는 실습을 해보자. (IntelliJ를 이용해 Web Server에 연결)

1. Dependency를 추가한다

```java
<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>
```

1. TestServlet을 만든다. (어떤 웹 사이트를 만들것인가?)

```java
public class TestServlet extends HttpServlet {
    private static final Loggerlogger= LoggerFactory.getLogger(TestServlet.class);

    @Override
    public void init() throws ServletException {
        super.init();
logger.info("INIT Servlet");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var requestURI = req.getRequestURI();
logger.info("Got Request From {}", requestURI);

        //HTTP에서 getMethod하면 위 doGet이 호출됨.
        //req을 이용해서 요청에 있는 내용을 꺼내서 보자.

        var writer = resp.getWriter();
        writer.println("Hello Servlet!");
        //resp을 가지고 응답을 write할 수 있다.;
        //Servlet 요청은 Container가 한다!
        //Container에다가 TestServlet클래스가 존재한다고 알려줘야함!!!!
        //Web.xml을 이용.
    }
}

```

TestServlet을 선언했을 때, doGet을 자동으로 쓰도록 하고, 거기에 Hello Servlet을 쓸 수 있도록 하자.

doGet을 사용하면, req과 resp을 쓸 수 있는데, URI에서 Request해봐서, HelloServlet을 돌려준다.

톰캣을 다운받아서 EditConfiguration에서 톰캣으로 실행하도록 설정 후, 여러가지 방법이 있지만 

@WebServlet을 사용해보자.

서블렛 위에

`@WebServlet(value = "/*",loadOnStartup = 1)`

를 입력해주면 , 자동으로 웹서버와 연결해주고, 

/*을 벨류로 넣었기때문에 도메인 뒤에 아무거나 입력해도 똑같은 사이트가 뜬다.

하지만 이것은 올드한 방법이라고 한다. 새로운 방법을 조만간 배운다고함.

###