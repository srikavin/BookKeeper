package library.fx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import library.data.LibraryData;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;

/**
 * This is an abstract class for controllers with {@link TableView}s in it.
 *
 * @param <T> The data type represented by this controller
 */
public abstract class DataViewController<T extends LibraryData> extends BaseController implements Initializable {
    @FXML
    protected TableView<T> table;
    @FXML
    protected ObservableList<T> dataSource;
    @FXML
    protected FilteredList<T> filteredList;
    @FXML
    protected SortedList<T> sortedList;
    @FXML
    protected TextField filter;

    protected abstract Predicate<T> getFilterPredicate(String filterText);

    protected abstract List<T> getDataSource();

    protected abstract boolean validate();

    protected abstract void setupColumns(TableView<T> table);

    protected abstract void setCurrentState(T current);

    @Override
    public void initializeData() {
        dataSource = FXCollections.observableList(getDataSource());
        filteredList = new FilteredList<>(dataSource);
        sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sortedList);

        filter.textProperty().addListener((observable, oldValue, newValue) ->
                filteredList.setPredicate(getFilterPredicate(newValue)));
    }

    protected T getCurrentlySelected() {
        return table.getSelectionModel().getSelectedItem();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupColumns(table);

        table.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            //Ensure that a change actually occurred in the selection
            if (newValue != null && oldValue != newValue) {
                setCurrentState(newValue);
            }
        });
    }

    @FXML
    protected void update(ActionEvent event) {
        T current = getCurrentlySelected();
        //Make sure something is selected
        if (current == null) {
            return;
        }
        //Make sure the entered data is valid
        if (validate()) {
            update(current);
            getLibrary().modify();
            table.refresh();
        }
    }

    protected abstract void update(T toUpdate);

    @FXML
    protected void delete(ActionEvent event) {
        T current = getCurrentlySelected();
        if (current != null) {
            dataSource.remove(current);
            getDataSource().remove(current);
        }
    }

}
