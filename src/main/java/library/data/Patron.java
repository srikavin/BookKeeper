package library.data;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;

public class Patron {
    /**
     * An observable container to hold the Identifier
     */
    private SimpleObjectProperty<Identifier> id = new SimpleObjectProperty<>();
    private SimpleStringProperty firstName = new SimpleStringProperty();
    private SimpleStringProperty lastName = new SimpleStringProperty();
    private SimpleObjectProperty<LocalDate> birthday = new SimpleObjectProperty<>();

    /**
     * Creates a {@linkplain Patron} object using the specified parameters.
     *
     * @param id        A {@link Identifier} object containing the desired identifier for this patron
     * @param firstName A {@linkplain String} object containing the desired first name of this patron
     * @param lastName  A {@linkplain String} object containing the desired last name of this patron
     * @param birthday  A {@linkplain LocalDate} object containing the desired birthday of this patron
     */
    public Patron(Identifier id, String firstName, String lastName, LocalDate birthday) {
        this.id.set(id);
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
     */
    public Patron(String[] patronData) {
        //Delegate object construction to other constructor
        this(new Identifier(patronData[0]), patronData[1], patronData[2], LocalDate.parse(patronData[3]));
    }

    public Identifier getId() {
        return id.get();
    }

    public void setId(Identifier id) {
        this.id.set(id);
    }

    public SimpleObjectProperty<Identifier> idProperty() {
        return id;
    }

    public String getFirstName() {
        return firstName.get();
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    public SimpleStringProperty firstNameProperty() {
        return firstName;
    }

    public String getLastName() {
        return lastName.get();
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    public SimpleStringProperty lastNameProperty() {
        return lastName;
    }

    public LocalDate getBirthday() {
        return birthday.get();
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday.set(birthday);
    }

    public SimpleObjectProperty<LocalDate> birthdayProperty() {
        return birthday;
    }
}
