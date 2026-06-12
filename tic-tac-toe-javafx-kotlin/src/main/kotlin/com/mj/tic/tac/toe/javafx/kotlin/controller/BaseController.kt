package com.mj.tic.tac.toe.javafx.kotlin.controller

import com.mj.tic.tac.toe.javafx.kotlin.util.ResourceBundleUtil
import java.net.URL
import java.util.ResourceBundle
import javafx.fxml.Initializable

/**
 * Abstract base class for all FXML controllers in the application.
 *
 * Implements [Initializable] to participate in the JavaFX FXML
 * initialisation lifecycle. Provides shared functionality:
 * - A [ControllerMediator] reference for delegating background tasks
 *   to the parent [com.mj.tic.tac.toe.javafx.kotlin.controller.view.ViewController].
 * - An i18n utility method for localized string lookups.
 *
 * All concrete controllers in the `controller.layout` and
 * `controller.view` packages extend this class.
 *
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 20-01-2023
 */

abstract class BaseController : Initializable {

    /**
     * The mediator used to execute background tasks.
     *
     * Injected by the parent [com.mj.tic.tac.toe.javafx.kotlin.controller.view.ViewController] during initialization
     * via [setMediator]. Controllers dispatch tasks such as
     * [com.mj.tic.tac.toe.javafx.kotlin.task.PlayGameTask] and
     * [com.mj.tic.tac.toe.javafx.kotlin.task.ResetGameTask] through
     * this mediator.
     */
    protected var mediator: ControllerMediator? = null
        private set

    /**
     * Called by JavaFX after FXML loading to initialize the controller.
     *
     * Subclasses must set up their UI bindings, event listeners, and
     * any other initialization logic here.
     *
     * @param url The location used to resolve relative paths for the
     *   root object, or null if the location is not known.
     * @param resources The resources used to localize the root object,
     *   or null if the root object was not localized.
     */
    abstract override fun initialize(url: URL, resources: ResourceBundle)

    /**
     * Injects the [ControllerMediator] reference into this controller.
     *
     * Called by [com.mj.tic.tac.toe.javafx.kotlin.controller.view.ViewController] to establish the mediator pattern
     * connection. The mediator is used to execute background tasks
     * that perform game logic asynchronously.
     *
     * @param mediator The mediator to set, or null to clear the reference.
     */
    fun setMediator(mediator: ControllerMediator?) {
        this.mediator = mediator
    }

    /**
     * Resolves a localized string from the application's resource bundles.
     *
     * Convenience method delegating to [ResourceBundleUtil.getString].
     *
     * @param key The resource bundle key to look up.
     * @return The localized string for the given key, or the key itself
     *   if not found in any bundle.
     */
    protected fun getLocalizedText(key: String): String = ResourceBundleUtil.getString(key)
}