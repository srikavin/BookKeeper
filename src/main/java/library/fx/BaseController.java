package library.fx;

import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public abstract class BaseController {
    /**
     * Should be used for animating the in animations. Used in {@link #animateIn(EventHandler)}
     */
    protected final Timeline animateInTimeline = new Timeline();
    /**
     * Should be used for animating the in animations. Used in {@link #animateOut(EventHandler)}
     */
    protected final Timeline animateOutTimeline = new Timeline();
    private FXInitializer initializer;

    /**
     * Sets a {@link FXInitializer}. This is used to open new windows and change currently displayed content.
     * Must not be called with null.
     *
     * @param initializer The FXInitializer object to use.
     * @throws IllegalArgumentException If the argument passed is null
     */
    public void setFXInitializer(FXInitializer initializer) {
        //Check for null before setting
        if (initializer == null) {
            throw new IllegalArgumentException("Initializer cannot be null");
        }
        this.initializer = initializer;
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
     * Stops all animations currently running
     */
    public void stopAnimation() {
        animateInTimeline.stop();
        animateOutTimeline.stop();
    }

    /**
     * Called after the window is visible and the root element has been loaded.
     * Should be used to implement any animations upon transitioning to this screen.
     *
     * @param callback Should be run as soon as the initial state of the animation is set.
     */
    public void animateIn(EventHandler<ActionEvent> callback) {

    }

    /**
     * Called when leaving this window.
     * Should be used to implement any animations before leaving this screen.
     *
     * @param callback Should be run as soon as the last animation is finished.
     */
    public void animateOut(EventHandler<ActionEvent> callback) {

    }
}
