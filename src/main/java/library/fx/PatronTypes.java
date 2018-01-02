package library.fx;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import library.data.Identifier;
import library.data.Library;
import library.data.PatronType;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * This class serves as a controller of the Patrons view defined in the FXML.
 * It handles updating and setting the content relating to the Patron class in the view as well as in the data layer.
 *
 * @author Srikavin Ramkumar
 */
public class PatronTypes extends BaseController implements Initializable {
    private final PseudoClass errorClass = PseudoClass.getPseudoClass("invalid-input");
    @FXML
    private Spinner<Integer> maxCheckedOutBooks;
    @FXML
    private Spinner<Integer> maxCheckoutDays;
    @FXML
    private TableView<PatronType> patronTypeTable;
    @FXML
    private TextField name;
    @FXML
    private TextField identifier;

    @FXML
    private void updatePatronType(ActionEvent event) {
        int index = Integer.parseInt(identifier.getText());
        PatronType type = patronTypeTable.getItems().get(index);
        if (validate()) {
            type.setMaxCheckedOutBooks(maxCheckedOutBooks.getValue());
            type.setMaxCheckoutDays(maxCheckoutDays.getValue());
            type.setName(name.getText());
            patronTypeTable.getItems().set(index, type);
        }
    }

    @FXML
    private void goHome(ActionEvent event) {
        getInitializer().setContent("MainWindow.fxml");
    }

    private boolean validate() {
        //Set all fields to the valid state to reset error states
        name.pseudoClassStateChanged(errorClass, false);
        maxCheckoutDays.pseudoClassStateChanged(errorClass, false);
        maxCheckedOutBooks.pseudoClassStateChanged(errorClass, false);
        identifier.pseudoClassStateChanged(errorClass, false);

        //Get Type name
        String typeName = name.getText();

        boolean success = true;

        //Make sure the first name is not empty
        if (typeName.length() == 0) {
            name.pseudoClassStateChanged(errorClass, true);
            success = false;
        }
        //Make sure the check out limit is valid
        if (maxCheckedOutBooks.getValue() < 1) {
            maxCheckedOutBooks.pseudoClassStateChanged(errorClass, true);
            success = false;
        }
        //Make sure the check out time limit is valid
        if (maxCheckedOutBooks.getValue() < 1) {
            maxCheckedOutBooks.pseudoClassStateChanged(errorClass, true);
            success = false;
        }

        //Make sure the currently selected PatronType is not empty
        SelectionModel<PatronType> patronTypeSelectionModel = patronTypeTable.getSelectionModel();
        if (patronTypeSelectionModel.isEmpty() || patronTypeSelectionModel.getSelectedItem() == null) {
            patronTypeTable.pseudoClassStateChanged(errorClass, true);
            success = false;
        }

        return success;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Override id column defined in the fxml
        ObservableList<TableColumn<PatronType, ?>> columns = patronTypeTable.getColumns();
        TableColumn<PatronType, Identifier> idColumn = new TableColumn<>("ID");
        TableColumn<PatronType, String> nameColumn = new TableColumn<>("Name");
        TableColumn<PatronType, Integer> checkoutDaysColumn = new TableColumn<>("Checkout Days");
        TableColumn<PatronType, Integer> checkoutLimitColumn = new TableColumn<>("Checkout Limit");

        maxCheckedOutBooks.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 1));
        maxCheckoutDays.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 1));

        idColumn.setCellValueFactory((value) -> new ReadOnlyObjectWrapper<>(value.getValue().getIdentifier()));
        nameColumn.setCellValueFactory((value) -> new ReadOnlyStringWrapper(value.getValue().getName()));
        checkoutDaysColumn.setCellValueFactory((value) -> new ReadOnlyObjectWrapper<>(value.getValue().getMaxCheckoutDays()));
        checkoutLimitColumn.setCellValueFactory((value) -> new ReadOnlyObjectWrapper<>(value.getValue().getMaxCheckedOutBooks()));
        columns.addAll(idColumn, nameColumn, checkoutDaysColumn, checkoutLimitColumn);

        patronTypeTable.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() == -1) {
                return;
            }
            //Get the PatronType currently selected
            PatronType patronType = patronTypeTable.getItems().get(newValue.intValue());
            //Set the name
            name.setText(patronType.getName());
            //Set the checkout limit
            maxCheckedOutBooks.getValueFactory().setValue(patronType.getMaxCheckedOutBooks());
            //Set the checkout time limit
            maxCheckoutDays.getValueFactory().setValue(patronType.getMaxCheckoutDays());
            //Set the identifier
            identifier.setText(patronType.getIdentifier().toString());
        });
    }

    @Override
    public void initializeData() {
        Library library = getLibrary();
        patronTypeTable.setItems(FXCollections.observableList(library.getPatronTypes()));
    }
}
