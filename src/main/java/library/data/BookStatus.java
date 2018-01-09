package library.data;

/**
 * This enum represents the possible states of a library book.
 *
 * @author Srikavin Ramkumar
 */
public enum BookStatus {
    AVAILABLE("Available"),
    CHECKED_OUT("Checked out"),
    LOST("Lost");

    private final String message;

    BookStatus(String message) {
        this.message = message;
    }

    /**
     * Get a friendly message of this enum value
     *
     * @return A user-friendly message to display regarding this enum value
     */
    @Override
    public String toString() {
        return message;
    }
}
