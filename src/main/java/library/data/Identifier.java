package library.data;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;

public class Identifier {
    private final SimpleStringProperty id = new ReadOnlyStringWrapper();

    public Identifier(String id) {
        this.id.set(id);
    }

    public Identifier(int id) {
        this.id.set(String.valueOf(id));
    }

    public SimpleStringProperty idProperty() {
        return id;
    }

    public String getId() {
        return id.get();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return id.get();
    }
}
