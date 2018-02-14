package library.fx;

import javafx.beans.value.ChangeListener;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.scene.transform.Transform;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates "spotlights", interactive help menus.
 * Allows for registering of multiple spotlights.
 * When {@link #trigger()} is called, all spotlights are displayed, in the order they were registered in.
 * {@link #disable()} should be called before disposing of this object
 *
 * @author Srikavin Ramkumar
 */
public class SpotlightManager {
    private final List<Spotlight> spotlights = new ArrayList<>();
    private int currentSpotlightIndex = -1;
    private Pane spotlightContainer;
    private Shape curShape;
    private VBox tooltipContainer;
    private Text descriptionLabel = new Text();
    private TitledPane titlePane = new TitledPane();
    private boolean isActive = false;
    private final ChangeListener<? super Transform> changeListener = (observable, oldValue, newValue) -> draw();

    /**
     * Creates a spotlight manager that creates "spotlight" interactive help menus.
     *
     * @param spotlightContainer The container the backdrop should be limited to
     */
    public SpotlightManager(Pane spotlightContainer) {
        this.spotlightContainer = spotlightContainer;
        //Create next button
        Button nextButton = new Button("Next");
        nextButton.setOnAction(event -> next());
        //Create and customize the pane displaying the title and containing the description and button nodes
        titlePane.setCollapsible(false);
        titlePane.setPrefHeight(Region.USE_COMPUTED_SIZE);
        titlePane.setPrefWidth(Region.USE_COMPUTED_SIZE);
        titlePane.setContent(descriptionLabel);
        titlePane.setFocusTraversable(false);
        //Create the container of the tooltip and add the children to it
        tooltipContainer = new VBox();
        tooltipContainer.setSpacing(5.0);
        tooltipContainer.setAlignment(Pos.CENTER_RIGHT);
        tooltipContainer.getChildren().add(titlePane);
        tooltipContainer.getChildren().add(nextButton);

        tooltipContainer.setVisible(false);

        spotlightContainer.getChildren().add(tooltipContainer);

        //Force redraws when the spotlight container is resized in order to keep the overlay over the entire bounding box
        spotlightContainer.heightProperty().addListener((observable, oldValue, newValue) -> draw());
        spotlightContainer.widthProperty().addListener((observable, oldValue, newValue) -> draw());
    }

    /**
     * Registers a spotlight with this manager. Spotlights will be shown in the order of registration when
     * {@link #trigger()} is called.
     *
     * @param node        The node to highlight when this spotlight is displayed
     * @param title       The title to show on the title pane of this spotlight
     * @param description The description of the node highlighted and any important, helpful information
     */
    public void registerSpotlight(Node node, String title, String description) {
        spotlights.add(new Spotlight(node, title, description));
    }

    /**
     * Trigger the spotlight help UI
     * All of the registered spotlights will be shown in the order of registration.
     */
    public void trigger() {
        //Ignore if no spotlights are registered
        if (spotlights.isEmpty()) {
            return;
        }
        currentSpotlightIndex = 0;
        addListenerToCurrentSpotlight();
        isActive = true;
        draw();
    }

    /**
     * Disables the spotlight by immediately removing the overlay and resetting to the default state.
     * Registered spotlights will be removed and cleared
     */
    public void disable() {
        if (currentSpotlightIndex != -1) {
            removeListenerFromCurrentSpotlight();
        }
        currentSpotlightIndex = 0;
        isActive = false;
        draw();
        spotlights.clear();
    }

    /**
     * Removes the listener from the current node
     * Should be done before going to the next spotlight in the current scene
     */
    private void removeListenerFromCurrentSpotlight() {
        Spotlight current = spotlights.get(currentSpotlightIndex);
        if (current.node != null) {
            current.node.localToSceneTransformProperty().removeListener(changeListener);
        }
    }

    /**
     * Adds the listener to the current node
     * Allows the draw function to only be called when needed
     */
    private void addListenerToCurrentSpotlight() {
        Spotlight current = spotlights.get(currentSpotlightIndex);
        if (current.node != null) {
            current.node.localToSceneTransformProperty().addListener(changeListener);
        }
    }

    /**
     * Goes to the next spotlight and updates the listeners
     * Updating the listeners improves performance as the overlay will only be drawn when the scene changes
     */
    public void next() {
        removeListenerFromCurrentSpotlight();
        if (spotlights.size() - 1 > currentSpotlightIndex) {
            //Increment the spotlight index
            currentSpotlightIndex++;
            addListenerToCurrentSpotlight();
        } else {
            //Set the current status to inactive
            currentSpotlightIndex = 0;
            isActive = false;
        }
        //Draw the current state to the screen
        draw();
    }

    /**
     * Calls {@link #draw(Spotlight)} with the currently active spotlight
     */
    private void draw() {
        if (!spotlights.isEmpty() && currentSpotlightIndex != -1) {
            draw(spotlights.get(currentSpotlightIndex));
        }
    }

    /**
     * Draws the current state to the spotlight container. If {@link #isActive} is false, it will clear the screen of the
     * spotlight overly. If it is true, it will draw the overlay with the information given in the specified spotlight.
     *
     * @param spotlight The spotlight information to be used when drawing the overlay.
     */
    private void draw(Spotlight spotlight) {
        if (!isActive) {
            tooltipContainer.setVisible(false);
            spotlightContainer.getChildren().remove(curShape);
            return;
        }

        //Calculate bounds of object
        Bounds currentBounds = spotlight.node.getBoundsInParent();
        final Point2D pt = spotlightContainer.sceneToLocal(spotlight.node.getParent().localToScene(currentBounds.getMinX(), currentBounds.getMinY()));
        Bounds bounds = new BoundingBox(pt.getX(), pt.getY(), currentBounds.getWidth(), currentBounds.getHeight());

        //Create rectangle for entire container
        Rectangle base = new Rectangle(spotlightContainer.getWidth(), spotlightContainer.getHeight());
        //Create rectangle and set properties to only cover the currentTarget
        Rectangle rect = new Rectangle();
        rect.setLayoutX(bounds.getMinX());
        rect.setLayoutY(bounds.getMinY());
        rect.setWidth(bounds.getMaxX() - bounds.getMinX());
        rect.setHeight(bounds.getMaxY() - bounds.getMinY());
        //Get the difference between the two shapes; this is everything but the current target
        Shape shape = Shape.subtract(base, rect);
        //Make the shape ignore clicks
        shape.setMouseTransparent(false);
        //Set the color and opacity
        shape.setFill(Color.BLACK);
        shape.setOpacity(0.7);
        //Remove the shape if it exists and add the newly calculated shape to the container
        spotlightContainer.getChildren().remove(curShape);
        spotlightContainer.getChildren().add(shape);
        //Set the current shape
        this.curShape = shape;

        curShape.setOnMouseClicked((e) -> this.next());

        //Create container for information about the current node
        titlePane.setText(spotlight.title);
//        tooltipContainer.setMaxWidth(titlePane.getWidth());
        descriptionLabel.setText(spotlight.description);
        descriptionLabel.setWrappingWidth(250);

        // Used for calculating the position to place the tooltip container
        final double CONTAINER_MARGIN = 16;

        tooltipContainer.setVisible(true);
        spotlightContainer.layout();

        double layoutX = bounds.getMaxX() + CONTAINER_MARGIN;
        double layoutY = bounds.getMinY();

        //Put the tooltip on the left if the tooltip is too big to fit on the right
        if (layoutX + tooltipContainer.getWidth() > spotlightContainer.getWidth()) {
            layoutX = bounds.getMinX() - (CONTAINER_MARGIN + tooltipContainer.getWidth());
            if (layoutX < 5 || layoutX > spotlightContainer.getWidth() - tooltipContainer.getWidth()) {
                //Fallbacks in case the layout cannot be calculated properly
                layoutX = CONTAINER_MARGIN;
                layoutY = spotlightContainer.getHeight() - (tooltipContainer.getHeight() + 5);
            }
        }

        if (layoutY + tooltipContainer.getHeight() > spotlightContainer.getHeight() + 5) {
            layoutY = spotlightContainer.getHeight() - (tooltipContainer.getHeight() + 5);
        }

        //Set the layout to the layout calculated above
        tooltipContainer.setLayoutX(layoutX);
        tooltipContainer.setLayoutY(layoutY);

        //Sets the title pane and spotlight information to be in front of the backdrop
        spotlightContainer.getChildren().remove(tooltipContainer);
        spotlightContainer.getChildren().add(tooltipContainer);
    }

    /**
     * Used to store the spotlights registered with the spotlight manager
     */
    private static class Spotlight {
        final Node node;
        final String title;
        final String description;

        Spotlight(Node node, String title, String description) {
            this.node = node;
            this.title = title;
            this.description = description;
        }
    }
}
