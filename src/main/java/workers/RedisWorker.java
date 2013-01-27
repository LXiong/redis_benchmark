package workers;

import redis.clients.jedis.JedisPool;


public abstract class RedisWorker{

    protected final JedisPool pool;
    protected final int opCount;
	
	public RedisWorker(JedisPool pool, int opCount) {
		this.opCount = opCount;
		this.pool = pool;
	}
	
	abstract void doJob();

}
