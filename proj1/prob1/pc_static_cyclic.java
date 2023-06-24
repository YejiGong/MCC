import java.util.ArrayList;

public class pc_static_cyclic{
    private static int NUM_END = 200000; //default input
    private static int NUM_THREADS = 6; //default number of threads
    public static void main (String[] args) throws InterruptedException {
        if (args.length == 2){
            NUM_THREADS = Integer.parseInt(args[0]);
            NUM_END = Integer.parseInt(args[1]);
        }
        thread_static[] thread = new thread_static[NUM_THREADS];

        //ex)thread=4 -> (1,41,81,...),(11,51,91...),(21,61,101....),(31,71,111...)
        ArrayList<Integer>[] thread_blocks = new ArrayList[NUM_THREADS];
        for(int t=0; t<NUM_THREADS; t++){
            thread_blocks[t] = new ArrayList<Integer>();
        }
        for (int t=1; t<NUM_END; t+=10*NUM_THREADS){
            for (int k=0; k<NUM_THREADS; k++){
                if (t+10*k<NUM_END) {
                    thread_blocks[k].add(t + 10 * k);
                }
            }
        }

        for(int t=0; t<NUM_THREADS; t++){
            thread[t] = new thread_static(thread_blocks[t]);
        }
        long startTime = System.currentTimeMillis();
        for(int t=0; t<NUM_THREADS; t++){
            thread[t].start();
        }
        for(int t=0; t<NUM_THREADS; t++){
            thread[t].join();
        }

        //after all threads finish, check the time
        long endTime = System.currentTimeMillis();
        long timeDiff = endTime - startTime;

        for(int t=0; t<NUM_THREADS; t++){
            System.out.println("Thread-" + t + "'s Execution Time : " + thread[t].timeDiff + "ms");
        }
        System.out.println("Program Execution Time: "+ timeDiff + "ms");


        int result = 0;
        for(int t=0; t<NUM_THREADS; t++){
            //get sum of each threads's result
            result += thread[t].temp;
        }
        System.out.println("1..." + (NUM_END-1) + " prime# counter=" + result);
    }
    private static boolean isPrime(int x){
        int i;
        if (x<=1) return false;
        for (i=2; i<x; i++){
            if (x%i==0) return false;
        }
        return true;
    }

    static class thread_static extends Thread{
        ArrayList<Integer> x;
        int temp = 0;
        long startTime;
        long endTime;
        long timeDiff;

        public thread_static(ArrayList<Integer> x){
            this.x = x;
        }

        public void run(){
            startTime = System.currentTimeMillis();

            int i;
            for(i=0; i<x.size(); i+=1){
                for(int j=x.get(i); j<x.get(i)+10; j+=1){
                    if(isPrime(j)){
                        temp++;
                    }
                }
            }
            endTime = System.currentTimeMillis();
            timeDiff = endTime - startTime;
        }
    }
}