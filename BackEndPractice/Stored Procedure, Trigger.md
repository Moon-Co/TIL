# Stored Procedure, Trigger

- Stored Procedure 란?
    - View와 비슷한데(가상테이블), View보다 강력하다.
    - MySQL 서버단에 저장되는  SQL쿼리들
    → CREATE PROCEDURE사용.
    - IN⇒ Stored Procedure 에 값넣는것
    OUT⇒ Stored Procedured에서 리턴할 값
    - 프로그래밍 언어의 함수처럼 인자 넘기기 가능
    - 간단한 If, Case, loop등의 구문 가능
    
- 정의 문법
- 

```sql
DELIMITER//
CREATE PROCEDURE procedure_name(parameter_list)
##Stored Procedure LOGIC

BEGIN
	statements;
END//(or $$)
DELIMETER

```

- 호출문법

```sql
CALL stored_procedure_name(argument_list);
```

- 정의 예시

```sql
DELIMITER//
CREATE PROCEDURE return_session_details()
BEGIN
	SELECT*
	FROM test.seungsik_session_details;
END//
DELIMETER;
```

- 호출 예시

```sql
CALL return_session_details()
```

→ begin 과 end사이에 select들이 모인 집합

- IN Parameter

```sql
DROP PROCEDURE IF EXISTS return_session_details;
DELIMITER//
CREATE PROCEDURE return_session_details(IN channelName varchar(64))
## 입력 파라미터가 있다.(channel name)
BEGIN
	SELECT *
	FROM test.seungsik_session_details
	WHERE channel = channelName;
## View에 있는 record값중에 channel의 값이 channelName인것만 리턴!
END//
DELEMITER;
```

StoredProcedure를 하면, 변수를 IN, OUT할 수 있어서 리턴되는 SELECT의 결과가 달라진다.

- INOUT Parameter

```sql
DROP PROCEDURE IF EXISTS return_session_count;
DELIMITER//
CREATE PROCEDURE return_session_count(IN channelName varchar(64),INOUT
totalRecord int)
BEGIN
	SELECT COUNT(1) INTO totalRecord FROM test.seungsik_session_detail())
	WHERE channel = channelName;
END//
DELIMITER;

SET @facebook_count =0;
CALL return_session_count('Facebook',@facebook_count)
## 첫번째 인자 : 채널이름, 2번째 인자 : INOUT Parameter => 변수를 줘야함. 
SELECT @facebook_count;
## 주어진 채널이름을 만족하는 것들의 개수.
```

`SELECT COUNT(1) INTO totalRecord FROM test.seungsik_session_detail())`

→ 주어진 channelName을 만족하는 레코드가 몇개이냐?

## Stored Function이란?

레코드를 리턴하는게 아닌! 값 하나를 리턴해주는 함수.(특정 데이터베이스 밑에 등록됨)
그런데 서버단에 등록됨.

- 용도에따라 리턴값은 Deterministic 또는 Non Deterministic이다.
→Deterministic : 같은 입력에 대해서는 같은 출력 나가는것.
→ Non Deterministic : 입력이 같아도 출력이 달라질 수도 있다.(Random함수같은거)
- IN 파라미터만 받을 수 있다.(INOUT, OUT 안됨.)
- Store procedure는 Call키워드 써서 불러야하는데, Store Funtion은 SELECT안에서 사용 가능.
→마치 Native 함수처럼 호출 가능.

## Stored Function 예시

```sql
DELIMITER $$ ##($$이 나오면 END인걸로 하자!)
CREATE FUNCTION test.Channel_Type(channel varchar(32))
##무조건 값을 하나 리턴해서 리턴문이 따라와야함. 
RETURNS VARCHAR(20)
DETERMINISTIC ## DETERMINISTIC인지 NON DETERMINISTIC인지 정해
BEGIN
	DECLARE channel_type VARCHAR(20);
	IF channel in('Facebook', 'Instagram', 'Tiktok')THEN
		SET channel_type = 'Social Network' 
	ELSEIF channel in('Google','Naver') THEN
...
```

### Stored function 호출

SELECT안에서 사용된다.

```sql
SELECT channel, test.Channel_TYPE(channel)
FROM prod.channel;
```

# Trigger

- CREATE TRIGGER 명령으로 만든다.
- 테이블의 레코드를 추가/삭제/수정할경우 작업이 일어나기 전/후에 특정 LOGIC을 실행하도록!
- 어떤 작업인지? 작업 전인지? 후인지? 이걸 고려해놔야함.
- LOGIC안에서는, INSERT의 경우는 새로운 레코드를 Access할 방법이 필요→ NEW필요
- DELETE 기존에 있는거를 없애야 하기 때문에→ OLD필요
- UPDATE : DELETE 하고 INSERT하기 때문에 NEW OLD 다필요

### 트리거 예시

만약 중요한 테이블의 레코드를 누군가 변경할경우!

→ 레코드에 변경이 생길 때마다, 변경전의 레코드를 저장하는 트리거를 만든다면?

```sql
CREATE TABLE test.seungsik_name_gender_audit(

	name varchar(16),
	gender enum('Male', 'Female'),
	modified timestamp
);
--트리거 정의
CREATE TRIGGER test.before_update_seungsik_name_gender
	BEFORE UPDATE ON test.seungsik_name_gender
//update가 실행되기 전에! test.seungsik_name_gender를 실행!
	FOR EACH ROW
INSERT INTO test.seungsik_name_gender_audit
SET name = OLD.name,
	gender = OLD.GENDER, // 기존에 있던 것들.
	modified = NOW();
```