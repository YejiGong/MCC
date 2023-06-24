import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
public class ex1 {
    public static void main(String[] args){
        Seat seat = new Seat(7);
        for(int i=1; i<=10; i++){
            Student std = new Student("std"+i, seat);
        }
    }
}

class Seat { //library seat
    private BlockingQueue seats;
    public Seat(int num){
        this.seats = new ArrayBlockingQueue<String>(num);
    }
    public void seating(String name) { //student trying to seat
        try {
            seats.put(name);
        }catch(InterruptedException e){}
    }
    public void leave(){ //student finish study, and leave library
        try {
            seats.take();
        }catch(InterruptedException e){}
    }
}

class Student extends Thread{
    private Seat seat;
    public Student(String name, Seat seat){
        super(name);
        this.seat = seat;
        start();
    }

    private void tryingToSeat(){
        System.out.println(this.getName() + " is going to take a seat in the library");
    }
    private void studying(){
        System.out.println(this.getName() + " seats down and studies");
    }
    private void endStudying(){
        System.out.println("     "+ this.getName() + " is about to finish studying");
    }
    private void left(){
        System.out.println("     "+ this.getName() + " left library");
    }
    public void run(){
        while(true){
            try {
                sleep((int) (Math.random() * 10000));
            }catch(InterruptedException e){}
            tryingToSeat();
            seat.seating(this.getName());
            studying();
            try{
                sleep((int) (Math.random() * 15000));
            }catch(InterruptedException e){}
            endStudying();
            seat.leave();
            left();
        }
    }
}
