package workers;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public final class RedisWorkerImpl extends RedisWorker{

    public RedisWorkerImpl(JedisPool pool, int opCount) {
		super(pool, opCount);
	}

    @Override
    void doJob() {
    	Jedis jedis = pool.getResource();
        for (int i = 0; i < opCount; ++i) {
            jedis.set("foo", "1234567890123456");
            String value = jedis.get("foo");
        }
        pool.returnResource(jedis);
    }
}