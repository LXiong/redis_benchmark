package workers;

import java.util.ArrayList;
import java.util.List;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

public final class RedisWorkerImplWithPipelining extends RedisWorker {

    public RedisWorkerImplWithPipelining(JedisPool pool, int opCount) {
		super(pool, opCount);
	}

	@Override
    void doJob() {
		Jedis jedis = pool.getResource();
        int interval = 10000;
        int lower = 0, upper = interval;
        String value = "1234567890123456";

        while (lower < opCount) {
        	List<Response<String>> list = new ArrayList<>();
        	
        	Pipeline p = jedis.pipelined();
        	for (int i = 0; i < interval; i++) {
        		p.set(i + "", value);
        		list.add(p.get(i + ""));
        	}
        	p.sync();

        	lower = upper;
        	upper = (upper + interval) > opCount ? opCount : upper + interval;
        	interval = upper - lower;
		}
        
        pool.returnResource(jedis);
    }

  
}