package library.data;

public class Identifier {
    private final int id;

    public Identifier(String id) {
        this.id = Integer.parseInt(id);
    }

    public Identifier(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
