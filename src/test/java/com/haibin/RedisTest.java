package com.haibin;

import org.junit.jupiter.api.*;
import redis.clients.jedis.*;

import java.util.Set;

/**
 * 用于测试redis功能
 * jedis官方测试用例，https://github.com/xetorthio/jedis/tree/master/src/test/java/redis/clients/jedis
 * Created by abing_hu on 2017/1/9.
 */
public class RedisTest {

    static JedisPool pool;// redis，保证线程安全

    @BeforeAll
    static void initAll(){
        // 初始化jedis pool
        pool = new JedisPool(new JedisPoolConfig(), "114.55.119.39", 6379, 10000, "Teegether!QAZ@WSX#EDC");
    }

    @Test
    @DisplayName("junit5 基本断言测试")
    void myFirstTest(){
        Assertions.assertEquals(2, 1+1);
    }

    @Test
    @DisplayName("redis读写测试")
    void setGetTest()
    {
        try(Jedis jedis = pool.getResource()) {
            jedis.set("hi", "hello world");
            Assertions.assertEquals("hello world", jedis.get("hi"));
        }
    }

    @Test
    @DisplayName("redis pipeling测试")
    void pipelingTest()
    {
        try(Jedis jedis = pool.getResource()){
            Pipeline p = jedis.pipelined();
            // set key-value
            p.set("fool", "bar");
            // set sorted-set
            p.zadd("course", 91, "haibin");
            p.zadd("course", 93, "scott");
            p.zadd("course", 88, "abu");

            // get
            Response<String> pipeString = p.get("fool");
            Response<Set<String>> sose = p.zrange("course", 0, -1);
            p.sync();

            Assertions.assertEquals("bar", pipeString.get());
            Assertions.assertEquals(3, sose.get().size());
        }
    }

    @AfterAll
    static void tearDownAll()
    {
        pool.destroy();
    }
}
