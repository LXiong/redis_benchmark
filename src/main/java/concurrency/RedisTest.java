package concurrency;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.Test;
import redis.clients.jedis.JedisPool;
import workers.LatchWorker;
import workers.RedisWorker;
import workers.RedisWorkerImpl;
import workers.RedisWorkerImplWithMSet;
import workers.RedisWorkerImplWithPipelining;

public class RedisTest {

	final JedisPool jedisPool = new JedisPool("localhost", 6379);
    RedisWorker redisWorker;

    private void test(final int threadCount, RedisWorker redisWorker) throws InterruptedException {
        
        final CountDownLatch startGate = new CountDownLatch(1);
        final CountDownLatch endGate = new CountDownLatch(threadCount);
        final ExecutorService executor = Executors.newFixedThreadPool(threadCount + 1);
        

        for (int i = 0; i < threadCount; ++i) {
//            executor.execute(new RedisWorkerImpl(jedisPool, opCount, startGate, endGate, i));
        	executor.execute(new LatchWorker(redisWorker, startGate, endGate, i));
        }

        long start = System.nanoTime();
        startGate.countDown();
        endGate.await();
        long end = System.nanoTime();
        System.out.println("Redis: Thread count: " + threadCount + " Time: " + (end - start) / 1000 + "ms");
    }
    
    @Test
    public void testRedisWorkerWithSET() throws InterruptedException {
        System.out.println("\nRedis with SET test started");
        
        for (int i = 0; i < 8; i++) {
        	int threadCount = (int) Math.pow(2, i); 
			test(threadCount, new RedisWorkerImpl(jedisPool, 131072 / threadCount));
			System.out.println("----------------------");
		}
        
    }
    
    @Test
    public void testRedisWorkerWithMSET() throws InterruptedException {
        System.out.println("\nRedis with MSET test started");
        
        for (int i = 0; i < 8; i++) {
        	int threadCount = (int) Math.pow(2, i); 
			test(threadCount, new RedisWorkerImplWithMSet(jedisPool, 131072 / threadCount));
			System.out.println("----------------------");
		}
        
    }
    
    
    @Test
    public void testRunRedisWorkerWithPipelining() throws InterruptedException {
        System.out.println("\nRedis with Pipelining test started");
        
        for (int i = 0; i < 8; i++) {
        	int threadCount = (int) Math.pow(2, i); 
			test(threadCount, new RedisWorkerImplWithPipelining(jedisPool, 131072 / threadCount));
			System.out.println("----------------------");
		}
        
    }
}