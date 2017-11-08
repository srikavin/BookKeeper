package library.fx;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import library.data.Identifier;
import library.data.Patron;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class Patrons extends BaseController implements Initializable {
    @FXML
    private TableView<Patron> patronTable;
    @FXML
    private TextField lastName;
    @FXML
    private TextField identifier;
    @FXML
    private TextField firstName;
    @FXML
    private DatePicker birthDate;
    @FXML
    private Pane header;
    @FXML
    private Pane headerBackground;
    @FXML
    private Pane contentBackground;
    @FXML
    private Pane container;

    @Override
    public void animateIn(EventHandler<ActionEvent> callback) {
        final Timeline timeline = new Timeline();
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

    @Override
    public void animateOut(EventHandler<ActionEvent> callback) {
        final Timeline timeline = new Timeline();
        timeline.setCycleCount(1);

        //Set starting point of the animation - currently set to off-screen
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(0),
                new KeyValue(container.opacityProperty(), 1, Interpolator.EASE_IN),
                new KeyValue(headerBackground.scaleYProperty(), 1, Interpolator.EASE_IN),
                new KeyValue(header.translateYProperty(), 0, Interpolator.EASE_IN)));

        //Run the animation
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(150),
                new KeyValue(container.opacityProperty(), 0),
                new KeyValue(header.translateYProperty(), -150),
                new KeyValue(headerBackground.scaleYProperty(), 90.0 / 47.0, Interpolator.EASE_IN)
        ));

        //Run the animation
        timeline.getKeyFrames().add(new KeyFrame(Duration.millis(250),
                callback,
                new KeyValue(headerBackground.translateYProperty(), 23, Interpolator.EASE_IN)));

        //Start the animation
        timeline.play();
    }

    @FXML
    private void goHome(ActionEvent event) {
        getInitializer().setContent("MainWindow.fxml");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Override id column defined in the fxml
        ObservableList<TableColumn<Patron, ?>> columns = patronTable.getColumns();
        TableColumn<Patron, String> idColumn = new TableColumn<>("Identifier");
        idColumn.setCellValueFactory((value) -> value.getValue().identifierProperty().get().idProperty());
        columns.set(0, idColumn);

        @SuppressWarnings("unchecked")
        ObservableList<Patron> patrons = patronTable.getItems();
        patrons.add(new Patron(new Identifier(12), "abc", "dfe", LocalDate.now()));
        patrons.add(new Patron(new Identifier(13), "abc", "dfe", LocalDate.now()));
        patrons.add(new Patron(new Identifier(124), "abc123", "dfe", LocalDate.now()));
        patrons.add(new Patron(new Identifier(125), "aba13c", "dfe", LocalDate.now()));
        patrons.add(new Patron(new Identifier(122), "abc", "dfe", LocalDate.now()));
        patrons.add(new Patron(new Identifier(112), "abasdc", "dfe", LocalDate.now()));
        patrons.add(new Patron(new Identifier(152), "abasc", "dfe", LocalDate.now()));
    }
}
