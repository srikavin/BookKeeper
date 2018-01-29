package library.data;

import java.util.Objects;

public class Identifier implements Comparable<Identifier> {
    private final String id;

    public Identifier(String id) {
        this.id = id;
    }

    public Identifier(int id) {
        this.id = String.format("%06d", id);
    }

    public String getId() {
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        return this == o || (o instanceof Identifier && Objects.equals(id, ((Identifier) o).id));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(Identifier o) {
        return id.compareTo(o.id);
    }
}
