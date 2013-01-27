package workers;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public final class RedisWorkerImplWithMSet extends RedisWorker {

	private final int interval = 1000;

    public RedisWorkerImplWithMSet(JedisPool pool, int opCount) {
		super(pool, opCount);
	}

    @Override
    void doJob() {
		Jedis jedis = pool.getResource();
        int interval = this.interval;
        int lower = 0, upper = interval;
        String value = "1234567890123456";
        
        while (lower < opCount) {
        	int keyCountForThisLoopRound = upper - lower;
        	String[] keyArray 	   = new String[keyCountForThisLoopRound];
        	String[] keyValueArray = new String[keyCountForThisLoopRound * 2];
        	
        	for (int i = 0; i < interval; i++) {
        		keyArray[i] = i + "";
        		keyValueArray[i * 2] = keyArray[i];
        		keyValueArray[i * 2 + 1] = value;
        	}
        	
        	jedis.mset(keyValueArray);
        	jedis.mget(keyArray);

        	lower = upper;
        	upper = (upper + interval) > opCount ? opCount : upper + interval;
        	interval = upper - lower;
		}
        
        pool.returnResource(jedis);
    }

}