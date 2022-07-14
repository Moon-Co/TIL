# INSERT, DELETE,UPDATE

## INSERT

테이블을 2개 생성해보자.

```java
CREATE TABLE prod.vital(
	user_id int not null,
	vital_id int primary key,
	date timestamp not null,
	weight int not null
);

CREATE TALE prod.alert(
	alert_id int primary key,
	vital_id int,
	alert_type varchar(32),
	date timestamp,
	user_id int
);
```

테이블의 요소를 Insert로 추가해보자.

```java
INSERT INTO prod.vital(user_id, vital_id, date, weight) VALUES(100,1,'2020-01-01',75);
INSERT INTO prod.vital(user_id, vital_id, date, weight) VALUES(100,3,'2020-01-02',78);
INSERT INTO prod.vital(user_id, vital_id, date, weight) VALUES(101,2,'2020-01-01',90);
INSERT INTO prod.vital(user_id, vital_id, date, weight) VALUES(101,4,'2020-01-02',95);
INSERT INTO prod.vital(user_id, vital_id, date, weight) VALUES(999,5,'2020-01-02',-1);
INSERT INTO prod.vital(user_id, vital_id, date, weight) VALUES(999,5,'2020-01-02',10);
//마지막줄을 primary key부분에 중복된것을 넣었기 때문에 오류가 날것
INSERT INTO prod.alert VALUES(1,4,'WeightIncrese',2020-01-02,101);
INSERT INTO prod.alert VALUES(2,NULL,'MISSING VITAL',2020-01-04,100);
INSERT INTO prod.alert VALUES(3,NULL,'MISSING VITAL',2020-01-04,101);

```

## DELETE

테이블에서 레코드 삭제, 혹은 모든 레코드 삭제

(테이블을 삭제하는건 아님)

### DELETE  FROM vs TRUNCATE

TRUNCATE → 조건없이 모든 레코드를 삭제. 빠르지만 롤백이 안된다.

```java
DELETE FROM prod.vital
// 레코드가 다날아간다. 테이블은 있지만
```

## UPDATE

조건을 기반으로, 테이블의 특정 레코드 변경

- 예 : vital_id가 4인 레코드의 weight를 92로 변경하고싶다면

```java
UPDATE prod.vital
SET weight = 92
WHERE vital_id = 4;

```