public class pc_static_block{
    private static int NUM_END = 200000; //default input
    private static int NUM_THREADS = 4; //default number of threads
    public static void main (String[] args) throws InterruptedException {
        if (args.length == 2) {
            NUM_THREADS = Integer.parseInt(args[0]);
            NUM_END = Integer.parseInt(args[1]);
        }
        thread_static[] thread = new thread_static[NUM_THREADS];

        //ex)thread = 4-> (1, 49999), (50000,99999), (100000, 149999), (150000, 199999)
        for (int t=0; t<NUM_THREADS; t++){
            if(t!=NUM_THREADS-1) {
                thread[t] = new thread_static((NUM_END / NUM_THREADS) * t, (NUM_END / NUM_THREADS) * (t + 1));
            }else{
                thread[t] = new thread_static((NUM_END/NUM_THREADS)*t, NUM_END);
            }
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
        int start;
        int finish;
        int temp = 0;
        long startTime;
        long endTime;
        long timeDiff;

        public thread_static(int start, int finish){
            this.start = start;
            this.finish = finish;
        }

        public void run(){
            startTime = System.currentTimeMillis();
            for(int i=start; i<finish; i+=1){
                if(isPrime(i)){
                    temp++;
                }
            }

            endTime = System.currentTimeMillis();
            timeDiff = endTime - startTime;
        }
    }
}