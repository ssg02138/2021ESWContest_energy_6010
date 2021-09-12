package gasproject.redis;

import com.redislabs.modules.rejson.Path;
import gasproject.App;
import gasproject.JSON.GasDTO;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;
import com.redislabs.modules.rejson.JReJSON;
import redis.clients.jedis.util.Pool;
import redis.clients.jedis.util.SafeEncoder;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class Redis {
    JedisPoolConfig jedisPoolConfig;
    JedisPool pool;
    private Pool<Jedis> client;

    //Jedis풀 생성(JedisPoolConfig, host, port, timeout, password)
    Jedis jedis;//thread, db pool처럼 필요할 때마다 getResource()로 받아서 쓰고 다 쓰면 close로 닫아야 한다.

    JReJSON jReJSON;


    public Redis(String host, int port, int timeout, String password) {
        try {
            jedisPoolConfig = new JedisPoolConfig();
            pool = new JedisPool(jedisPoolConfig, host, port, timeout, password);
        }catch (Exception e){
            e.printStackTrace();
            closeRedis();
        }
    }

    public Boolean setMessage(String key, String msg) {
        try {
            jedis = pool.getResource();
            jedis.set(key, msg);
            System.out.println(msg+" is set to " + key);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            closeRedis();
            return false;
        }finally {
        }

    }

    public String getMessage(String key) {
        String result = "ERROR";
        try {
            jedis = pool.getResource();
            result = jedis.get(key);
            System.out.println("data is get: " + result);
        }catch (Exception e){
            e.printStackTrace();
            closeRedis();
        }finally {
        }
        return result;
    }

    public void deleteMessage(String key) {
        try {
            jedis.del(key);
            System.out.println("data is deleted " + key);
        }catch (Exception e){
            e.printStackTrace();
            closeRedis();

        }finally {
        }
    }

    public void closeRedis() {
        if (jedis != null) jedis.close();
        if(pool!=null) pool.close();
    }

    public Boolean pipelineSet(ArrayList<GasDTO> gasDTOArrayList){
        try {
            jedis = pool.getResource();
            Pipeline pipeline = jedis.pipelined();

            for(int i = 0; i < gasDTOArrayList.size(); i++) {
                pipeline.set(Integer.toString(i), gasDTOArrayList.get(i).toString());
            }
            pipeline.sync();
            System.out.println ( "Key value:" + jedis.keys ( "*"));
            return true;
        }catch (Exception e){
            e.printStackTrace();
            closeRedis();
            return false;
        }finally {
        }


    }
    public Boolean pipelinedelete(){
        try {
            jedis = pool.getResource();
            Pipeline p = jedis.pipelined();
            for(Integer i = 0; i < 100000; i++) {
                p.del(i.toString());
            }
            p.sync();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            closeRedis();
            return false;
        }finally {
        }

    }

    public <T> T get(String key, Class<T> clazz, Path... paths) {
//        byte[][] args = new byte[1 + paths.length][];
//        int i=0;
//        args[i] = SafeEncoder.encode(key);
//        for (Path p :paths) {
//            args[++i] = SafeEncoder.encode(p.toString());
//        }
//
//        String rep;
//        try (Jedis conn = getConnection()) {
//            conn.getClient().sendCommand(JReJSON.Command.GET, args);
//            rep = conn.getClient().getBulkReply();
//        }
//        assertReplyNotError(rep);
//        return gson.fromJson(rep, clazz);
return null;
    }

//    private Jedis getConnection() {
//        //return this.client.getResource();
//    }

}
