package moe.lyrebird.view;

import javafx.fxml.FXMLLoader;
import lombok.extern.slf4j.Slf4j;
import moe.lyrebird.view.util.EasyFXML;
import moe.lyrebird.view.util.ViewLoader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

import java.util.ResourceBundle;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

/**
 * Front-end (views-related) components go here.
 */
@Configuration
@Slf4j
public class FrontendComponents {
    
    @Bean
    public ResourceBundle resourceBundle() {
        return ResourceBundle.getBundle("strings");
    }
    
    /**
     * Containerize {@link FXMLLoader} so we can get configured instances
     * of it through Spring. Namely setting Spring as the provider for
     * controller instances.
     *
     * @param context
     *         The spring{@link ApplicationContext}.
     * @return A *new* (non-singleton) configured {@link FXMLLoader}
     */
    @Bean
    @Scope(scopeName = SCOPE_PROTOTYPE)
    public FXMLLoader fxmlLoader(final ApplicationContext context, final ResourceBundle resourceBundle) {
        final FXMLLoader loader = new FXMLLoader();
        loader.setControllerFactory(context::getBean);
        loader.setResources(resourceBundle);
        return loader;
    }
    
    @Bean
    public EasyFXML easyFXML(final ApplicationContext context) {
        return new EasyFXML(context);
    }
    
    @Bean
    public ViewLoader viewLoader(final EasyFXML easyFXML) {
        return new ViewLoader(easyFXML);
    }
    
    @Bean
    public GUIManager guiManager(final Environment environment, final ViewLoader viewLoader, final ResourceBundle resourceBundle) {
        return new GUIManager(environment, viewLoader, resourceBundle);
    }
}
