package library.fx;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import library.data.Identifier;
import library.data.Library;
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
public class PatronTypes extends DataViewController<PatronType> {
    @FXML
    private Spinner<Integer> maxCheckedOutBooks;
    @FXML
    private Spinner<Integer> maxCheckoutDays;
    @FXML
    private TextField name;
    @FXML
    private TextField identifier;

    @Override
    protected Predicate<PatronType> getFilterPredicate(String filter) {
        return e -> e.getName().toLowerCase().contains(filter)
                || e.getIdentifier().getId().toLowerCase().contains(filter);
    }

    @Override
    protected List<PatronType> getDataSource() {
        Library library = getLibrary();
        return library.getPatronTypes();
    }

    protected boolean validate() {
        //Set all fields to the valid state to reset error states
        name.pseudoClassStateChanged(errorClass, false);
        maxCheckoutDays.pseudoClassStateChanged(errorClass, false);
        maxCheckedOutBooks.pseudoClassStateChanged(errorClass, false);
        identifier.pseudoClassStateChanged(errorClass, false);

        //Get Type name
        String typeName = name.getText();

        //Keep track of validation errors
        Set<Node> errors = new HashSet<>();

        //Make sure the first name is not empty
        if (typeName.length() == 0) {
            errors.add(name);
        }
        //Make sure the check out limit is valid
        if (maxCheckedOutBooks.getValue() < 1) {
            errors.add(maxCheckedOutBooks);
        }
        //Make sure the check out time limit is valid
        if (maxCheckoutDays.getValue() < 1) {
            errors.add(maxCheckoutDays);
        }

        for (Node e : errors) {
            e.pseudoClassStateChanged(errorClass, true);
        }

        return errors.isEmpty();
    }

    @Override
    protected void setupColumns(TableView<PatronType> table) {
        //Create Columns
        ObservableList<TableColumn<PatronType, ?>> columns = table.getColumns();
        TableColumn<PatronType, Identifier> idColumn = new TableColumn<>("ID");
        TableColumn<PatronType, String> nameColumn = new TableColumn<>("Name");
        TableColumn<PatronType, Integer> checkoutDaysColumn = new TableColumn<>("Checkout Days");
        TableColumn<PatronType, Integer> checkoutLimitColumn = new TableColumn<>("Checkout Limit");

        //Set how data is populated in each column
        maxCheckedOutBooks.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 1));
        maxCheckoutDays.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE, 1));
        //Force spinners to update their value when the user clicks out of them
        maxCheckoutDays.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                maxCheckoutDays.increment(0);
            }
        });
        maxCheckedOutBooks.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                maxCheckedOutBooks.increment(0);
            }
        });
        idColumn.setCellValueFactory((value) -> new ReadOnlyObjectWrapper<>(value.getValue().getIdentifier()));
        nameColumn.setCellValueFactory((value) -> new ReadOnlyStringWrapper(value.getValue().getName()));
        checkoutDaysColumn.setCellValueFactory((value) -> new ReadOnlyObjectWrapper<>(value.getValue().getMaxCheckoutDays()));
        checkoutLimitColumn.setCellValueFactory((value) -> new ReadOnlyObjectWrapper<>(value.getValue().getMaxCheckedOutBooks()));
        //Set columns
        columns.addAll(idColumn, nameColumn, checkoutDaysColumn, checkoutLimitColumn);

    }

    @Override
    protected void setCurrentState(PatronType patronType) {
        //Set the name
        name.setText(patronType.getName());
        //Set the checkout limit
        maxCheckedOutBooks.getValueFactory().setValue(patronType.getMaxCheckedOutBooks());
        //Set the checkout time limit
        maxCheckoutDays.getValueFactory().setValue(patronType.getMaxCheckoutDays());
        //Set the identifier
        identifier.setText(patronType.getIdentifier().toString());
    }

    @Override
    protected PatronType createNewItem(Identifier identifier) {
        return new PatronType(identifier, "", 1, 1);
    }

    @Override
    protected void update(PatronType type) {
        type.setMaxCheckedOutBooks(maxCheckedOutBooks.getValue());
        type.setMaxCheckoutDays(maxCheckoutDays.getValue());
        type.setName(name.getText());
    }
}
