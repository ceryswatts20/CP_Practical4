/*===========================================================


===========================================================*/
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.*;;

class Example1 {
    static final int MAX_NUMBER = 10;
    static final int BUFFER_SIZE = 4;

    static int [] buffer = new int [BUFFER_SIZE];
    static int head =0, tail = 0;
    static Semaphore elements = new Semaphore(0);
    static Semaphore spaces = new Semaphore(BUFFER_SIZE);
    static Lock pLock = new ReentrantLock();
    static Lock cLock = new ReentrantLock();

    public static void main (String[] args) {

        System.out.println("Start of program.");
        Producer producer = new Producer(1);
        Producer producer2 = new Producer(2);
        Consumer consumer = new Consumer(1);
        Consumer consumer2 = new Consumer(2);

        producer.start();
        producer2.start();
        consumer.start();
        consumer2.start();
    }
}
/*===========================================================
    Producer process, puts numbers in the buffer
===========================================================*/
class Producer extends Thread {
    private int number = 0;
    private int id;

    Producer(int id_) {
        id = id_;
    }

    public void run() {
        do {
            Example1.spaces.acquireUninterruptibly();
            Example1.pLock.lock();
            int i=produce();
            Example1.buffer[Example1.tail]=i;
            Example1.tail=(Example1.tail+1)% Example1.BUFFER_SIZE;
            Example1.pLock.unlock();
            System.out.println("P" + id + ": " + i + " added to buffer");
            Example1.elements.release();
        } while (number  != Example1.MAX_NUMBER);
        System.out.println("P" + id + ": finished");
    }

    public int produce() {
        number++;
        return number;
    }

}

/*===========================================================
    Consumer process, reads numbers in the buffer
===========================================================*/
class Consumer extends Thread {

    private int number=0;
    private int id;

    Consumer(int id_) {
        id = id_;
    }

    public void run() {
        do {
            Example1.elements.acquireUninterruptibly();
            Example1.cLock.lock();
            int i=Example1.buffer[Example1.head];
            Example1.head=(Example1.head+1)% Example1.BUFFER_SIZE;
            consume(i);
            Example1.cLock.unlock();
            Example1.spaces.release();
        } while (number != Example1.MAX_NUMBER);
         System.out.println("C" + id + ": finished");
    }

    public void consume(int i) {
        System.out.println("C" + id + ": " + i + " read from buffer");
        number++;
    }
}
