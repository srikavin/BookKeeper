package library.fx;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.util.StringConverter;
import library.data.Library;
import library.data.Patron;
import library.data.PatronType;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * This class serves as a controller of the Patrons view defined in the FXML.
 * It handles updating and setting the content relating to the Patron class in the view as well as in the data layer.
 *
 * @author Srikavin Ramkumar
 */
public class Patrons extends BaseController implements Initializable {
    private final PseudoClass errorClass = PseudoClass.getPseudoClass("invalid-input");
    @FXML
    private ChoiceBox<PatronType> patronTypes;
    @FXML
    private TableView<Patron> patronTable;
    @FXML
    private TextField lastName;
    @FXML
    private TextField identifier;
    @FXML
    private TextField firstName;
    @FXML
    private Pane header;
    @FXML
    private Pane headerBackground;
    @FXML
    private Pane contentBackground;
    @FXML
    private Pane container;
    @FXML
    private TextField filter;
    private FilteredList<Patron> filteredList;

    @FXML
    private void updatePatron(ActionEvent event) {
        Patron patron = patronTable.getSelectionModel().getSelectedItem();
        if (validate()) {
            int index = patronTable.getItems().indexOf(patron);
            patron.setPatronType(patronTypes.getSelectionModel().getSelectedItem());
            patron.setFirstName(firstName.getText());
            patron.setLastName(lastName.getText());
            patronTable.getItems().set(index, patron);
        }
    }

    @FXML
    private void goHome(Event event) {
        getInitializer().setContent("MainWindow.fxml");
    }

    private boolean validate() {
        //Set all fields to the valid state to reset error states
        firstName.pseudoClassStateChanged(errorClass, false);
        lastName.pseudoClassStateChanged(errorClass, false);
        patronTypes.pseudoClassStateChanged(errorClass, false);
        identifier.pseudoClassStateChanged(errorClass, false);

        //Get firstname and lastname
        String fName = firstName.getText();
        String lName = lastName.getText();

        boolean success = true;

        //Make sure the first name is not empty
        if (fName.length() == 0) {
            firstName.pseudoClassStateChanged(errorClass, true);
            success = false;
        }
        //Make sure the last name is not empty
        if (lName.length() == 0) {
            lastName.pseudoClassStateChanged(errorClass, true);
            success = false;
        }

        //Make sure the currently selected PatronType is not empty
        SelectionModel<PatronType> patronTypeSelectionModel = patronTypes.getSelectionModel();
        if (patronTypeSelectionModel.isEmpty() || patronTypeSelectionModel.getSelectedItem() == null) {
            patronTypes.pseudoClassStateChanged(errorClass, true);
            success = false;
        }

        return success;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Override id column defined in the fxml
        ObservableList<TableColumn<Patron, ?>> columns = patronTable.getColumns();
        TableColumn<Patron, String> idColumn = new TableColumn<>("Identifier");
        TableColumn<Patron, String> patronTypeColumn = new TableColumn<>("Patron Type");
        idColumn.setCellValueFactory((value) -> new ReadOnlyStringWrapper(value.getValue().getIdentifier().getId()));
        patronTypeColumn.setCellValueFactory((value) -> new ReadOnlyStringWrapper(value.getValue().getPatronType().getName()));
        columns.set(0, idColumn);
        columns.set(3, patronTypeColumn);

        patronTable.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.intValue() == -1) {
                return;
            }
            //Get the patron currently selected
            Patron patron = patronTable.getItems().get(newValue.intValue());
            //Set the lastname
            lastName.setText(patron.getLastName());
            //Set the firstname
            firstName.setText(patron.getFirstName());
            //Set the identifier
            identifier.setText(patron.getIdentifier().toString());

            //Set how to display PatronType objects in the UI
            patronTypes.setConverter(new StringConverter<PatronType>() {
                @Override
                public String toString(PatronType type) {
                    return type.getName();
                }

                @Override
                public PatronType fromString(String typeString) {
                    return getLibrary().getPatronTypeFromName(typeString);
                }
            });

            //Set the available patron types into the select box
            patronTypes.setItems(FXCollections.observableList(getLibrary().getPatronTypes()));
            //Set the currently selected PatronType
            patronTypes.getSelectionModel().select(patron.getPatronType());
        });

        filter.textProperty().addListener(
                (observable, oldValue, newValue) ->
                        filteredList.setPredicate((e) -> {
                                    String lowerCaseValue = newValue.toLowerCase();
                                    return e.getFirstName().toLowerCase().contains(lowerCaseValue) ||
                                            e.getLastName().toLowerCase().contains(lowerCaseValue) ||
                                            e.getIdentifier().getId().toLowerCase().contains(lowerCaseValue);
                                }
                        ));
    }

    @Override
    public void initializeData() {
        Library library = getLibrary();

        //Load data from Library object
        ObservableList<Patron> observableList = FXCollections.observableList(library.getPatrons());
        //Setup filtering
        filteredList = new FilteredList<>(observableList);
        //Setup sorting
        SortedList<Patron> sortedList = new SortedList<>(filteredList);
        sortedList.comparatorProperty().bind(patronTable.comparatorProperty());
        //Set the list to the table
        patronTable.setItems(sortedList);
    }
}
