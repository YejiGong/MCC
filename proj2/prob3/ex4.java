import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
public class ex4 {
    public static void main(String[] args){
        Runnable action = new Runnable() {
            @Override
            public void run() {
                System.out.println("                 all traveler gathered at the meeting point");
                //all traveler gathered, so cyclicbarrier broken
            }
        };
        CyclicBarrier cyclicBarrier = new CyclicBarrier(4, action);
        for(int i=1; i<5; i++){
            Traveler traveler = new Traveler("traveler"+i, cyclicBarrier);
        }
    }
}

class Traveler extends Thread{
    private CyclicBarrier cyclicBarrier;
    public Traveler(String name, CyclicBarrier cyclicBarrier){
        super(name);
        this.cyclicBarrier = cyclicBarrier;
        start();
    }
    private void traveling(){
        System.out.println(this.getName() + " is traveling");
    }
    private void reached(int num){
        System.out.println("     "+ this.getName() + " reached at " + num+"th meeting place");
    }
    public void run(){
        for(int i=1; i<4; i++) { //traveler travels freely, and meet four times at meeting point.
            traveling();
            try {
                sleep((int) (Math.random() * 15000)); //travel freely
            } catch (InterruptedException e) {}
            reached(i); //reach at meeting place
            try {
                cyclicBarrier.await(); //wait until other travelers reach at meeting place
            } catch (InterruptedException e) {}
            catch (BrokenBarrierException e) {}
        }


    }

}
