package library.data;

public class PatronType implements LibraryData {
    private Identifier identifier;
    private String name;
    private int maxCheckoutDays;
    private int maxCheckedOutBooks;

    public PatronType(Identifier identifier, String name, int maxCheckoutDays, int maxCheckedOutBooks) {
        this.identifier = identifier;
        this.name = name;
        this.maxCheckoutDays = maxCheckoutDays;
        this.maxCheckedOutBooks = maxCheckedOutBooks;
    }

    public PatronType(String[] data) {
        this(new Identifier(data[0]),
                data[1],
                Integer.parseInt(data[2]),
                Integer.parseInt(data[3]));
        if (data.length != 4) {
            throw new IllegalArgumentException("Invalid data used!");
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxCheckoutDays() {
        return maxCheckoutDays;
    }

    public void setMaxCheckoutDays(int maxCheckoutDays) {
        this.maxCheckoutDays = maxCheckoutDays;
    }

    public int getMaxCheckedOutBooks() {
        return maxCheckedOutBooks;
    }

    public void setMaxCheckedOutBooks(int maxCheckedOutBooks) {
        this.maxCheckedOutBooks = maxCheckedOutBooks;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identifier getIdentifier() {
        return identifier;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] asData() {
        return new String[]{
                identifier.toString(),
                name,
                Integer.toString(maxCheckoutDays),
                Integer.toString(maxCheckedOutBooks)
        };
    }
}
