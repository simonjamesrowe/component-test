package com.simonjamesrowe.component.test.redis;

import com.simonjamesrowe.component.test.ComponentTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.annotation.DirtiesContext;

import static org.assertj.core.api.Assertions.assertThat;

@WithRedisContainer
@ComponentTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class WithRedisContainerTest {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void testRedisOperationsWithTestContainer() {
        assertThat(redisTemplate).isNotNull();
        redisTemplate.opsForValue().set("key", "value");
        assertThat(redisTemplate.opsForValue().get("key")).isEqualTo("value");
    }
}
