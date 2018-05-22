package moe.lyrebird.view.components.tweet;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.api.FxmlController;
import lombok.extern.slf4j.Slf4j;
import twitter4j.Status;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageView;

import static moe.lyrebird.view.components.tweet.TweetFormatter.tweetContent;
import static moe.lyrebird.view.components.tweet.TweetFormatter.userProfileImage;
import static moe.lyrebird.view.components.tweet.TweetFormatter.username;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Slf4j
@Component
@Scope(scopeName = SCOPE_PROTOTYPE)
public class TweetPaneController implements FxmlController {

    @FXML
    private Label author;

    @FXML
    private ImageView authorProfilePicture;

    @FXML
    private Label content;

    @FXML
    private ToolBar toolbar;

    @FXML
    private Button likeButton;

    @FXML
    private Button retweetButton;

    private Status status;

    public final BooleanProperty selected = new SimpleBooleanProperty(false);

    @Override
    public void initialize() {
        toolbar.visibleProperty().bind(selected);
    }

    public void setStatus(final Status status) {
        author.setText(username(status.getUser()));
        authorProfilePicture.setImage(userProfileImage(status.getUser()));
        content.setText(tweetContent(status));
        this.status = status;
    }
}