# 9월 26일 강의(Delivery)

## 라우팅을 하기 위한 기본 원칙

### 우리는 지금 네트워크 계층을 배우고 있다.

라우터 : 특정 망을 향한 최적의 경로를 정해주는것

## 1. Direct Delivery

내가 전달한 최종 목적지가 나와 직접 연결되어있는 망에있는가?

라우터는 여러개 망과 연결되어있는데, 

ARP 목적지 : IP 목적지 주소. 

### 1.1Host 기반 라우팅

| Prefix | 목적지 망 | Next -Hop |
| --- | --- | --- |
| /8 | 10.0.0.0 | R2 |
| /32 | 10.0.0.100 | R3 |

보통 10으로 시작하는건 전부다 R2로 가는데, 호스트 딱 하나만!! 다른 라우터로 가고싶을때는 이렇게한다.

![Untitled](9%E1%84%8B%E1%85%AF%E1%86%AF%2026%E1%84%8B%E1%85%B5%E1%86%AF%20%E1%84%80%E1%85%A1%E1%86%BC%E1%84%8B%E1%85%B4(Delivery)%208e79f4fa3ec34085aacb103bc5ea36ca/Untitled.png)

2.Indirect Delivery

어떤 망에 가려면 다른 라우터 여러개를 거쳐야할 경우.

ARP 목적지 : 다음 라우터 주소. 

![Untitled](9%E1%84%8B%E1%85%AF%E1%86%AF%2026%E1%84%8B%E1%85%B5%E1%86%AF%20%E1%84%80%E1%85%A1%E1%86%BC%E1%84%8B%E1%85%B4(Delivery)%208e79f4fa3ec34085aacb103bc5ea36ca/Untitled%201.png)

### Source Routing

사실 라우팅을 하기 위한 가장 쉬운 방법은, 모든 경로를 지정하는거야

### Next Hop routing

라우팅 테이블 양 줄이기 좋다.

## Network 라우팅

내가 꼭 가야하는 망들은 라우팅 테이블에 넣어놓고, 그게 아니다? Default routing을 설정해놓음.

![Untitled](9%E1%84%8B%E1%85%AF%E1%86%AF%2026%E1%84%8B%E1%85%B5%E1%86%AF%20%E1%84%80%E1%85%A1%E1%86%BC%E1%84%8B%E1%85%B4(Delivery)%208e79f4fa3ec34085aacb103bc5ea36ca/Untitled%202.png)

정해지지 않는건 다 R2로 가는데, N2로 가고자 하는게 R2로 갈 경우, Redirection을 통해 다시 R1으로 옮겨주면서 

R1에서 라우팅 테이블 형성.

Default routing의 mask = 0.0.0.0 ( 어떤 주소가 들어와도! 이걸로 Masking하면 0.0.0.0이 나온다.)

…./0 이 니까 

라우팅 테이블 구성법 : Prefix가 긴것부터 정렬한다. 가다가 딱 맞으면 들어가도록! 

→ Default라우팅 같은경우는 Prefix정렬했을 때 제일 아래. 정 하다 없으면 Default로!

### 문제

![Untitled](9%E1%84%8B%E1%85%AF%E1%86%AF%2026%E1%84%8B%E1%85%B5%E1%86%AF%20%E1%84%80%E1%85%A1%E1%86%BC%E1%84%8B%E1%85%B4(Delivery)%208e79f4fa3ec34085aacb103bc5ea36ca/Untitled%203.png)

 

 R1의 포워딩 테이블을 만드세요!

라우팅 테이블은 모든 경로가 들어있는데, 그 중 제일 적은 경로→Forwarding Table

(목적지까지 가는 최고 좋은 경로로 테이블을 만들어라!)

| Prefix | 목적지 망 주소 | I/R | Next-Hop |
| --- | --- | --- | --- |
| /26이 제일 위어야한다
255.255.255.192(11000000) | 180.70.65.192 | m2 | 다이렉트기 때문에 
- |
| /25인 저 위에거의 프리픽스는
255.255.255.128(이진수 봐라) | 180.70.65.128 | m0 | - |
| /24
255.255.255.0 |  |  |  |
| /22
255.255.255.0 |  |  |  |
| 0.0.0.0 |  |  |  |

프리픽스를 봐서 /26이면 앞에서부터 26비트 제외하고 했을때 목적지와 같은지.. 이런거 

![Untitled](9%E1%84%8B%E1%85%AF%E1%86%AF%2026%E1%84%8B%E1%85%B5%E1%86%AF%20%E1%84%80%E1%85%A1%E1%86%BC%E1%84%8B%E1%85%B4(Delivery)%208e79f4fa3ec34085aacb103bc5ea36ca/Untitled%204.png)

## 풀어보세요

![Untitled](9%E1%84%8B%E1%85%AF%E1%86%AF%2026%E1%84%8B%E1%85%B5%E1%86%AF%20%E1%84%80%E1%85%A1%E1%86%BC%E1%84%8B%E1%85%B4(Delivery)%208e79f4fa3ec34085aacb103bc5ea36ca/Untitled%205.png)

다이렉트 연결을 먼저 그리는게 포인트. (Next-Hop이—-인거)

[Untitled-2022-09-26-1045.excalidraw](9%E1%84%8B%E1%85%AF%E1%86%AF%2026%E1%84%8B%E1%85%B5%E1%86%AF%20%E1%84%80%E1%85%A1%E1%86%BC%E1%84%8B%E1%85%B4(Delivery)%208e79f4fa3ec34085aacb103bc5ea36ca/Untitled-2022-09-26-1045.excalidraw)

## Address aggregation

라우팅 테이블의 개수를 줄이고싶어.

![Untitled](9%E1%84%8B%E1%85%AF%E1%86%AF%2026%E1%84%8B%E1%85%B5%E1%86%AF%20%E1%84%80%E1%85%A1%E1%86%BC%E1%84%8B%E1%85%B4(Delivery)%208e79f4fa3ec34085aacb103bc5ea36ca/Untitled%206.png)

Prefix를 줄이면서 24로 통일한다. 이렇게 해서 라우팅테이블 사이즈를 줄이기