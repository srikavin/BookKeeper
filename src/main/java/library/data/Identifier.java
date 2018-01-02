package library.data;

import java.util.Objects;

public class Identifier {
    private final String id;

    public Identifier(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof Identifier && Objects.equals(id, o));
    }

    public int get() {
        return Integer.parseInt(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return id;
    }
}
