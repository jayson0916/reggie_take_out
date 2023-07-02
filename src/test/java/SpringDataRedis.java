import org.junit.Test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;


/**
 * @Author: Jayson_P
 * @Date: 2023/07/02/10:55
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes =SpringDataRedis.class )
public class SpringDataRedis {

    @Resource
    private RedisTemplate redisTemplate;

    @Test
    public void  testString(){
        redisTemplate.opsForValue().set("city","beijing");
    }
}
