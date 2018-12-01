/*
 *     Lyrebird, a free open-source cross-platform twitter client.
 *     Copyright (C) 2017-2018, Tristan Deloche
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package moe.lyrebird;

import java.awt.Toolkit;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;

import javafx.application.Application;
import javafx.stage.Stage;

import moe.lyrebird.api.client.LyrebirdServerClientConfiguration;
import moe.lyrebird.model.interrupts.CleanupService;
import moe.lyrebird.model.update.compatibility.PostUpdateCompatibilityHelper;
import moe.lyrebird.view.LyrebirdUiManager;
import moe.tristan.easyfxml.EasyFxmlAutoConfiguration;
import moe.tristan.easyfxml.FxApplication;
import moe.tristan.easyfxml.FxUiManager;

/**
 * This class is the entry point for Lyrebird. It bootstraps JavaFX, Spring Boot and AWT and then delegates
 * that work to the {@link LyrebirdUiManager}.
 * <p>
 * More precisely:
 * <p>
 * From the fact that {@link FxApplication} extends {@link Application}, the {@link
 * FxApplication#start(Stage)} method is called with the main stage as argument.
 * <p>
 * This makes it so the spring application context {@link #springContext} fetches the default {@link FxUiManager} whose
 * {@code onStageCreated(Stage)} method will be called with the main stage generated by JavaFX.
 * <p>
 * The {@link Toolkit#getDefaultToolkit()} call is because AWT needs to be instantiated before JavaFX has been started
 * and this only can be done in the {@link #main(String[])} method with the architecture of Lyrebird.
 *
 * @see LyrebirdUiManager
 */
@SpringBootApplication
@EnableCaching
@Import({EasyFxmlAutoConfiguration.class, LyrebirdServerClientConfiguration.class})
public class Lyrebird extends FxApplication {

    /**
     * Main method called on JAR execution.
     * <p>
     * It bootstraps AWT then JavaFX with the Spring delegation left for {@link LyrebirdUiManager} to do.
     *
     * @param args The command line arguments given on JAR execution. Usually empty.
     */
    public static void main(final String[] args) {
        PostUpdateCompatibilityHelper.executeCompatibilityTasks();
        launch(args);
    }

    /**
     * Called by the JavaFX platform (through {@link Application#stop()}) on application closure request.
     * We execute all the operations to be executed on shutdown (with {@link CleanupService}) then shutdown
     * the virtual machine with {@link Runtime#halt(int)}.
     * <p>
     * This shutdown is not a good practice and is only there due to bad practices from the Twitter4J project regarding
     * thread management.
     */
    @Override
    public void stop() {
        springContext.getBean(CleanupService.class).executeCleanupOperations();
        super.stop();
        System.exit(0);
    }

}
