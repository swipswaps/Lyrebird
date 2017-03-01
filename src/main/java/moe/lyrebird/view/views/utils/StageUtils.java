package moe.lyrebird.view.views.utils;

import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Created by Tristan on 01/03/2017.
 */
public class StageUtils {
    public static Stage stageOf(final String title, final Scene scene) {
        final Stage stage = new Stage(StageStyle.DECORATED);
        stage.setTitle(title);
        stage.setScene(scene);
        return stage;
    }
}
