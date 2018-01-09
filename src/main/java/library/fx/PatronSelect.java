package library.fx;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import library.data.Identifier;
import library.data.Patron;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

/**
 * Class that allows for selecting of a specific patron
 */
public class PatronSelect implements Initializable {
    @FXML
    private TextField filter;
    @FXML
    private TableView<Patron> table;
    /**
     * Used to return to the original caller with the identifier of the Patron object selected
     */
    private Consumer<Identifier> callback;
    private Stage stage;

    /**
     * Initialize this object with data and a callback
     *
     * @param callback   Used to return the original caller the identifier of the selected Patron object
     * @param patronList Data to display in this object
     * @param stage      The stage this view is being displayed in
     */
    public void init(Consumer<Identifier> callback, List<Patron> patronList, Stage stage) {
        stage.initModality(Modality.APPLICATION_MODAL);
        this.callback = callback;
        this.stage = stage;
        ObservableList<Patron> observableList = FXCollections.observableList(patronList);
        //Setup filtering
        FilteredList<Patron> filteredList = new FilteredList<>(observableList);
        //Setup sorting
        filter.textProperty().addListener((observable, oldValue, newValue) ->
                filteredList.setPredicate((e) -> {
                    String lowerCaseValue = newValue.toLowerCase();
                    return e.getFirstName().toLowerCase().contains(lowerCaseValue) ||
                            e.getLastName().toLowerCase().contains(lowerCaseValue) ||
                            e.getIdentifier().getId().toLowerCase().contains(lowerCaseValue);
                }));
        SortedList<Patron> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(table.comparatorProperty());
        //Set the list to the table
        table.setItems(sortedList);
        Platform.runLater(() -> table.refresh());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Patrons.initializeTable(table);
    }

    @FXML
    private void cancel(ActionEvent event) {
        stage.close();
    }

    @FXML
    private void select(ActionEvent event) {
        Patron selected = table.getSelectionModel().getSelectedItem();
        if (!table.getSelectionModel().isEmpty() && selected != null) {
            callback.accept(selected.getIdentifier());
            stage.close();
        }
    }
}
