package DFSProblem1;


class Solution {
    int answer = 0;
    public int solution(int[] numbers , int target){
        dfs(numbers, 0,0,target);

        return answer;
    }
    public void dfs(int[] numbers, int depth, int sum, int target) {
        if (depth == numbers.length) {
            if (sum == target) {
                answer += 1;
            }
        }else {
                dfs(numbers, depth + 1, sum + numbers[depth], target);
                dfs(numbers, depth + 1, sum - numbers[depth], target);
            }

        }

    }

public class Main {
    public static void main(String[] args) {
        Solution slt = new Solution();
        slt.solution(new int[]{4,1,2,1}, 4);

    }
}

