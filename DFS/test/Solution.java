package test;

public class Solution {
    int answer =0;
    public static void main(String[] args) {
        Solution solution = new Solution();
        int[][] arrays = {{1,0,0,1}, {0, 1, 1,0}, {0,1,1,0},{1,1,0,1}};
        System.out.println(solution.solution(4,arrays));
    }
    public int solution(int n, int[][] computers) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if(dfs(i,j,n,computers)){
                    answer++;
                }
            }
        }
        return answer;
    }
    public boolean dfs(int x, int y, int n , int[][] computers){
        if (x>=n || x<0 || y>=n || y<0){
            return false;
        }
        if(computers[x][y] == 1){
            computers[x][y] =0;
            computers[y][x]=0;
            dfs(x+1,y,n,computers);
            dfs(x,y+1,n,computers);
            dfs(x-1,y,n,computers);
            dfs(x,y-1,n,computers);
            return true;
        }
        return false;
    }
}
