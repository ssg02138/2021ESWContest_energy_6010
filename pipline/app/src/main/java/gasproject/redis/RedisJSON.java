package gasproject.redis;

import com.redislabs.modules.rejson.JReJSON;
import com.redislabs.modules.rejson.Path;
import gasproject.JSON.GasDTO;


@Deprecated
//[20210911] 일반 Redis로 대체 됨
public class RedisJSON {
    JReJSON jReJSON;

    public void initJReJSON(){
        jReJSON = new JReJSON("localhost", 6379);
    }
    public void setJSONtoRedis(){
        //JSON.SET obj .a '{"id":"gas","list":[{"name":"ttt","num":1},{"name":"aa","num":2}]}'

        Path path = new Path(".");
        //jReJSON.set("obj","test",path);
    }

    public void getJSONfromRedis(){
        GasDTO result=(GasDTO)jReJSON.get("obj",GasDTO.class,new Path(".a"));
//        System.out.println(((TestDTO)result.getArrayList().get(0)).getName());
//        System.out.println(result.toString());

    }
}
