package moe.lyrebird.model.twitter;

import org.springframework.stereotype.Component;
import moe.lyrebird.model.sessions.Session;
import moe.lyrebird.model.sessions.SessionManager;
import moe.lyrebird.model.twitter.twitter4j.TwitterUserListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.TwitterStream;

import javafx.beans.property.Property;

@Component
public class TwitterStreamingService {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterStreamingService.class);

    private final TwitterStream twitterStream;
    private final SessionManager sessionManager;
    private final TwitterUserListener twitterUserListener;

    public TwitterStreamingService(
            TwitterStream twitterStream, SessionManager sessionManager,
            TwitterUserListener twitterUserListener
    ) {
        LOG.debug("Starting twitter streaming management...");
        this.twitterStream = twitterStream;
        this.sessionManager = sessionManager;
        this.twitterUserListener = twitterUserListener;
        startListening();
        LOG.debug("Started twitter streaming management!");
    }

    private void startListening() {
        final Property<Session> currentSessionBinding = sessionManager.currentSessionProperty();
        currentSessionBinding.addListener((observable, oldValue, newValue) -> {
            if (oldValue != null) {
                LOG.info("Stopping streaming for session {}", oldValue);
            }
            LOG.info("Starting streaming for session {}", newValue);
            closeSession();
            switchToSession(newValue);
        });
    }

    private void switchToSession(final Session newSession) {
        twitterStream.setOAuthAccessToken(newSession.getAccessToken());
        twitterStream.addListener(twitterUserListener);
        twitterStream.user();
    }

    private void closeSession() {
        twitterStream.clearListeners();
        twitterStream.cleanUp();
        twitterStream.shutdown();
    }

}
