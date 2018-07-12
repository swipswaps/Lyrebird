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

package moe.lyrebird.view.screens.credits;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import moe.tristan.easyfxml.EasyFxml;
import moe.tristan.easyfxml.api.FxmlController;
import moe.tristan.easyfxml.model.awt.integrations.BrowserSupport;
import moe.tristan.easyfxml.model.components.listview.ComponentListViewFxmlController;
import moe.tristan.easyfxml.model.exception.ExceptionHandler;
import moe.tristan.easyfxml.model.fxml.FxmlLoadResult;
import moe.tristan.easyfxml.util.Buttons;
import moe.tristan.easyfxml.util.Stages;
import moe.lyrebird.model.credits.CreditsService;
import moe.lyrebird.model.credits.objects.CredittedWork;
import moe.lyrebird.model.update.UpdateService;
import moe.lyrebird.view.components.cells.CreditsCell;
import moe.lyrebird.view.screens.Screens;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.ReadOnlyListWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

import java.net.URL;

@Lazy
@Component
public class CreditsScreenController extends ComponentListViewFxmlController<CredittedWork> {

    private static final Logger LOG = LoggerFactory.getLogger(CreditsScreenController.class);

    @FXML
    private Button licenseButton;

    @FXML
    private Button sourceCodeButton;

    @FXML
    private Button knownIssuesButton;

    @FXML
    private Button updatesButton;

    private final EasyFxml easyFxml;
    private final CreditsService creditsService;
    private final BrowserSupport browserSupport;
    private final Environment environment;
    private final UpdateService updateService;

    @Autowired
    public CreditsScreenController(
            final ApplicationContext context,
            final EasyFxml easyFxml,
            final CreditsService creditsService,
            final BrowserSupport browserSupport,
            final Environment environment,
            final UpdateService updateService
    ) {
        super(context, CreditsCell.class);
        this.easyFxml = easyFxml;
        this.creditsService = creditsService;
        this.browserSupport = browserSupport;
        this.environment = environment;
        this.updateService = updateService;
    }

    @Override
    public void initialize() {
        super.initialize();
        LOG.debug("Loading credits...");
        listView.itemsProperty().bind(new ReadOnlyListWrapper<>(creditsService.creditedWorks()));

        bindButtonToOpenHrefEnvProperty(licenseButton, "credits.license");
        bindButtonToOpenHrefEnvProperty(sourceCodeButton, "credits.sourceCode");
        bindButtonToOpenHrefEnvProperty(knownIssuesButton, "credits.knownIssues");
        updatesButton.setOnAction(e -> updateService.isUpdateAvailable().thenAcceptAsync(updateAvailable -> {
            if (updateAvailable) {
                openUpdatesScreen();
            }
        }));
    }

    private void bindButtonToOpenHrefEnvProperty(final Button button, final String prop) {
        final URL actualUrl = environment.getRequiredProperty(prop, URL.class);
        Buttons.setOnClick(button, () -> browserSupport.openUrl(actualUrl));
    }

    private void openUpdatesScreen() {
        final FxmlLoadResult<Pane, FxmlController> updateScreenLoadResult = easyFxml.loadNode(Screens.UPDATE_VIEW);
        final Pane updatePane = updateScreenLoadResult.getNode().getOrElseGet(ExceptionHandler::fromThrowable);
        Stages.stageOf("Updates", updatePane).thenAcceptAsync(Stages::scheduleDisplaying);
    }

}
