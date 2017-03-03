package moe.lyrebird.model.twitter4j;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringRunner;
import twitter4j.Twitter;
import twitter4j.conf.Configuration;

/**
 * Created by tristan on 02/03/2017.
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class Twitter4JComponentsTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private Environment environment;

    @Test
    public void configuration() throws Exception {
        final Configuration configuration = this.context.getBean(Configuration.class);
        Assert.assertEquals(
                configuration.getOAuthConsumerSecret(),
                environment.getProperty("twitter.consumerSecret")
        );
        Assert.assertEquals(
                configuration.getOAuthConsumerKey(),
                environment.getProperty("twitter.consumerKey")
        );
    }

    @Test(expected = NullPointerException.class)
    public void twitterConfiguration() throws Exception {
        final TwitterConfiguration twitterConfiguration = this.context.getBean(TwitterConfiguration.class);
        twitterConfiguration.setConfiguration(null);
    }

    @Test
    public void twitter() throws Exception {
        final Twitter twitter = this.context.getBean(Twitter.class);
        Assert.assertNotNull(twitter);
    }
}