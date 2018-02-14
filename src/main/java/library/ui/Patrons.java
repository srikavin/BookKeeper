package library.ui;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import library.data.Identifier;
import library.data.Library;
import library.data.Patron;
import library.data.PatronType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

/**
 * This class serves as a controller of the Patrons view defined in the FXML.
 * It handles updating and setting the content relating to the Patron class in the view as well as in the data layer.
 *
 * @author Srikavin Ramkumar
 */
public class Patrons extends DataViewController<Patron> {
    @FXML
    private ChoiceBox<PatronType> patronTypes;
    @FXML
    private TextField lastName;
    @FXML
    private TextField identifier;
    @FXML
    private TextField firstName;

    public static void initializeTable(TableView<Patron> tableView) {
        //Create all columns
        ObservableList<TableColumn<Patron, ?>> columns = tableView.getColumns();

        TableColumn<Patron, String> idColumn = new TableColumn<>("Identifier");
        TableColumn<Patron, String> firstNameColumn = new TableColumn<>("First Name");
        TableColumn<Patron, String> lastNameColumn = new TableColumn<>("Last Name");
        TableColumn<Patron, String> patronTypeColumn = new TableColumn<>("Patron Type");

        idColumn.setCellValueFactory((value) -> new ReadOnlyStringWrapper(value.getValue().getIdentifier().getId()));
        firstNameColumn.setCellValueFactory((value) -> new ReadOnlyStringWrapper(value.getValue().getFirstName()));
        lastNameColumn.setCellValueFactory((value) -> new ReadOnlyStringWrapper(value.getValue().getLastName()));
        patronTypeColumn.setCellValueFactory((value) -> new ReadOnlyStringWrapper(value.getValue().getPatronType().getName()));

        //Add columns to the table
        columns.setAll(idColumn, firstNameColumn, lastNameColumn, patronTypeColumn);
    }

    protected boolean validate() {
        //Set all fields to the valid state to reset error states
        firstName.pseudoClassStateChanged(errorClass, false);
        lastName.pseudoClassStateChanged(errorClass, false);
        patronTypes.pseudoClassStateChanged(errorClass, false);
        identifier.pseudoClassStateChanged(errorClass, false);

        //Get firstname and lastname
        String fName = firstName.getText();
        String lName = lastName.getText();

        //Keep track of the values
        Set<Node> errors = new HashSet<>();

        //Make sure the first name is not empty
        if (fName.isEmpty()) {
            errors.add(firstName);
        }
        //Make sure the last name is not empty
        if (lName.isEmpty()) {
            errors.add(lastName);
        }

        //Make sure the currently selected PatronType is not empty
        if (patronTypes.getSelectionModel().isEmpty()) {
            errors.add(patronTypes);
        }

        //Iterate through all errors and add a red outline to them
        for (Node e : errors) {
            e.pseudoClassStateChanged(errorClass, true);
        }

        //Return true if errors is empty and return false if errors have occurred
        return errors.isEmpty();
    }

    @Override
    protected void setupColumns(TableView table) {
        //Initialize the table with default columns
        initializeTable(this.table);
    }

    @Override
    protected void setCurrentState(Patron patron) {
        //Set the lastname
        lastName.setText(patron.getLastName());
        //Set the firstname
        firstName.setText(patron.getFirstName());
        //Set the identifier
        identifier.setText(patron.getIdentifier().toString());
        //Set the currently selected PatronType
        patronTypes.getSelectionModel().select(patron.getPatronType());
    }

    @Override
    protected Patron createNewItem(Identifier identifier) {
        List<PatronType> patronTypes = getLibrary().getPatronTypes();
        PatronType patronType = null;
        if (patronTypes.size() > 0) {
            patronType = getLibrary().getPatronTypes().get(0);
        }

        if (patronType == null) {
            patronType = new PatronType(new Identifier(""), "", 0, 0);
        }

        return new Patron(identifier, "", "", patronType);
    }

    @Override
    protected void registerSpotlightFields(SpotlightManager manager) {

    }

    @Override
    protected Predicate<Patron> getFilterPredicate(String filter) {
        return (e) -> {
            String lowerCaseValue = filter.toLowerCase();
            return e.getFirstName().toLowerCase().contains(lowerCaseValue) ||
                    e.getLastName().toLowerCase().contains(lowerCaseValue) ||
                    e.getIdentifier().getId().toLowerCase().contains(lowerCaseValue);
        };
    }

    @Override
    protected List<Patron> getDataSource() {
        Library library = getLibrary();
        return library.getPatrons();
    }

    @Override
    public void initializeData() {
        super.initializeData();
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
        //Update the table after everything has been added to it
        Platform.runLater(() -> table.refresh());
    }

    @Override
    protected void update(Patron patron) {
        patron.setFirstName(firstName.getText());
        patron.setLastName(lastName.getText());
        patron.setPatronType(patronTypes.getSelectionModel().getSelectedItem());
    }
}
