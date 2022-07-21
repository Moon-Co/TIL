# Resource

스프링 애플리케이션을 만들때, 외부 리소스를 읽어올때가 있음!(이미지, 텍스트, 암복호화를 위한 키파일 등)

파일을 다양한곳에서 가져올 수 있지.(File, URI, URL등)

다양한 위치를 스프링은 인터페이스를 통해 하나의 API로 제공해준다.

리소스는, ResourceLoader에서 받아온다.(인터페이스)

getResource로 리소스를 가져옴.

Resource는 다양한 구현체를 제공해준다.

스프링 공식 사이트를 보면 알 수 있는데

ApplicationContext도 구현체중 하나다.

## ApplicationContext를 이용해서 Resource를 가져와보자.