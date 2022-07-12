# 데이터베이스(2)

### 클라우드 컴퓨팅의 장점

- 초기투자비용이 줄어든다.(매달 비용 내면됨)
- 리소스 준비를 위한 대기시간 감소

### AWS란?

가장 큰 클라우드 컴퓨팅 업체

## MySQL을 이용해보자!

1. 도커를 깐다.
2. 터미널에 들어가 다음과 같은 과정으로 명령어를 입력한다. 

```java
docker pull mysql/mysql-server:8.0
//도커 이미지 다운로드

docker run --name=mysql_container --restart on-failure -d
mysql/mysql-server:8.0
//다운받은 이미지로 도커 실행

docker logs mysql_container 2>&1 | grep GENERATED
//도커의 루트 패스워드 찾기

docker exec -it mysql_container mysql -uroot -p
//찾은 루트 패스워드를 이용해 도커 접속
```

1. mysql실행시작!

데이터베이스는 폴더와 테이블로 이루어져 있다고 했다.

![Untitled](%E1%84%83%E1%85%A6%E1%84%8B%E1%85%B5%E1%84%90%E1%85%A5%E1%84%87%E1%85%A6%E1%84%8B%E1%85%B5%E1%84%89%E1%85%B3(2)%200dfcadfac4174526a51e5ceb262a5bb9/Untitled.png)

`SHOW DATABASES;` 명령어를 입력하면 데이터베이스들이 나온다.

`CREATE DATABASE prod;`명령어를 입력하면 prod라는 폴더가 추가된다.

`USE prod;`prod 라는 데이터베이스로 입장!

`SHOW TABLES;`테이블이 어떤게 있는가 확인!

## AWS를 이용해서 MySQL사용해보기.

내일 알려준다고 한다.. ㅇㅏ이디를 안줌

## DDL과 예제 데이터 소개

DDL 테이블에 대해서!

- 사용자 ID : 인물 등록을 하면, 사람 구분을 위한 유니크한 식별자.
- 세션 ID: 사용자의 방문을 논리적으로 나눠 부여되는 ID (세션 : 사용자의 방문을 논리적으로 나눈 것)
→하나의 사용자는 여러개의 세션 ID를 가질 수 있음.
세션을 나누는 기준 : Google Analystic 기준으로 나누자.
세션을 생성했더라도, 그 사이트에서 30분정도 아무것도 하지 않고 있다 다시 행동을 할 경우, 새로운 세션을 연다.
- 세션은 어떤용도로 사용하는가: 세션정보를 파악해 광고를 띄우거나 할 수 있음. (마케팅에 사용)

### 예제

사용자 ID 100번이 3개의 세션을 만들었다. 

각 세션이 만들어질때 마다, 어떤 접점(채널)이 기록되는지 보자!

![Untitled](%E1%84%83%E1%85%A6%E1%84%8B%E1%85%B5%E1%84%90%E1%85%A5%E1%84%87%E1%85%A6%E1%84%8B%E1%85%B5%E1%84%89%E1%85%B3(2)%200dfcadfac4174526a51e5ceb262a5bb9/Untitled%201.png)

세션 1 : 구글 키워드 광고로 시작한 세션. 세션의 채널정보 : 구글

세션2: 페이스북 광고로 시작한 세션. 새로운 세션이 열린다. (30분이 지나지 않아도 열림.)
세션 채널정보 : 페이스북

세션3 : 네이버 광고로 시작한 세션. (30분이 지났고, 새로운 세션 열림) 
세션 채널정보 : 네이버

1명의 사용자가 3개의 세션을 만들었고, 3개의 채널정보를 만들었다.

이런 정보를 바탕으로 마케팅 전략을 정할 수 있다. 

## 관계형 데이터베이스 Table로 표현해보기

Data modeling이라고 부른다.

![Untitled](%E1%84%83%E1%85%A6%E1%84%8B%E1%85%B5%E1%84%90%E1%85%A5%E1%84%87%E1%85%A6%E1%84%8B%E1%85%B5%E1%84%89%E1%85%B3(2)%200dfcadfac4174526a51e5ceb262a5bb9/Untitled%202.png)

세션 정보table이 이렇다고 하자.

![Untitled](%E1%84%83%E1%85%A6%E1%84%8B%E1%85%B5%E1%84%90%E1%85%A5%E1%84%87%E1%85%A6%E1%84%8B%E1%85%B5%E1%84%89%E1%85%B3(2)%200dfcadfac4174526a51e5ceb262a5bb9/Untitled%203.png)

관계형 데이터베이스는 2단계 구조를 가진다. 

session table = 이전테이블과 유사하지만, channel table을 따로 만들었다. 

id 값이, 한 세션을 유일하게 식별 ⇒ primary key

channel_id = channel table내에서는 primary key ⇒ foreign key. (session테이블에서는 primary 아니지만, channel에서는 Primary key)

NOT NULL : 테이블에 값이 무조건 있어야함. (0과 NULL은 다름)

DEFAULT value: 필드에 값이 주어지지 않았을 때, 기본값 정의.

timestamp : default를 current timestamp로 지정하면, 현재 시간을 기본값으로 정해준다.

이런걸 다 이용해서 데이터베이스를  만들어 본다면!

![Untitled](%E1%84%83%E1%85%A6%E1%84%8B%E1%85%B5%E1%84%90%E1%85%A5%E1%84%87%E1%85%A6%E1%84%8B%E1%85%B5%E1%84%89%E1%85%B3(2)%200dfcadfac4174526a51e5ceb262a5bb9/Untitled%204.png)