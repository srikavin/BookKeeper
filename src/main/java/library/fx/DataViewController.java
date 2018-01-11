package library.fx;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import library.data.Identifier;
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
    private T currentlyCreating;

    protected abstract Predicate<T> getFilterPredicate(String filterText);

    protected abstract List<T> getDataSource();

    protected abstract boolean validate();

    protected abstract void setupColumns(TableView<T> table);

    protected abstract void setCurrentState(T current);

    protected abstract T createNewItem(Identifier identifier);

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
            //Ensure the user finishes creating the new item
            if (currentlyCreating != null) {
                if (newValue != currentlyCreating) {
                    validate();
                    Platform.runLater(() -> table.getSelectionModel().select(currentlyCreating));
                }
                return;
            }
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
            //If we are creating a new object, we can set it as created because it passes validation
            if (currentlyCreating != null) {
                currentlyCreating = null;
            }
            update(current);
            getLibrary().modify();
            table.refresh();
        }
    }

    protected abstract void update(T toUpdate);

    @FXML
    protected void delete(ActionEvent event) {
        if (currentlyCreating != null) {
            //Set currently creating to a temp variable to set that nothing is being created before deleting the object
            //to prevent visual glitches
            T temp = currentlyCreating;
            currentlyCreating = null;
            dataSource.remove(temp);
        }
        T current = getCurrentlySelected();
        if (current != null) {
            dataSource.remove(current);
            getDataSource().remove(current);
            getLibrary().modify();
        }
    }


    @Override
    protected void goHome(Event event) {
        //Before going home, delete the object being currently created
        dataSource.remove(currentlyCreating);
        currentlyCreating = null;
        super.goHome(event);
    }

    @FXML
    protected void newItem(ActionEvent event) {
        if (currentlyCreating != null) {
            dataSource.remove(currentlyCreating);
            currentlyCreating = null;
        }
        T newItem = createNewItem(getNextIdentifier(dataSource));
        dataSource.add(newItem);
        table.scrollTo(newItem);
        table.getSelectionModel().select(newItem);
        currentlyCreating = newItem;
    }

    private Identifier getNextIdentifier(List<T> list) {
        int cur = list.size() + 1;
        Identifier curId = new Identifier(cur);
        for (T e : list) {
            if (e.getIdentifier().equals(curId)) {
                cur++;
                curId = new Identifier(cur);
            }
        }
        return curId;
    }

}
