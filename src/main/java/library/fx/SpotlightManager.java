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
    private Pane container;
    private Shape curShape;
    private VBox curPane;
    private Button nextButton = new Button("Next");
    private boolean isActive = false;
    private final ChangeListener<? super Transform> changeListener = (observable, oldValue, newValue) -> draw();

    public SpotlightManager(Pane container) {
        this.container = container;
        nextButton.setOnAction(event -> next());
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
    }

    public void disable() {
        removeListenerFromCurrentSpotlight();
        currentSpotlightIndex = 0;
        isActive = false;
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
            container.getChildren().remove(curShape);
            container.getChildren().remove(curPane);
            return;
        }
        //Calculate bounds of object
        Bounds currentBounds = spotlight.node.getBoundsInParent();
        final Point2D pt = container.sceneToLocal(spotlight.node.getParent().localToScene(currentBounds.getMinX(), currentBounds.getMinY()));
        Bounds bounds = new BoundingBox(pt.getX(), pt.getY(), currentBounds.getWidth(), currentBounds.getHeight());

        //Create rectangle for entire container
        Rectangle base = new Rectangle(container.getWidth(), container.getHeight());
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
        container.getChildren().remove(curShape);
        container.getChildren().add(shape);
        //Set the current shape
        this.curShape = shape;

        //Create container for information about the current node
        VBox vBox = new VBox();
        TitledPane pane = new TitledPane();
        pane.setCollapsible(false);
        pane.setText(spotlight.title);
        pane.setPrefHeight(60);
        pane.setPrefWidth(80);
        Text label = new Text();
        label.setText(spotlight.description);
        pane.setContent(label);
        pane.setFocusTraversable(false);

        vBox.getChildren().add(pane);
        vBox.setSpacing(5.0);
        vBox.setAlignment(Pos.CENTER_RIGHT);
        vBox.getChildren().add(nextButton);

        vBox.setLayoutX(bounds.getMaxX() + (bounds.getWidth() / 4));
        vBox.setLayoutY(bounds.getMinY() + (bounds.getHeight() / 2) - pane.getPrefHeight());
        container.getChildren().remove(curPane);
        container.getChildren().add(vBox);
        curPane = vBox;
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
