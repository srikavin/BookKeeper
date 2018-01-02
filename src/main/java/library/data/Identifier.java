package library.data;

public class Identifier {
    private final String id;

    public Identifier(String id) {
        this.id = id;
    }

    public Identifier(int id) {
        this.id = String.valueOf(id);
    }

    public String getId() {
        return id;
    }

    public int get(){
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
