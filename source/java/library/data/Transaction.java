package library.data;

import java.time.Instant;

/**
 * Used to store all transactions that take place in a library instance.
 * Stores the affected patron, affected book, the action that took place, and a timestamp of when it occurred.
 * This object is identified by an {@link Identifier}.
 */
public class Transaction implements LibraryData {
    private final Identifier identifier;
    private final Patron changedPatron;
    private final Book changedBook;
    private final Action action;
    private final Instant timestamp;

    /**
     * Used to create an instance of a Transaction object. Should be used for creating one for testing, or from user input
     * Use {@link #Transaction(String[], Library)} for restoring from a saved instance of a Transaction
     *
     * @param identifier    The identifier of this transaction
     * @param changedPatron The {@link Patron} affected by this transaction
     * @param changedBook   The {@link Book} affected by this transaction
     * @param action        The {@link Action} that took place in this transaction
     * @param timestamp     The {@link Instant} that this transaction took place
     */
    public Transaction(Identifier identifier, Patron changedPatron, Book changedBook, Action action, Instant timestamp) {
        this.identifier = identifier;
        this.changedPatron = changedPatron;
        this.changedBook = changedBook;
        this.action = action;
        this.timestamp = timestamp;
    }

    /**
     * Creates a transaction instance from the given data
     *
     * @param data
     * @param library
     */
    public Transaction(String[] data, Library library) {
        if (data.length != 5) {
            throw new RuntimeException("Invalid data array passed to create a Transaction object.");
        }
        identifier = new Identifier(data[0]);
        changedPatron = library.getPatronFromID(new Identifier(data[1]));
        changedBook = library.getBookFromID(new Identifier(data[2]));
        action = Action.valueOf(data[3]);
        timestamp = Instant.parse(data[4]);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] asData() {
        return new String[]{identifier.getId(),
                changedPatron.getIdentifier().getId(),
                changedBook.getIdentifier().getId(),
                action.name(),
                timestamp.toString()};
    }

    public Action getAction() {
        return action;
    }

    public Book getChangedBook() {
        return changedBook;
    }

    public Patron getChangedPatron() {
        return changedPatron;
    }

    @Override
    public Identifier getIdentifier() {
        return identifier;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    /**
     * An enum containing all possible actions that a transaction can do.
     * This includes checking out and returning books.
     */
    public enum Action {
        /**
         * Indicates that a book was checked out
         */
        CHECKOUT,
        /**
         * Indicates that a book was returned
         */
        RETURN
    }
}