package com.mj.tic.tac.toe.javafx.java.dialog;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.events.JFXDialogEvent;
import com.mj.tic.tac.toe.javafx.java.util.ResourceBundleUtil;
import java.net.URL;
import java.util.ResourceBundle;
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
 *   <li>Receives its controller initialisation callback through
 *       {@link #initialize(URL, ResourceBundle)} (part of the
 *       {@link javafx.fxml.Initializable} contract).</li>
 *   <li>Supports dismissing the dialog by pressing the <b>ESCAPE</b> key.</li>
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
 * </ol>
 *
 * @param <R> the type of the root pane loaded from FXML (typically
 *            {@link javafx.scene.layout.BorderPane}).
 * @author Montaser Sbaih
 * @version 1.0
 * @email montaser.jjs@gmail.com
 * @phone +962-786258874
 * @since 15-10-2022
 */
public abstract class BaseDialog<R extends Pane> extends JFXDialog implements Initializable {

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
     * @param resources the resources used to localise the root object, or
     *                  {@code null} if not localised.
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
     * Sets the background colour of the dialog's parent overlay pane.
     *
     * @param color the colour to apply; {@link Color#TRANSPARENT} is typical
     *              for a clean overlay appearance.
     */
    protected void setParentBackground(Color color) {
        Pane pane = (Pane) getContent().getParent();

        Background background = new Background(new BackgroundFill(color, null, null));
        pane.setBackground(background);
    }

    /**
     * Looks up a localised string by resource key.
     *
     * @param key the resource bundle key.
     * @return the localised string.
     */
    protected final String getString(String key) {
        return ResourceBundleUtil.getString(key);
    }

    /**
     * Handles key-pressed events for the dialog. Currently closes the dialog
     * when the <b>ESCAPE</b> key is pressed.
     *
     * @param event the key event to process.
     */
    protected void onDialogKeyPressed(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ESCAPE)) {
            this.close();
        }
    }
}
