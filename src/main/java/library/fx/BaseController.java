package library.fx;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import library.data.Library;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * This class serves as a base for other GUI's to implement.
 * The class is made to be extremely generic and should be extended to include custom functionality.
 * This class is abstract and cannot be instantiated.
 *
 * @author Srikavin Ramkumar
 */
public abstract class BaseController implements Initializable {
    /**
     * Should be used for animating the in animations. Used in {@link #animateIn(EventHandler)}
     */
    protected final Timeline animateInTimeline = new Timeline();
    /**
     * Should be used for animating the in animations. Used in {@link #animateOut(EventHandler)}
     */
    protected final Timeline animateOutTimeline = new Timeline();
    /**
     * Used for highlighting fields as incorrect or containing an error
     */
    protected final PseudoClass errorClass = PseudoClass.getPseudoClass("invalid-input");
    private SpotlightManager spotlightManager;
    @FXML
    protected Pane container;
    @FXML
    protected Pane rootPane;
    private FXInitializer initializer;
    private Library library;
    @FXML
    private Pane header;
    @FXML
    private Pane headerBackground;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.spotlightManager = new SpotlightManager(rootPane);
        registerSpotlightItems(spotlightManager);
    }

    /**
     * Sets a {@link FXInitializer}. This is used to open new windows and change currently displayed content.
     * Must not be called with null.
     *
     * @param initializer The FXInitializer object to use.
     * @param library     The Library object to use.
     *
     * @throws IllegalArgumentException If the arguments passed are null
     */
    public void initialize(FXInitializer initializer, Library library) {
        //Check for null before setting
        if (initializer == null) {
            throw new IllegalArgumentException("Initializer cannot be null");
        }
        if (library == null) {
            throw new IllegalArgumentException("Library cannot be null");
        }
        this.initializer = initializer;
        this.library = library;
    }

    /**
     * Returns to the mainwindow.
     * Any currently active spotlights will be disabled and the {@link SpotlightManager} will be cleared of all registered spotlights.
     */
    @FXML
    protected void goHome(Event event) {
        getInitializer().setContent("MainWindow.fxml");
        spotlightManager.disable();
    }

    /**
     * Returns a {@link FXInitializer}. This is used to open new windows and change currently displayed content.
     *
     * @return a FXInitializer
     */
    protected FXInitializer getInitializer() {
        return initializer;
    }

    /**
     * Called after the window is visible and the root element has been loaded.
     * Should be used to implement any animations upon transitioning to this screen.
     *
     * @param callback Should be run as soon as the initial state of the animation is set.
     */
    public void animateIn(EventHandler<ActionEvent> callback) {
        final Timeline timeline = animateInTimeline;
        timeline.setCycleCount(1);

        //Set starting point of the animation - currently set to off-screen
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(0),
                callback,
                new KeyValue(container.opacityProperty(), 0),
                new KeyValue(header.translateYProperty(), -150)));

        //Run the animation
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(450),
                new KeyValue(container.opacityProperty(), 1, Interpolator.EASE_IN),
                new KeyValue(header.translateYProperty(), 0, Interpolator.EASE_IN)));

        //Start the animation
        timeline.play();
    }

    @FXML
    private void onSpotlightHelp(MouseEvent event) {
        spotlightManager.trigger();
    }

    /**
     * Used to register any desired nodes in the scene with the current {@link SpotlightManager}
     *
     * @param spotlightManager The spotlight manager to register nodes with
     */
    protected void registerSpotlightItems(SpotlightManager spotlightManager) {

    }

    /**
     * Get the {@link Library} instance this controller is tied to
     *
     * @return The library instance this controller is tied to
     */
    protected Library getLibrary() {
        return library;
    }

    /**
     * Called when leaving this window.
     * Should be used to implement any animations before leaving this screen.
     *
     * @param callback Should be run as soon as the last animation is finished.
     */
    public void animateOut(EventHandler<ActionEvent> callback) {
        final Timeline timeline = animateOutTimeline;
        timeline.setCycleCount(1);

        //Set starting point of the animation - currently set to off-screen
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(0),
                new KeyValue(headerBackground.scaleYProperty(), 1, Interpolator.EASE_IN),
                new KeyValue(header.translateYProperty(), 0, Interpolator.EASE_IN)));

        //Run the animation
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(150),
                new KeyValue(header.translateYProperty(), -150),
                new KeyValue(headerBackground.scaleYProperty(), 90.0 / 47.0, Interpolator.EASE_IN)
        ));

        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(250), callback,
                new KeyValue(headerBackground.translateYProperty(), 23, Interpolator.EASE_IN)));

        //Start the animation
        timeline.play();
    }

    /**
     * Called to initialize data views in the view. Requires {@link #initialize(FXInitializer, Library)} to have been called.
     */
    public void initializeData() {

    }
}
