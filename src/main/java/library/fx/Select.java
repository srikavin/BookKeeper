package library.fx;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import library.data.Book;
import library.data.Identifier;
import library.data.LibraryData;
import library.data.Patron;

import java.util.List;
import java.util.function.Consumer;


abstract class AbstractSelect<T extends LibraryData> {
    @FXML
    private TextField filter;

    @FXML
    private Label label;

    @FXML
    private TableView<T> table;

    /**
     * Used to return to the original caller with the identifier of the Patron object selected
     */
    private Consumer<Identifier> callback;
    private Stage stage;

    /**
     * Initialize this object with data and a callback
     *
     * @param callback   Used to return the original caller the identifier of the selected Patron object
     * @param dataSource Data to display in this object
     * @param stage      The stage this view is being displayed in
     */
    public void init(Consumer<Identifier> callback, List<T> dataSource, Stage stage) {
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(getTitle());
        label.setText(getTitle());
        this.callback = callback;
        this.stage = stage;

        initTable(table);

        ObservableList<T> observableList = FXCollections.observableList(dataSource);
        //Setup filtering
        FilteredList<T> filteredList = new FilteredList<>(observableList);
        filter.textProperty().addListener((observable, oldValue, newValue) ->
                filteredList.setPredicate((e) -> filterPredicate(filter.getText().toLowerCase(), e)));
        //Setup sorting
        SortedList<T> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedList);
        //Set the list to the table
        Platform.runLater(() -> table.refresh());
    }

    @FXML
    private void cancel(ActionEvent event) {
        stage.close();
    }


    @FXML
    private void select(ActionEvent event) {
        T selected = table.getSelectionModel().getSelectedItem();
        if (!table.getSelectionModel().isEmpty() && selected != null) {
            callback.accept(selected.getIdentifier());
            stage.close();
        }
    }

    protected abstract void initTable(TableView<T> table);

    protected abstract String getTitle();

    protected abstract boolean filterPredicate(String filterText, T current);
}

/**
 * Class that allows for selecting of a specific Library Data Type
 */
public class Select {
    /**
     * Allows for easy selection of a Patron object from the given dataset
     */
    public static class PatronSelect extends AbstractSelect<Patron> {
        @Override
        protected void initTable(TableView<Patron> table) {
            Patrons.initializeTable(table);
        }

        @Override
        protected String getTitle() {
            return "Select a Patron";
        }

        @Override
        protected boolean filterPredicate(String filterText, Patron current) {
            String lowerCaseValue = filterText.toLowerCase();
            return current.getFirstName().toLowerCase().contains(lowerCaseValue) ||
                    current.getLastName().toLowerCase().contains(lowerCaseValue) ||
                    current.getIdentifier().getId().toLowerCase().contains(lowerCaseValue);
        }
    }

    /**
     * Allows for easy selection of a Book object from the given dataset
     */
    public static class BookSelect extends AbstractSelect<Book> {
        @Override
        protected void initTable(TableView<Book> table) {
            Books.initializeTable(table);
        }

        @Override
        protected String getTitle() {
            return "Select a Book";
        }

        @Override
        protected boolean filterPredicate(String filterText, Book current) {
            return current.getIdentifier().getId().toLowerCase().contains(filterText)
                    || current.getIdentifier().getId().contains(filterText)
                    || current.getIsbn().toLowerCase().contains(filterText)
                    || current.getAuthor().toLowerCase().contains(filterText);
        }
    }
}
