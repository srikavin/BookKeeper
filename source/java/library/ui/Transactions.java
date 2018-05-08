package library.ui;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import library.data.*;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

/**
 * The UI View for {@link Transaction}s. Implements {@link DataViewController}. Holds a {@link Patron} and a {@link Book}
 * instance to update. Uses event-driven operations to maintain state abd update the model layer.
 */
public class Transactions extends DataViewController<Transaction> {
    /**
     * The DateTimeFormatter used to format the {@link java.time.Instant}'s stored in Transactions
     */
    private static final DateTimeFormatter formatter =
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                    .withLocale(Locale.US)
                    .withZone(ZoneId.systemDefault());
    /**
     * The TextField that stores the identifier of the Transaction
     */
    @FXML
    private TextField identifier;
    /**
     * The TextField that stores the string representation of the stored timestamp in the current Transaction
     */
    @FXML
    private TextField timestamp;
    /**
     * The TextField that stores the identifier of the {@link Patron} stored in the current Transaction
     */
    @FXML
    private TextField patronIdentifier;
    /**
     * The TextField that stores the name of the {@link Patron} stored in the current Transaction
     */
    @FXML
    private TextField patronName;
    /**
     * The TextField that stores the identifier of the {@link Book} stored in the current Transaction
     */
    @FXML
    private TextField bookIdentifier;
    /**
     * The TextField that stores the title of the {@link Book} stored in the current Transaction
     */
    @FXML
    private TextField bookTitle;
    /**
     * The TextField that stores the author of the {@link Book} stored in the current Transaction
     */
    @FXML
    private TextField bookAuthor;
    /**
     * The ChoiceBox (selection menu) that stores the {@link BookStatus} of the stored Book in  the current Transaction
     */
    @FXML
    private ChoiceBox<BookStatus> bookStatus;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void registerSpotlightFields(SpotlightManager manager) {
        manager.registerSpotlight(patronIdentifier, "Patron Identifier", "The patron to show books for. " +
                "Must be a valid Patron Identifier. Valid values can be selected by clicking the search icon.");
        manager.registerSpotlight(patronName, "Current Patron Name", "The name of the current patron. " +
                "This field can not modifiable. To modify this information, go the Patrons view.");
        manager.registerSpotlight(bookIdentifier, "Book Identifier", "The book to checkout or return. " +
                "Must be a valid Book Identifier. Valid values can be selected by clicking the search icon.");
        manager.registerSpotlight(bookTitle, "Current Book Title", "The title of the current book. " +
                "This field can not be modified. To edit this information, go to the the Books view.");
        manager.registerSpotlight(bookAuthor, "Current Book Author", "The author of the current book. " +
                "This field can not be modified. To edit this information, go to the the Books view.");
        manager.registerSpotlight(bookStatus, "Current Book Status", "The status of the current book. " +
                "If it is checked out or lost, a librarian override is required. This field will automatically be set to " +
                "Checked Out if the book is checked out.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Predicate<Transaction> getFilterPredicate(String filter) {
        return transaction -> transaction.getIdentifier().getId().toLowerCase().contains(filter)
                || transaction.getChangedBook().getTitle().toLowerCase().contains(filter)
                || transaction.getChangedBook().getIsbn().toLowerCase().contains(filter)
                || transaction.getChangedBook().getAuthor().toLowerCase().contains(filter)
                || transaction.getChangedPatron().getLastName().toLowerCase().contains(filter)
                || transaction.getChangedPatron().getFirstName().toLowerCase().contains(filter)
                || transaction.getChangedPatron().getPatronType().getName().toLowerCase().contains(filter)
                || transaction.getTimestamp().toString().toLowerCase().contains(filter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected List<Transaction> getDataSource() {
        return FXCollections.observableList(getLibrary().getTransactions());
    }

    /**
     * {@inheritDoc}
     *
     * Always returns false, as transactions cannot be created manually
     */
    @Override
    protected boolean validate() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setupColumns(TableView<Transaction> table) {
        //Get the list of columns on the table
        ObservableList<TableColumn<Transaction, ?>> columns = table.getColumns();

        //Create columns with appropriate titles
        TableColumn<Transaction, Identifier> idColumn = new TableColumn<>("ID");
        TableColumn<Transaction, String> nameColumn = new TableColumn<>("Patron Name");
        TableColumn<Transaction, String> bookNameColumn = new TableColumn<>("Book Title");
        TableColumn<Transaction, Transaction.Action> actionColumn = new TableColumn<>("Action");
        TableColumn<Transaction, BookStatus> bookStatusColumn = new TableColumn<>("Prior Book Status");
        TableColumn<Transaction, String> timestampColumn = new TableColumn<>("Timestamp");

        //Set the value generation scheme for each column
        idColumn.setCellValueFactory((value) -> new ReadOnlyObjectWrapper<>(value.getValue().getIdentifier()));
        nameColumn.setCellValueFactory((value) -> Bindings.concat(value.getValue().getChangedPatron().getFirstName(), " ", value.getValue().getChangedPatron().getLastName()));
        bookNameColumn.setCellValueFactory((value) -> new ReadOnlyObjectWrapper<>(value.getValue().getChangedBook().getTitle()));
        actionColumn.setCellValueFactory((value) -> new ReadOnlyObjectWrapper<>(value.getValue().getAction()));
        bookStatusColumn.setCellValueFactory((value) -> new ReadOnlyObjectWrapper<>(value.getValue().getChangedBook().getStatus()));
        timestampColumn.setCellValueFactory((value) -> new ReadOnlyStringWrapper(formatter.format(value.getValue().getTimestamp())));

        //Add columns to the table
        columns.addAll(idColumn, nameColumn, bookNameColumn, actionColumn, bookStatusColumn, timestampColumn);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setCurrentState(Transaction current) {
        Book changedBook = current.getChangedBook();
        Patron changedPatron = current.getChangedPatron();

        identifier.setText(current.getIdentifier().getId());
        patronIdentifier.setText(changedPatron.getIdentifier().getId());
        bookIdentifier.setText(changedBook.getIdentifier().getId());
        bookTitle.setText(changedBook.getTitle());
        bookAuthor.setText(changedBook.getAuthor());
        bookStatus.setValue(changedBook.getStatus());
        patronName.setText(changedPatron.getFirstName() + " " + changedPatron.getLastName());
        timestamp.setText(formatter.format(current.getTimestamp()));
    }

    /**
     * {@inheritDoc}
     * Should not be called, as new transactions cannot be manually created
     */
    @Override
    protected Transaction createNewItem(Identifier identifier) {
        return null;
    }

    /**
     * {@inheritDoc}
     * Should not be called, as new transactions cannot be manually changed
     */
    @Override
    protected void update(Transaction toUpdate) {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void initializeData() {
        super.initializeData();
        table.setPlaceholder(new Text("No transactions exist!"));
        bookStatus.setItems(FXCollections.observableArrayList(BookStatus.values()));
        bookStatus.setConverter(new StringConverter<BookStatus>() {
            @Override
            public String toString(BookStatus object) {
                return object.name();
            }

            @Override
            public BookStatus fromString(String string) {
                return BookStatus.valueOf(string);
            }
        });
    }
}
