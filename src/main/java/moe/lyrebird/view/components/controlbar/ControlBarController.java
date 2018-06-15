package moe.lyrebird.view.components.controlbar;

import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.EasyFxml;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.model.beanmanagement.StageManager;
import moe.tristan.easyfxml.model.exception.ExceptionHandler;
import moe.tristan.easyfxml.util.Stages;
import moe.lyrebird.model.sessions.SessionManager;
import moe.lyrebird.view.components.Components;
import moe.lyrebird.view.screens.Screens;
import moe.lyrebird.view.screens.root.RootViewController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import static javafx.scene.input.MouseEvent.MOUSE_CLICKED;
import static moe.lyrebird.view.screens.Screens.LOGIN_VIEW;
import static moe.lyrebird.view.screens.Screens.TWEET_VIEW;

@Component
public class ControlBarController implements FxmlController {

    private static final Logger LOG = LoggerFactory.getLogger(ControlBarController.class);

    @FXML
    private Label currentUserLabel;

    @FXML
    private Button loginButton;

    @FXML
    private Button tweetButton;

    @FXML
    private Button timelineViewButton;

    @FXML
    private Button mentionsViewButton;

    @FXML
    private Button directMessagesViewButton;

    private final EasyFxml easyFxml;
    private final StageManager stageManager;
    private final SessionManager sessionManager;
    private final RootViewController rootViewController;

    public ControlBarController(
            final EasyFxml easyFxml,
            final StageManager stageManager,
            final SessionManager sessionManager,
            final RootViewController rootViewController
    ) {
        this.easyFxml = easyFxml;
        this.stageManager = stageManager;
        this.sessionManager = sessionManager;
        this.rootViewController = rootViewController;
    }

    @Override
    public void initialize() {
        this.loginButton.addEventHandler(MOUSE_CLICKED, e -> openLoginWindow());
        this.tweetButton.addEventHandler(MOUSE_CLICKED, e -> openTweetWindow());

        bindButtonToLoadingView(timelineViewButton, Components.TIMELINE);
        bindButtonToLoadingView(mentionsViewButton, Components.MENTIONS);
        bindButtonToLoadingView(directMessagesViewButton, Components.DIRECT_MESSAGES);

        this.currentUserLabel.textProperty().bind(sessionManager.currentSessionUsernameProperty());
    }

    private void openLoginWindow() {
        LOG.info("User requested login.");
        final Pane loginPane = this.easyFxml
                .loadNode(LOGIN_VIEW)
                .getNode()
                .getOrElseGet(err -> new ExceptionHandler(err).asPane());

        Stages.stageOf("Login", loginPane)
              .thenCompose(Stages::scheduleDisplaying)
              .thenAccept(stage -> this.stageManager.registerSingle(Screens.LOGIN_VIEW, stage));
    }

    private void openTweetWindow() {
        LOG.info("Opening new tweet stage...");
        final Pane tweetPane = this.easyFxml
                .loadNode(TWEET_VIEW)
                .getNode()
                .getOrElseGet(err -> new ExceptionHandler(err).asPane());

        Stages.stageOf("Tweet", tweetPane)
              .thenCompose(Stages::scheduleDisplaying)
              .thenAccept(stage -> this.stageManager.registerSingle(TWEET_VIEW, stage))
              .thenRun(() -> LOG.info("New tweet stage opened !"));
    }

    private void bindButtonToLoadingView(final Button button, final Components component) {
        button.addEventHandler(MOUSE_CLICKED, e ->
                Platform.runLater( () -> rootViewController.setContent(component))
        );
    }
}
