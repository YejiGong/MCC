import java.util.*;
import java.lang.*;

// command-line execution example) java MatmultD 6 < mat500.txt
// 6 means the number of threads to use
// < mat500.txt means the file that contains two matrices is given as standard input
//
// In eclipse, set the argument value and file input by using the menu [Run]->[Run Configurations]->{[Arguments], [Common->Input File]}.

// Original JAVA source code: http://stackoverflow.com/questions/21547462/how-to-multiply-2-dimensional-arrays-matrix-multiplication


public class MatmultD
{
    private static Scanner sc = new Scanner(System.in);
    public static void main(String [] args) throws InterruptedException {
        int thread_no=0;
        if (args.length==1) thread_no = Integer.valueOf(args[0]);
        else thread_no = 1;

        thread_static[] thread = new thread_static[thread_no];

        int a[][]=readMatrix();
        int b[][]=readMatrix();

        thread_static.a = a;
        thread_static.b = b;

        //ex)thread=4: (0...124), (125...249), (250...374), (375...499)
        //divide a's rows per threads(static block), then do multiplication
        for (int t=0; t<thread_no; t++){
            if(t!=thread_no-1) {
                thread[t] = new thread_static(t * (a.length / thread_no), (t + 1) * (a.length / thread_no));
            }
            else{
                thread[t] = new thread_static(t*(a.length/thread_no), a.length);
            }
        }

        long startTime = System.currentTimeMillis();
        if(a.length == 0){
            thread_static.ans = new int[0][0];
        }
        else if(a[0].length != b.length){
            thread_static.ans = null; //invalid dims
        }
        else {
            thread_static.ans = new int[a.length][b[0].length];

            for (int t = 0; t < thread_no; t++) {
                thread[t].start();
            }
            for (int t = 0; t < thread_no; t++) {
                thread[t].join();
            }
        }
        //after all threads finish, check the time
        long endTime = System.currentTimeMillis();

        printMatrix(thread_static.ans);

        for(int t=0; t<thread_no; t++){
            System.out.printf("[thread_no]:%2d , [Time]:%4d ms\n", t, thread[t].timeDiff);
        }

        System.out.printf("Program Execution Time: %4d ms\n", endTime-startTime);
    }

    public static int[][] readMatrix() {
        int rows = sc.nextInt();
        int cols = sc.nextInt();
        int[][] result = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = sc.nextInt();
            }
        }
        return result;
    }

    public static void printMatrix(int[][] mat) {
        int rows = mat.length;
        int columns = mat[0].length;
        int sum = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                sum+=mat[i][j];
            }
        }
        //get result sum
        System.out.println("Matrix Sum = " + sum + "\n");
    }

    public static void multMatrix(int a[][], int b[][], int start, int finish){//a[m][n], b[n][p]

        int n = a[0].length;
        int p = b[0].length;

        for(int i = start;i < finish;i++){
            for(int j = 0;j < p;j++){
                for(int k = 0;k < n;k++){
                    thread_static.ans[i][j] += a[i][k] * b[k][j];
                }
            }
        }
    }

    static class thread_static extends Thread{
        int start;
        int finish;
        static int[][] a;
        static int[][] b;
        static int[][] ans;
        long startTime;
        long endTime;
        long timeDiff;

        public thread_static(int start, int finish){
            this.start = start;
            this.finish = finish;
        }

        public void run(){
            startTime = System.currentTimeMillis();
            multMatrix(a, b, start, finish);
            endTime = System.currentTimeMillis();
            timeDiff = endTime - startTime;
        }
    }
}