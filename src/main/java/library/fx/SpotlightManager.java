package library.fx;

import javafx.animation.AnimationTimer;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class SpotlightManager {
    private final List<Spotlight> spotlights = new ArrayList<>();
    private double offsetX;
    private double offsetY;
    private Pane container;
    private Canvas canvas;
    private GraphicsContext context;
    private Node currentTarget;
    private Bounds currentBounds;

    public SpotlightManager(Pane container, double offsetX, double offsetY) {
        this.container = container;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        canvas = new Canvas();
        canvas.heightProperty().bind(container.heightProperty());
        canvas.widthProperty().bind(container.widthProperty());
        canvas.setMouseTransparent(true);
        container.getChildren().add(canvas);
        context = canvas.getGraphicsContext2D();

        AnimationTimer animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                context.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

                if (currentTarget == null || currentBounds == null) {
                    return;
                }

                context.setFill(Color.BLACK.deriveColor(1, 1, 1, 0.39));
                context.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

                context.clearRect(currentBounds.getMinX(),
                        currentBounds.getMinY(),
                        currentBounds.getWidth(),
                        currentBounds.getHeight());
            }
        };
        animationTimer.start();
    }

    public void registerSpotlight(Node node, String title, String description) {
        spotlights.add(new Spotlight(node, title, description));
    }

    /**
     * Trigger the spotlight help UI
     */
    public void trigger(Node target) {
        currentTarget = target;
        currentBounds = target.localToScene(target.getBoundsInLocal());
    }

    private void applyCSS() {

    }

    public double getOffsetY() {
        return offsetY;
    }

    public void setOffsetY(double offsetY) {
        this.offsetY = offsetY;
    }

    public double getOffsetX() {
        return offsetX;
    }

    public void setOffsetX(double offsetX) {
        this.offsetX = offsetX;
    }

    private static class Spotlight {
        public final Node node;
        public final String title;
        public final String description;

        Spotlight(Node node, String title, String description) {
            this.node = node;
            this.title = title;
            this.description = description;
        }
    }
}
