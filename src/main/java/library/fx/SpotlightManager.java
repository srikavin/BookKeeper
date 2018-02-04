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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.scene.transform.Transform;

import java.util.ArrayList;
import java.util.List;

public class SpotlightManager {
    private final List<Spotlight> spotlights = new ArrayList<>();
    private int currentSpotlightIndex = -1;
    private Pane spotlightContainer;
    private Shape curShape;
    private VBox tooltipContainer;
    private Text descriptionLabel;
    private TitledPane titlePane;
    private Button nextButton;
    private boolean isActive = false;
    private final ChangeListener<? super Transform> changeListener = (observable, oldValue, newValue) -> draw();

    public SpotlightManager(Pane spotlightContainer) {
        this.spotlightContainer = spotlightContainer;
        //Create next button
        nextButton = new Button("Next");
        nextButton.setOnAction(event -> next());
        //Create the text label displaying the description
        descriptionLabel = new Text();
        //Create and customize the pane displaying the title and containing the description and button nodes
        titlePane = new TitledPane();
        titlePane.setCollapsible(false);
        titlePane.setPrefHeight(60);
        titlePane.setPrefWidth(80);
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

    }

    public void registerSpotlight(Node node, String title, String description) {
        spotlights.add(new Spotlight(node, title, description));
    }

    /**
     * Trigger the spotlight help UI
     */
    public void trigger() {
        currentSpotlightIndex = 0;
        addListenerToCurrentSpotlight();
        isActive = true;
        draw();
    }

    public void disable() {
        removeListenerFromCurrentSpotlight();
        currentSpotlightIndex = 0;
        isActive = false;
        draw();
    }

    /**
     * Removes the listener from the current node
     * Should be done before going to the next spotlight in the current scene
     */
    private void removeListenerFromCurrentSpotlight() {
        Spotlight current = spotlights.get(currentSpotlightIndex);
        current.node.localToSceneTransformProperty().removeListener(changeListener);
    }

    /**
     * Adds the listener to the current node
     * Allows the draw function to only be called when needed
     */
    private void addListenerToCurrentSpotlight() {
        Spotlight current = spotlights.get(currentSpotlightIndex);
        current.node.localToSceneTransformProperty().addListener(changeListener);
    }

    /**
     * Goes to the next spotlight and updates the listeners
     * Updating the listeners improves performance as the overlay will only be drawn when the scene changes
     */
    public void next() {
        removeListenerFromCurrentSpotlight();
        if (spotlights.size() - 1 > currentSpotlightIndex) {
            currentSpotlightIndex++;
            addListenerToCurrentSpotlight();
            draw();
        } else {
            disable();
            draw();
        }
    }

    private void draw() {
        draw(spotlights.get(currentSpotlightIndex));
    }

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
        shape.setMouseTransparent(true);
        //Set the color and opacity
        shape.setFill(Color.BLACK);
        shape.setOpacity(0.7);
        //Remove the shape if it exists and add it to the container
        spotlightContainer.getChildren().remove(curShape);
        spotlightContainer.getChildren().add(shape);
        //Set the current shape
        this.curShape = shape;

        //Create container for information about the current node
        titlePane.setText(spotlight.title);
        tooltipContainer.setMaxWidth(titlePane.getWidth());
        descriptionLabel.setText(spotlight.description);

        double layoutX = bounds.getMaxX() + (titlePane.getWidth() / 5);
        //Put the tooltip on the left if the tooltip is too big to fit on the right
        if (layoutX > spotlightContainer.getWidth()) {
            layoutX = bounds.getMinX() - (titlePane.getWidth() * 1.2);
        }
        //Make sure the tooltip stays within the range of the window on the left
        if (layoutX < 5) {
            layoutX = 5;
        }

        tooltipContainer.setLayoutX(layoutX);
        tooltipContainer.setLayoutY(bounds.getMinY() + (bounds.getHeight() / 2) - titlePane.getPrefHeight());

        tooltipContainer.setVisible(true);
        //Set tooltip container to front of overlay
        spotlightContainer.getChildren().remove(tooltipContainer);
        spotlightContainer.getChildren().add(tooltipContainer);
    }

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
