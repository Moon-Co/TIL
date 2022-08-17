# Spring Security Essential

# Thread Per Request

병렬처리 기법중 하나이다.

클라이언트의 요청을 큐에 넣고→ 큐에서 나온걸 스레드풀로 옮겨서→ 병렬 스레드 가처리

동시에 여러 요청을 처리하기 위한 방법!

( 최근 소개된 WebFlux는 Thread 갯수를 작은 갯수로 유지하면서 Http요청을 동시 처리할 수 있도록 한다→ Thread Per Request와는 반대!

### Thread Local

- Thread 범위 변수 → 동일 쓰레드 내에서는 언제든 Thread Local 변수에 접근할 수 있음.
- 쓰레드로컬 변수를 사용하고 사용이 끝난다면 꼭 Clear해줘야한다!!!

## Security context holder

Security Context를 담고있는 그릇이라고 생각하자.