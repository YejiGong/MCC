import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
public class ex2 {
    public static void main(String[] args){
        ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        Lock readLock = readWriteLock.readLock();
        Lock writeLock = readWriteLock.writeLock();
        Book book = new Book(readLock, writeLock);
        for(int i=1; i<4; i++) {
            Writer writer = new Writer("writer"+ i,book);
        }
        for(int i=1; i<5; i++){
            Reader reader = new Reader("reader"+i, book);
        }
    }
}

class Book{
    private Lock readLock;
    private Lock writeLock;
    private int bookPage;
    private String book;
    public Book(Lock readLock, Lock writeLock){
        this.readLock = readLock;
        this.writeLock = writeLock;
        this.bookPage = 0;
        this.book= "";
    }
    public void update(char str){ //writer updates the contents of the book
        try{
            writeLock.lock();
            bookPage+=1;
            book += str;
        }finally {
            writeLock.unlock();
        }
    }
    public void startReading(){
        readLock.lock();
    }
    public String reading(){ //reader reads the book
        try{
            return book;
        }finally {
            readLock.unlock();
        }
    }
}

class Writer extends Thread{
    private Book book;
    public Writer(String name, Book book){
        super(name);
        this.book = book;
        start();
    }
    private void startWriting(){
        System.out.println(this.getName() + " is trying to writing");
    }
    private void wrote(){
        System.out.println("   " + this.getName() + " wrote something");
    }
    public void run(){
        while(true){
            try{
                sleep((int) (Math.random() * 10000)); //think about content of book
            }catch(InterruptedException e){}
            startWriting();
            book.update((char)((int)(Math.random()*26)+65));
            wrote();
        }

    }
}

class Reader extends Thread{
    private Book book;
    public Reader(String name, Book book){
        super(name);
        this.book = book;
        start();
    }
    private void startReading(){
        System.out.println("            "+this.getName() + " starts reading book");
    }
    private void read(String element){
        System.out.println("            "+this.getName() + " read the book and content of book is" + element);
    }
    private void finishReading(){
        System.out.println("            "+this.getName() + " finish reading");

    }
    public void run(){
        while(true){
            try{
                sleep((int) (Math.random() * 10000));//waiting while writer writes
            }catch(InterruptedException e){}
            startReading();
            book.startReading();
            try{
                sleep((int) (Math.random() * 15000));//reading
            }catch(InterruptedException e){}
            String element = book.reading();
            read(element);
            finishReading();
        }
    }
}