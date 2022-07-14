# JOIN

## JOIN 이란?

- 두개 이상의 테이블들을 공통 필드를 가지고 통합.
- 양쪽 필드를 모두 가진 새로운 테이블이 생긴다.

## Join 문법

```java
SELECT A.*, B.*
FROM RAW_data.table1A

INNER JOIN raw_data table2 B on A.key1 = B.key1 and A.key2=B.key2
WHERE A.ts>= '2019-01-01';
//INNER : 2개의 테이블이 공통으로 매치 되었을때
//LEFT,: 왼쪽에 있는 테이블은 다 리턴, 겹치는게 있으면 오른쪽것도 따라감.
//RIGHT , CROSS
```

## JOIN 시 고려해야할 점

- 중복 레코드가 없고, Primary Key가 잘 적용되었는가?
- 조인하는 테이블 사이의 관계를 명확하게 정의.

## JOIN 연습

![Untitled](JOIN%201dc6c469567b468e9b6fcbfdf7a32d04/Untitled.png)

위와 같은 테이블이 있다고 하자. 

### INNER JOIN

![Untitled](JOIN%201dc6c469567b468e9b6fcbfdf7a32d04/Untitled%201.png)

`SELECT *FROM prod.vital v`

`JOIN prod.alert a ON v.vital_id = a.vita_id`

vital id가 같다면 join해서 새로운 테이블을 만드는 건데

vital_id가 4로 같은게 하나 있으므로 , 합쳐서 테이블을 하나 만들어 준다.

### LEFT JOIN

![Untitled](JOIN%201dc6c469567b468e9b6fcbfdf7a32d04/Untitled%202.png)

`SELECT *FROM prod.vital v`

`LEFT JOIN prod.alert a ON v.vital_id = a.vital_id;`

LEFT JOIN은 왼쪽 테이블은 그대로 두고, 

vital id가 같다면 오른쪽 테이블에 같은 요소를 추가한다. (매칭이 되는 경우에만 채워짐)

 

### FULL JOIN

MySQL에서는 불가능하다. 

![Untitled](JOIN%201dc6c469567b468e9b6fcbfdf7a32d04/Untitled%203.png)

위와같이 양쪽 테이블을 다 리턴한다.

MySQL에서 사용하기 위해서는, LEFT JOIN, RIGHTJOIN 을 UNION해주면 된다.

```java
SELECT * FROM prod.vital v
LEFT JOIN prod.alert a ON v.vital_id = a.vital_id
UNION //UNION ALL 도 가능 
//UNION ->겹치는거 있을 때 중복 없애기.
//UNION ALL -> 중복 안건드림.
SELECT * FROM prod.vital v
RIGHT JOIN prod.alert a ON v.vital_id = a.vital_id;
```

## CROSS JOIN

왼쪽테이블과 오른쪽 테이블의 모든 레코드들의 조합을 리턴.