# View

- 자주 사용하는 SQL쿼리 (SELECT)가 있다면?
    - 그 SELECT문에 이름을 주고 사용을 쉽게 함.(TABLE이름처럼)
    - 내가 등록한 SELECT문의 결과가 TABLE인것처럼.
    
- View 예시
    - 아래 SELECT를 기본으로 자주 사용한다고 하자.
    
    ```sql
    SELECT s.id, s.user_id, s.created, s.channel_id,c.channel
    FROM session s 
    JOIN channel c ON c.id = s.channel_id;
    ```
    
    이놈을 VIEW 로 바꿔보자.
    
    ```sql
    CREATE OR REPLACE VIEW test.session_details AS
    SELECT s.id, s.user_id, s.created, s.channel_id, c.channel
    FROM session s
    JOIN channel c ON c.id = s.channel_id;
    
    SELECT *FROM test.session_details;
    ```