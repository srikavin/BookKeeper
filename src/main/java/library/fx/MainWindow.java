package library.fx;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class MainWindow extends BaseController implements Initializable {
    @FXML
    private Text currentDate;
    @FXML
    private Pane container;
    @FXML
    private Pane rootPane;
    @FXML
    private Pane header;
    @FXML
    private Pane dateContainer;
    @FXML
    private Pane headerBackground;
    @FXML
    private Rectangle patronButton;

    /**
     * An event handler called when books is clicked
     *
     * @param event Mouse Event of the click triggering this event handler
     */
    @FXML
    private void books(MouseEvent event) {
        getInitializer().setContent("Books.fxml");
    }

    /**
     * An event handler called when help is clicked
     *
     * @param event Mouse Event of the click triggering this event handler
     */
    @FXML
    private void help(MouseEvent event) {
        getInitializer().loadHelp();
    }

    /**
     * An event handler called when patrons is clicked
     *
     * @param event Mouse Event of the click triggering this event handler
     */
    @FXML
    private void patrons(MouseEvent event) {
        getInitializer().setContent("Patrons.fxml");
    }

    /**
     * An event handler called when reports is clicked
     *
     * @param event Mouse Event of the click triggering this event handler
     */
    @FXML
    private void reports(MouseEvent event) {
        getInitializer().setContent("Reports.fxml");
    }

    /**
     * An event handler called when types is clicked
     *
     * @param event Mouse Event of the click triggering this event handler
     */
    @FXML
    private void types(MouseEvent event) {
        getInitializer().setContent("PatronTypes.fxml");
    }

    @FXML
    private void checkout(MouseEvent event) {
        getInitializer().setContent("Checkout.fxml");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Set the date in the UI to the formatted date right now when it is initialized
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, u");
        currentDate.setText(formatter.format(LocalDate.now()));

        Platform.runLater(() -> {
            SpotlightManager manager = new SpotlightManager(rootPane, 0, 0);
            manager.trigger(patronButton);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void animateIn(EventHandler<ActionEvent> callback) {
        animateInTimeline.setCycleCount(1);

        //Set starting point of the animation - currently set to off-screen
        animateInTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(0),
                callback, // Call the callback after the initial state has been set
                new KeyValue(container.translateYProperty(), 500),
                new KeyValue(dateContainer.translateXProperty(), 500),
                new KeyValue(header.translateXProperty(), -500),
                new KeyValue(rootPane.opacityProperty(), 0),
                new KeyValue(dateContainer.rotateProperty(), 0)));

        animateInTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(450), "Animate Date Container Rotation",
                new KeyValue(rootPane.opacityProperty(), 1, Interpolator.EASE_IN)));

        //Run the animation
        animateInTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(700),
                "Animate Properties",
                new KeyValue(container.translateYProperty(), 0, Interpolator.EASE_IN)));

        animateInTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(1200), "Animate Date Container Position",
                new KeyValue(dateContainer.translateXProperty(), 0, Interpolator.EASE_IN)));

        animateInTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(1200), "Animate Date Container Rotation",
                new KeyValue(dateContainer.rotateProperty(), -2, Interpolator.EASE_IN)));

        animateInTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(1200), "Animate Date Container Rotation",
                new KeyValue(header.translateXProperty(), 0, Interpolator.EASE_IN)));

        //Start the animation
        animateInTimeline.play();
    }

    @Override
    public void animateOut(EventHandler<ActionEvent> callback) {
        animateOutTimeline.setCycleCount(1);

        animateOutTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(0),
                new KeyValue(dateContainer.rotateProperty(), -2),
                new KeyValue(dateContainer.translateXProperty(), 0),
                new KeyValue(header.translateYProperty(), 0),
                new KeyValue(container.translateXProperty(), 0),
                new KeyValue(headerBackground.scaleYProperty(), 1),
                new KeyValue(headerBackground.translateYProperty(), 0)
        ));

        animateOutTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(300),
                new KeyValue(dateContainer.rotateProperty(), 0, Interpolator.EASE_IN)));

        animateOutTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(350),
                new KeyValue(dateContainer.translateXProperty(), 500, Interpolator.EASE_IN)));

        animateOutTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(400),
                new KeyValue(header.translateYProperty(), -750, Interpolator.EASE_IN)));

        animateOutTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(500),
                new KeyValue(container.translateXProperty(), 1000, Interpolator.EASE_IN)));

        //Run the animation
        animateOutTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(550),
                callback,
                new KeyValue(headerBackground.scaleYProperty(), 47.0 / 90.0, Interpolator.EASE_IN),
                new KeyValue(headerBackground.translateYProperty(), -27, Interpolator.EASE_IN)));

        //Delay for 25 ms
        animateOutTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(575)));

        //Start the animation
        animateOutTimeline.play();
    }
}
