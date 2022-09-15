# 카카오 기출: k진수에서 소수 개수 구하기

- k진수에서 소수 개수 구하기

**darklight**

**sublimevimemacs**

**Java**

### **문제 설명**

### 문제 설명

양의 정수 `n`이 주어집니다. 이 숫자를 `k`진수로 바꿨을 때, 변환된 수 안에 아래 조건에 맞는 소수(Prime number)가 몇 개인지 알아보려 합니다.

- `0P0`처럼 소수 양쪽에 0이 있는 경우
- `P0`처럼 소수 오른쪽에만 0이 있고 왼쪽에는 아무것도 없는 경우
- `0P`처럼 소수 왼쪽에만 0이 있고 오른쪽에는 아무것도 없는 경우
- `P`처럼 소수 양쪽에 아무것도 없는 경우
- 단, `P`는 각 자릿수에 0을 포함하지 않는 소수입니다.
    - 예를 들어, 101은 `P`가 될 수 없습니다.

예를 들어, 437674을 3진수로 바꾸면 `211`0`2`01010`11`입니다. 여기서 찾을 수 있는 조건에 맞는 소수는 왼쪽부터 순서대로 211, 2, 11이 있으며, 총 3개입니다. (211, 2, 11을 `k`진법으로 보았을 때가 아닌, 10진법으로 보았을 때 소수여야 한다는 점에 주의합니다.) 211은 `P0` 형태에서 찾을 수 있으며, 2는 `0P0`에서, 11은 `0P`에서 찾을 수 있습니다.

정수 `n`과 `k`가 매개변수로 주어집니다. `n`을 `k`진수로 바꿨을 때, 변환된 수 안에서 찾을 수 있는 **위 조건에 맞는 소수**의 개수를 return 하도록 solution 함수를 완성해 주세요.

---

### 제한사항

- 1 ≤ `n` ≤ 1,000,000
- 3 ≤ `k` ≤ 10

---

### 입출력 예

---

### 입출력 예 설명

**입출력 예 #1**

문제 예시와 같습니다.

**입출력 예 #2**

110011을 10진수로 바꾸면 110011입니다. 여기서 찾을 수 있는 조건에 맞는 소수는 11, 11 2개입니다. 이와 같이, 중복되는 소수를 발견하더라도 모두 따로 세어야 합니다.

| --- | --- | --- |

---

# 풀이

## 전체코드

```java

import java.util.*;
public class Main {
    public static void main(String[] args) {
        Solution slt = new Solution();
        System.out.println(slt.solution(110011,10));
    }

}
class Solution {
    static intcount;
    static ArrayList<Integer>targetNumber= new ArrayList<>();
    public int solution(int n, int k) {
        ArrayList<Integer> numberList = new ArrayList<>();
        numberList = numberSystem(n,k);
        for (int i = 0; i < numberList.size(); i++) {

            if(numberList.get(i)==0){
                isPrime(targetNumber);
targetNumber.clear();
                continue;
            }
targetNumber.add(numberList.get(i));
        }
        isPrime(targetNumber);

        returncount;
    }
    public ArrayList<Integer> numberSystem(int decimal,int k){
        //a를 k진수로 바꿔주는 프로그램
        ArrayList<Integer> numbers = new ArrayList<>();
        int remain = 0;
        //10을 2진수로 바꾸려면?
        //10%2 = 0, 10/2 = 5
        //5%2 = 1, 5/2 = 2
        //2%2 = 0, 2/2 = 1
        //1010
        //110011
        //
        while(true){
            remain = decimal % k;
            numbers.add(0,remain);
            decimal = decimal/k;
            if(decimal<k){
                numbers.add(0,decimal%k);
                break;
            }
        }
        return numbers;
    }
    public void isPrime(ArrayList<Integer> target){
        long num =0;
        for(long a : target){
            num*=10;
            num+=a;
        }
        if(num<=1){
            return;
        }

        for (int i = 2; i <=Math.sqrt(num); i++) {
            if(num%i==0){
                return;
            }
        }
count++;

        }

}

```

아이디어는 풀만했으나 갑자기 소수구하기 테크닉에서 시간초과가 나서 고됐다.

저렇게 for문을 루트넘보다 작게 하는 테크닉을 기억해놓자.

```java
 for (int i = 2; i <=Math.sqrt(num); i++) {
            if(num%i==0){
                return;
            }
        }
```

뿐만 아니라 정수배열을 정수로 만드는 테크닉도 알아야아한다

```java
long num =0;
        for(long a : target){
            num*=10;
            num+=a;
        }
```

211이라고하면

0*10+2 = 2

2*10 +1= 21

21*10 + 1 = 211

이런식으로 하는거다!

또 k진수로 바꿔주는 테크닉도 알자

```java
while(true){
            remain = decimal % k;
            numbers.add(0,remain);
            decimal = decimal/k;
            if(decimal<k){
                numbers.add(0,decimal%k);
                break;
            }
        }
        return numbers;
```

수학으로 생각하자. 쉽다.