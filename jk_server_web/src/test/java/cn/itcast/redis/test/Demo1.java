package cn.itcast.redis.test;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;

public class Demo1 {
	@Test
	public void run7() throws Exception{
		ApplicationContext ac = new ClassPathXmlApplicationContext("classpath:applicationContext-redis.xml");
		RedisTemplate<String, String> t = (RedisTemplate<String, String>) ac.getBean("redisTemplate");
		t.opsForValue().set("msg", "haha");
	}
}
