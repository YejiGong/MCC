import java.util.concurrent.atomic.AtomicInteger;

public class pc_dynamic{
    private static int NUM_END = 200000; //default input
    private static int NUM_THREADS = 4; //default number of threads
    public static void main (String[] args) throws InterruptedException {
        if (args.length == 2){
            NUM_THREADS = Integer.parseInt(args[0]);
            NUM_END = Integer.parseInt(args[1]);
        }
        AtomicInteger atomic_int = new AtomicInteger(1);

        thread_dynamic[] thread = new thread_dynamic[NUM_THREADS];

        for(int t=0; t<NUM_THREADS; t++){
            thread[t] = new thread_dynamic(atomic_int);
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

    static class thread_dynamic extends Thread{
        private AtomicInteger AtomicCounter;
        int x;
        int temp = 0;
        long startTime;
        long endTime;
        long timeDiff;

        public thread_dynamic(AtomicInteger x){
            this.AtomicCounter = x;
            //get initial value. work unit : 10number=> get and add 10.
            this.x = x.getAndAdd(10);
        }

        public void run(){
            startTime = System.currentTimeMillis();

            int i;
            while(x<NUM_END){
                for(i=x; i<x+10; i++) {
                    if (isPrime(i)) {
                        temp++;
                    }
                }
                //get next value
                x = AtomicCounter.getAndAdd(10);
            }

            endTime = System.currentTimeMillis();
            timeDiff = endTime - startTime;
        }
    }
}