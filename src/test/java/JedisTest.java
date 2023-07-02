import org.junit.jupiter.api.Test;
import redis.clients.jedis.Jedis;

/**
 * @Author: Jayson_P
 * @Date: 2023/07/02/0:12
 */
public class JedisTest {
    @Test
    public void testRedis(){
        //获取连接
        Jedis jedis = new Jedis("localhost", 6379);
        //执行操作
        jedis.set("username","xiaoming");
        String username = jedis.get("username");
        System.out.println(username);

        jedis.del("username");
        //关闭连接
        jedis.close();
    }
}
