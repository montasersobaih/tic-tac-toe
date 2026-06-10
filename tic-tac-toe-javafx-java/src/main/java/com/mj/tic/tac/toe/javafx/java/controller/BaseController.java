package com.mj.tic.tac.toe.javafx.java.controller;

import com.mj.tic.tac.toe.javafx.java.util.ResourceBundleUtil;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

/**
 * Base class for JavaFX controllers in the application.
 *
 * <p>This class provides shared controller functionality that can be reused by
 * all concrete FXML controllers. It centralizes access to the
 * {@link ControllerMediator}, which allows controllers to communicate through a
 * common mediator instead of depending directly on each other, and it exposes a
 * convenience method for reading localized text from the application's resource
 * bundle.</p>
 *
 * <p>Subclasses must implement {@link #initialize(URL, ResourceBundle)} to
 * perform their own JavaFX initialization after the related FXML file has been
 * loaded.</p>
 *
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 20-01-2023
 */

public abstract class BaseController implements Initializable {

    /**
     * Mediator used to publish controller events and state changes to other
     * interested application components.
     */
    protected ControllerMediator mediator;

    /**
     * Initializes the controller after its FXML view has been loaded.
     *
     * <p>The JavaFX runtime calls this method automatically. Each subclass is
     * responsible for implementing its own setup logic, such as binding
     * properties, configuring controls, registering event handlers, or preparing
     * initial UI state.</p>
     *
     * @param url       the location used to resolve relative paths for the root
     *                  object, or {@code null} if the location is unknown.
     * @param resources the resources used to localize the root object, or
     *                  {@code null} if no resource bundle was provided.
     */
    @Override
    public abstract void initialize(URL url, ResourceBundle resources);

    /**
     * Assigns the mediator used by this controller.
     *
     * <p>The mediator should be set by the application wiring code before the
     * controller needs to publish or react to cross-controller events. Passing
     * {@code null} removes the mediator reference.</p>
     *
     * @param mediator the mediator instance shared between controllers.
     */
    public final void setMediator(ControllerMediator mediator) {
        this.mediator = mediator;
    }

    /**
     * Looks up a localized string from the application's resource bundle.
     *
     * @param key the resource bundle key.
     * @return the localized value associated with the given key.
     */
    protected final String getLocalizedText(String key) {
        return ResourceBundleUtil.getString(key);
    }
}
