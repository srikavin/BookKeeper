package library.data;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;

/**
 * A simple class used to hold the data of a single library patron.
 *
 * @author Srikavin Ramkumar
 */
public class Patron {
    /**
     * An observable container to hold this {@linkplain Patron}'s {@link Identifier}.
     */
    private SimpleObjectProperty<Identifier> identifier = new SimpleObjectProperty<>();
    /**
     * An observable container to hold this {@linkplain Patron}'s first name.
     */
    private SimpleStringProperty firstName = new SimpleStringProperty();
    /**
     * An observable container to hold this {@linkplain Patron}'s first name.
     */
    private SimpleStringProperty lastName = new SimpleStringProperty();
    /**
     * An observable container to hold this {@linkplain Patron}'s birthday.
     */
    private SimpleObjectProperty<LocalDate> birthday = new SimpleObjectProperty<>();

    /**
     * Creates a {@linkplain Patron} object using the specified parameters.
     *
     * @param identifier A {@link Identifier} object containing the desired identifier for this patron.
     * @param firstName  A {@linkplain String} object containing the desired first name of this patron.
     * @param lastName   A {@linkplain String} object containing the desired last name of this patron.
     * @param birthday   A {@linkplain LocalDate} object containing the desired birthday of this patron.
     */
    public Patron(Identifier identifier, String firstName, String lastName, LocalDate birthday) {
        this.identifier.set(identifier);
        this.firstName.set(firstName);
        this.lastName.set(lastName);
        this.birthday.set(birthday);
    }

    /**
     * Creates a {@linkplain Patron} object from a String Array, arr, in the format:
     * <pre>
     *  arr[0] -> identifier
     *  arr[1] -> firstName
     *  arr[2] -> lastName
     *  arr[3] -> birthday in ISO-8601 format
     * </pre>
     *
     * @param patronData A string array with the format specified above.
     * @throws IllegalArgumentException if the data format given is invalid.
     */
    public Patron(String[] patronData) {
        //Delegate object construction to other constructor
        this(checkDataFormatAndGetIdentifier(patronData), patronData[1], patronData[2], LocalDate.parse(patronData[3]));
    }

    private static Identifier checkDataFormatAndGetIdentifier(String[] patronData) {
        //Check for an illegal data format.
        if (patronData.length != 4) {
            throw new IllegalArgumentException("Invalid data format used.");
        }
        return new Identifier(patronData[0]);
    }

    /**
     * Converts this patron's data into a format represented by a String Array. This is the inverse function of
     * {@link #Patron(String[])}.
     *
     * @return This patron's data represented as a String array.
     */
    public String[] asData() {
        return new String[]{
                identifier.get().toString(),        //Set identifier to [0]
                firstName.get(),            //Set first name to [1]
                lastName.get(),             //Set last name to [2]
                birthday.get().toString()   //Set Birthday in ISO-8601 to [3]
        };
    }

    /**
     * @return The unique identifier representing this Patron.
     */
    public Identifier getIdentifier() {
        return identifier.get();
    }

    /**
     * Sets the id of this patron. This id must not be duplicated among Patrons.
     * Must not be null.
     *
     * @param id An unique id for this Patron.
     * @throws IllegalArgumentException if {@code id} is {@code null}
     */
    public void setIdentifier(Identifier id) {
        //Verify id is not null
        if (id == null) {
            throw new IllegalArgumentException("Identifier cannot be null");
        }
        this.identifier.set(id);
    }

    public SimpleObjectProperty<Identifier> identifierProperty() {
        return identifier;
    }

    public String getFirstName() {
        return firstName.get();
    }

    public void setFirstName(String firstName) {
        if (firstName == null) {
            throw new IllegalArgumentException("First name cannot be null");
        }
        this.firstName.set(firstName);
    }

    public SimpleStringProperty firstNameProperty() {
        return firstName;
    }

    public String getLastName() {
        return lastName.get();
    }

    public void setLastName(String lastName) {
        if (lastName == null) {
            throw new IllegalArgumentException("Last name cannot be null");
        }
        this.lastName.set(lastName);
    }

    public SimpleStringProperty lastNameProperty() {
        return lastName;
    }

    public LocalDate getBirthday() {
        return birthday.get();
    }

    public void setBirthday(LocalDate birthday) {
        if (birthday == null) {
            throw new IllegalArgumentException("Birthday cannot be null");
        }
        this.birthday.set(birthday);
    }

    public SimpleObjectProperty<LocalDate> birthdayProperty() {
        return birthday;
    }

    /**
     * This function relies on the unique nature of its identifier.
     *
     * @param obj The object to test equality with.
     * @return True if the objects are equal. Otherwise, it returns false.
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Patron && identifier.get().equals(((Patron) obj).identifier.get());
    }
}
