import java.util.concurrent.atomic.AtomicInteger;
public class ex3 {
    public static void main(String[] args){
        Restaurant restaurant = new Restaurant(0, 1);
        for(int i=1; i<10; i++){
            Customer customer = new Customer("customer"+i, restaurant);
        }
    }
}

class Restaurant {
    private AtomicInteger sales;
    private AtomicInteger wholeCustomerNum;
    private AtomicInteger status; //1:true, 0:false
    public Restaurant(int sales, int wholeCustomerNum){
        this.sales = new AtomicInteger(sales);
        this.wholeCustomerNum = new AtomicInteger(wholeCustomerNum);
        this.status = new AtomicInteger(1);
    }
    public int status(){ //get restaurant status, if status : 0 -> restaurant close. 1 -> restaurant open.
        return status.get();
    }
    public int enter(){ //customer enter in restaurant, no limit of enter
        try {
            return wholeCustomerNum.getAndAdd(1);
        }finally{
            if(wholeCustomerNum.get()>10 && status.get()==1){
                finish();
            }
        }
    }
    private void finish(){
        status.set(0);
        System.out.println("               restaurant close, no more customer.");
    }
    public void calculate(int price){
        if(sales.addAndGet(price)>1000 && status.get()==1) {
                finish();
        }
    }
}

class Customer extends Thread{
    Restaurant restaurant;
    public Customer(String name, Restaurant restaurant){
        super(name);
        this.restaurant = restaurant;
        start();
    }
    private void tryingToEnter(){
        System.out.println(this.getName() + " trying to enter restaurant");
    }
    private void failToEnter(){
        System.out.println("     "+ this.getName() + " fail to enter because restaurant closed");
    }
    private void entered(int num){
        System.out.println(this.getName() + " is entered and " + this.getName() + " is " + num + "th customer");
    }
    private void eating(){
        System.out.println(this.getName() + " is eating");
    }
    private void finishEating(){
        System.out.println(this.getName() + " finish eating");
    }
    private void left(){
        System.out.println(this.getName() + " left the restaurant");
    }
    public void run(){
        while(restaurant.status()==1){
            try{
                sleep((int) (Math.random() * 10000)); //find restaurant
            }catch(InterruptedException e){}
            tryingToEnter();
            if(restaurant.status() == 0){ //if restaurant status is 0 : it's closed, can't enter
                failToEnter();
                break;
            }
            int num = restaurant.enter(); //enter
            entered(num);
            eating();
            try{
                sleep((int) (Math.random() * 15000)); //eating
            }catch(InterruptedException e){}
            finishEating();
            restaurant.calculate((int)(Math.random()*1000)); //finish eating, and calculating
            left(); //left the restaurant
        }

    }
}
