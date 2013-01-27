package workers;

import java.util.concurrent.CountDownLatch;

public class LatchWorker implements Runnable {

	private RedisWorker redisWorker;
    private final int id;
    private final CountDownLatch startGate;
    private final CountDownLatch endGate;

    public LatchWorker(RedisWorker redisWorker, CountDownLatch startGate, 
    					CountDownLatch endGate, int id) {
    	this.redisWorker = redisWorker;
        this.id = id;
        this.startGate = startGate;
        this.endGate = endGate;
    }

    @Override
    public void run() {
//        System.out.println("Thread[" + this.id + "]: waiting for latch");
        try {
            startGate.await();
//            System.out.println("Thread[" + this.id + "]: started");
            redisWorker.doJob();
//            System.out.println("Thread[" + this.id + "] completed");
            endGate.countDown();
        } catch (InterruptedException ex) {
        }
    }

    
}