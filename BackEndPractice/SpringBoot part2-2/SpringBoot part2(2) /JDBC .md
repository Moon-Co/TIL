# JDBC

JDBC(Java DataBase Connectivity)

JavaApplication과, MySQL같은 데이터베이스를 연결해주는것

JavaAPplication  ←JDBC→Databace

다양한 타입이 있는데, 우리는 MYSQL을 이용하는 JDBC type4를 쓸거다.

![image.png](JDBC%20fe07fb3e569f4e7eb6b386d85bc1314e/image.png)

위와같은 구조로 SQL과 JAVA를 연결하는데,

JDBC API는 Connection, Statement, Result로 이루어져있고,

DriverManager를 통해서 Connection을 받아온다!

실습을 하기전, JDBC의 FLOW를 보자.

![image.png](JDBC%20fe07fb3e569f4e7eb6b386d85bc1314e/image%201.png)