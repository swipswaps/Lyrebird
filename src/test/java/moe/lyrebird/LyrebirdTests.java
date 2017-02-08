package moe.lyrebird;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LyrebirdTests {
    
    @Autowired
    private ApplicationContext context;
    
    @Test
    public void contextLoads() {
        Assertions.assertNotNull(this.context);
    }
}