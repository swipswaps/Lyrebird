package moe.lyrebird.model.sessions;

import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import twitter4j.auth.AccessToken;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class SessionRepositoryTest {

    @Autowired
    private SessionRepository sessionRepository;

    private Session session = null;

    @Before
    public void setUp() {
        session = new Session(
                "test_uid",
                new AccessToken("test_token", "test_token_secret", 1010)
        );
    }

    @Test
    public void testSave() {
        final Session saved = sessionRepository.save(session);
        final Session sessionTryFind = sessionRepository.findOne("test_uid");
        assertThat(sessionTryFind).isEqualTo(saved);
        log.info(
                "Saved sessions : {}",
                sessionRepository.findAll().toString()
        );
    }

    @After
    public void cleanUp() {
        sessionRepository.deleteAll();
    }

}