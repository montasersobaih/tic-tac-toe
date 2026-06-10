package com.mj.tic.tac.toe.javafx.java.dialog;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.events.JFXDialogEvent;
import com.mj.tic.tac.toe.javafx.java.util.ResourceBundleUtil;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 * Abstract base class for all custom dialogs in the application.
 *
 * <p>Wraps the third-party {@link JFXDialog} (JFoenix) to provide a consistent
 * foundation for modal overlay dialogs. Every concrete subclass:</p>
 *
 * <ul>
 *   <li>Is positioned as an overlay inside a given {@link StackPane} container.</li>
 *   <li>Loads its UI layout from a corresponding FXML resource via
 *       {@link #initializeLayout()}.</li>
 *   <li>Receives its controller initialization callback through
 *       {@link #initialize(URL, ResourceBundle)} (part of the
 *       {@link javafx.fxml.Initializable} contract).</li>
 *   <li>Supports dismissing the dialog by pressing the <b>ESCAPE</b> key.</li>
 *   <li>Can return a strongly typed result value when shown through
 *       {@link #showAndWait()}.</li>
 * </ul>
 *
 * <h3>Subclassing contract</h3>
 * Subclasses must:
 * <ol>
 *   <li>Implement {@link #initializeLayout()} to load and return the FXML
 *       root node.</li>
 *   <li>Implement {@link #initialize(URL, ResourceBundle)} for any post-FXML
 *       setup (can be left empty if not needed).</li>
 *   <li>Call {@code super(container)} from the constructor, which calls
 *       {@link #initializeLayout()} internally — therefore subclass
 *       fields referenced inside that method must be initialised
 *       <em>before</em> the super-constructor invocation is complete.</li>
 *   <li>Use {@link #updateValueAndClose(Object)} when the user completes the
 *       dialog with a value, for example by pressing a confirm or selection
 *       button.</li>
 * </ol>
 *
 * <h3>Result handling</h3>
 * The {@code V} type parameter represents the value produced by the dialog.
 * For example, a confirmation dialog can use {@link Boolean}, while a
 * difficulty-selection dialog can use a domain enum. Calling
 * {@link #showAndWait()} displays the dialog and enters a nested JavaFX event
 * loop until the dialog is closed. UI events continue to be processed while
 * the caller waits for the result.
 *
 * <p>If the dialog is closed without calling {@link #updateValueAndClose(Object)}
 * first, the result is {@code null}.</p>
 *
 * @param <R> the type of the root pane loaded from FXML (typically
 *            {@link javafx.scene.layout.BorderPane}).
 * @param <V> the type of result value returned by {@link #showAndWait()}.
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 15-10-2022
 */
public abstract class BaseDialog<R extends Pane, V> extends JFXDialog implements Initializable {

    /**
     * Stores the result value produced by the dialog.
     *
     * <p>The value is set by {@link #updateValueAndClose(Object)} before the
     * dialog is closed. {@link #showAndWait()} reads this property when the
     * {@link JFXDialogEvent#CLOSED} event exits the nested event loop.</p>
     */
    private final ObjectProperty<V> dialogValue = new SimpleObjectProperty<>();

    /**
     * Constructs a new modal dialog over the given container.
     *
     * <p>The dialog overlay is configured with the following defaults:</p>
     * <ul>
     *   <li>Overlay close is disabled — clicking outside the dialog will
     *       <em>not</em> dismiss it.</li>
     *   <li>Pressing the <b>ESCAPE</b> key dismisses the dialog.</li>
     *   <li>The dialog automatically requests focus when opened.</li>
     * </ul>
     *
     * @param container the parent {@link StackPane} that hosts the dialog overlay.
     */
    protected BaseDialog(StackPane container) {
        super.setDialogContainer(container);
        super.setOverlayClose(false);
        super.setContent(initializeLayout());
        super.setEventHandler(KeyEvent.ANY, this::onDialogKeyPressed);
        super.addEventHandler(JFXDialogEvent.OPENED, event -> requestFocus());
    }

    /**
     * Called after the FXML is loaded to perform any post-init setup.
     *
     * @param location  the location used to resolve relative paths for the
     *                  root object, or {@code null} if unknown.
     * @param resources the resources used to localize the root object, or
     *                  {@code null} if not localized.
     */
    @Override
    public abstract void initialize(URL location, ResourceBundle resources);

    /**
     * Loads the FXML layout and returns the root pane.
     *
     * <p>This method is invoked from the super-constructor, so subclass
     * fields must be set before {@code super(container)} completes.</p>
     *
     * @return the root pane created from the FXML resource.
     */
    protected abstract R initializeLayout();

    /**
     * Sets the background color of the dialog's parent overlay pane.
     *
     * @param color the color to apply; {@link Color#TRANSPARENT} is typical
     *              for a clean overlay appearance.
     */
    protected void setParentBackground(Color color) {
        Pane pane = (Pane) getContent().getParent();

        Background background = new Background(new BackgroundFill(color, null, null));
        pane.setBackground(background);
    }

    /**
     * Looks up a localized string by resource key.
     *
     * @param key the resource bundle key.
     * @return the localized string.
     */
    protected final String getString(String key) {
        return ResourceBundleUtil.getString(key);
    }

    /**
     * Handles key-pressed events for the dialog. Currently, closes the dialog
     * when the <b>ESCAPE</b> key is pressed.
     *
     * @param event the key event to process.
     */
    protected void onDialogKeyPressed(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ESCAPE)) {
            this.close();
        }
    }

    /**
     * Returns the current dialog result value.
     *
     * <p>This is mainly useful for subclasses or close handlers that need to
     * inspect the value after a user action. A {@code null} value means either
     * no result has been set yet, or the dialog was closed through a path such
     * as ESCAPE or the close button.</p>
     *
     * @return the current result value, possibly {@code null}
     */
    protected V getDialogValue() {
        return dialogValue.get();
    }

    /**
     * Stores the dialog result and closes the dialog.
     *
     * <p>Call this from concrete dialog actions when the user completes the
     * dialog with a meaningful result. Examples include confirming a yes/no
     * dialog or choosing an item from a selection dialog.</p>
     *
     * <p>The close operation is safe to request from any thread. If this method
     * is called from the JavaFX Application Thread, the dialog is closed
     * immediately. Otherwise, the close request is queued with
     * {@link Platform#runLater(Runnable)}.</p>
     *
     * @param value the result value to return from {@link #showAndWait()}
     */
    protected void updateValueAndClose(V value) {
        dialogValue.set(value);

        if (Platform.isFxApplicationThread()) {
            super.close();
        } else {
            Platform.runLater(super::close);
        }
    }

    /**
     * Shows the dialog and blocks until it is closed.
     *
     * <p>{@link JFXDialog} does not provide a blocking result API, so this
     * method implements one with {@link Platform#enterNestedEventLoop(Object)}.
     * The method must be called from the JavaFX Application Thread. While it is
     * waiting, JavaFX continues to process UI events, allowing the user to
     * interact with the dialog normally.</p>
     *
     * <p>The nested event loop exits when the dialog fires
     * {@link JFXDialogEvent#CLOSED}. The returned value is whatever was last
     * passed to {@link #updateValueAndClose(Object)}, or {@code null} if the
     * dialog closed without a stored result.</p>
     *
     * @return the dialog result, or {@code null} if no result was provided
     * @throws IllegalStateException if called from outside the JavaFX
     *                               Application Thread
     */
    @SuppressWarnings("unchecked")
    public V showAndWait() {
        if (!Platform.isFxApplicationThread()) {
            throw new IllegalStateException("showAndWait() must be called from the JavaFX Application Thread.");
        }

        EventHandler<JFXDialogEvent> handler = event -> Platform.exitNestedEventLoop(BaseDialog.this, dialogValue.get());
        super.addEventHandler(JFXDialogEvent.CLOSED, handler);
        try {
            super.show();
            return (V) Platform.enterNestedEventLoop(this);
        } finally {
            super.removeEventHandler(JFXDialogEvent.CLOSED, handler);
        }
    }
}
