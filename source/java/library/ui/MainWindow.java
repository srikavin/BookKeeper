package library.ui;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * The controller for the MainWindow view. Includes links to other controllers and uses an event-driven design to
 * maintain performance and code designs.
 */
public class MainWindow extends BaseController {
    @FXML
    private Text schoolName;
    @FXML
    private Text currentDate;
    @FXML
    private Pane container;
    @FXML
    private Pane rootPane;
    @FXML
    private Pane header;
    @FXML
    private TilePane tiles;
    @FXML
    private Pane dateContainer;
    @FXML
    private Pane headerBackground;
    @FXML
    private Pane patronsTile;
    @FXML
    private Pane typesTile;
    @FXML
    private Pane booksTile;
    @FXML
    private Pane reportsTile;
    @FXML
    private Pane checkoutTile;

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

    /**
     * An event handler called when checkout is clicked
     *
     * @param event Mouse Event of the click triggering this event handler
     */
    @FXML
    private void checkout(MouseEvent event) {
        getInitializer().setContent("Checkout.fxml");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void registerSpotlightItems(SpotlightManager manager) {
        manager.registerSpotlight(patronsTile, "Patrons", "View and manage all patrons. \nIt is possible to add, create, and delete patrons.");
        manager.registerSpotlight(typesTile, "Patron Types", "View and manage all patron types. \n" +
                "It is possible to add, create, and delete patrons. " +
                "Patron types can be applied to patrons to " +
                "set book checkout limits and time limits.");
        manager.registerSpotlight(booksTile, "Books", "View and manage all books. \nIt is possible to add, create, and delete books.");
        manager.registerSpotlight(reportsTile, "Reports", "View reports on patrons and current fines.");
        manager.registerSpotlight(checkoutTile, "Checkout & Return", "Manage books and checkout books to patrons.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initializeData() {
        //Set the date in the UI to the formatted date right now when it is initialized
        PreferenceManager preferenceManager = getInitializer().getPreferenceManager();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, u");
        currentDate.setText(formatter.format(LocalDate.now()));
        schoolName.setText(preferenceManager.getValue("school_name", "Robinson High School"));
    }

    @Override
    protected void goHome(Event event) {
        initializeData();
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
                new KeyValue(dateContainer.translateXProperty(), container.getWidth() + 5),
                new KeyValue(header.translateXProperty(), 0),
                new KeyValue(headerBackground.scaleYProperty(), 47.0 / 90.0),
                new KeyValue(headerBackground.translateYProperty(), -27),
                new KeyValue(dateContainer.rotateProperty(), -3)));

        //Run the animation
        animateInTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(700),
                "Animate Properties",
                new KeyValue(container.translateYProperty(), 0, Interpolator.EASE_IN)));

        animateInTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(1200), "Animate Date Container Position",
                new KeyValue(dateContainer.translateXProperty(), 0, Interpolator.EASE_IN)));

        animateInTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(1200), "Animate Date Container Rotation",
                new KeyValue(dateContainer.rotateProperty(), 0, Interpolator.EASE_IN)));

        animateInTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(250), "Animate Header Container Rotation",
                new KeyValue(header.translateXProperty(), 0, Interpolator.DISCRETE),
                new KeyValue(headerBackground.scaleYProperty(), 1),
                new KeyValue(headerBackground.translateYProperty(), 0)));

        //Start the animation
        animateInTimeline.play();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void animateOut(EventHandler<ActionEvent> callback) {
        animateOutTimeline.setCycleCount(1);

        animateOutTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(0),
                new KeyValue(dateContainer.rotateProperty(), 0),
                new KeyValue(dateContainer.translateXProperty(), 0),
                new KeyValue(header.translateYProperty(), 0),
                new KeyValue(container.translateXProperty(), 0),
                new KeyValue(headerBackground.scaleYProperty(), 1),
                new KeyValue(headerBackground.translateYProperty(), 0)
        ));

        animateOutTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(300),
                new KeyValue(dateContainer.rotateProperty(), 5, Interpolator.EASE_IN)));

        animateOutTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(350),
                new KeyValue(dateContainer.translateXProperty(), 500, Interpolator.EASE_IN)));

        animateOutTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(400),
                new KeyValue(header.translateYProperty(), -750, Interpolator.EASE_IN)));

        animateOutTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(500),
                new KeyValue(container.translateXProperty(), container.getWidth() + 5, Interpolator.EASE_IN)));

        //Run the animation
        animateOutTimeline.getKeyFrames().add(new KeyFrame(Duration.millis(550),
                callback,
                new KeyValue(headerBackground.scaleYProperty(), 47.0 / 90.0, Interpolator.EASE_IN),
                new KeyValue(headerBackground.translateYProperty(), -27, Interpolator.EASE_IN)));

        //Start the animation
        animateOutTimeline.play();
    }
}
