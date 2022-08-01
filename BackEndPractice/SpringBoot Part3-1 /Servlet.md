# Servlet

## Web Server와 Web Application Server의 차이

동적컨텐츠를 지원하냐? 안하냐? 의 차이가 있다.(웹서버는 지원안하고 어플리케이션서버는 함)

![image.png](Servlet%20c1db19e525dc45569b8ca1cd459363d3/image.png)

### Web Server

HTTP WebServer 를 줄여서 웹 서버라고 부르는데, 

HTTP,HTTPS 프로토콜을 지원하는 서버를 뜻한다.

정적 컨텐츠만 지원한다.(Static Content를 포스팅하기 위해서)

WAS도 정적 컨텐츠를 보낼 수 있지만, 웹서버를 이용하면 Application Server까지 안가고 정적컨텐츠를 빠르게 보낼 수 있다는 장점이 있음.

### Web Application Server(WAS)

Web Server + Web Container라고 생각하면 됨.

Web서버의 기능을 구조적으로 분리하여 처리하기이ㅜ해 만들어짐.

DB조회나, 비즈니스 로직을 처리한다. 

동적인 컨텐츠를 처리하기 위해 만들어진 서버

WAS에 Web Application을 만들면, build를 해서, WAS에 Deploy한다. 

WAS는 별도의 프로세스로 구현되어있어서, 오픈소스를 다운받는 방식으로 사용 가능(tomcat등)

우리가 개발한걸 war형태로 was에 deploy한다.

# Servlet

서비스, 레포지토리, DB를 잇는건 성공했다. 

브라우저에서 서비스를 호출하려면? Servlet을 이용해야함

### Servlet은 클라이언트의 요청을 받아 서비스를 호출한다.

javax.servlet으로 사용할 수 있는데 , 웹 서버안에서 동작하는 자바 프로그램임. 

서블릿이 실제로 동작하기 위해서는 Web Container가필요한데, 그건 Web Server에 속해있으니까 Web Server > Web Container > Servlet이다. (내부에 있는거. 크다는거말구)

## MVC 패턴

Mode-View-Controller로 구성되어있는 패턴

웹 어플리케이션을 만들때 Model,View,Controller로 기능을 분리시켜서하는것. (관심사를 분리)

View: JSP(보이는부분) 시각적으로 데이터를 나타내기 위한 인터페이스 요소

Model : 애플리케이션의 정보, 데이터를 나타냄

Controller: 모델과 뷰를 연결

# Servlet의 LifeCycle

클라이언트가 웹서버로 요청, 웹서버가 WAS를 요청한다. 

WAS가 요청을 받아서, 요청에 해당하는 Thread를 만들음. (multithread)

Thread가 Survlet의 서블릿의 인스턴스에 있는 서비스의 메소드를 요청한다.

(쓰레드가 서블릿을 만드는게 아님)

서비스에서 doGet,doPost호출됨