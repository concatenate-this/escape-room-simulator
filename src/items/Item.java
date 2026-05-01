package items;

public abstract class Item {
    protected String name;
    protected String description;
    public Item(String name, String description) {
        this.name = name;
        this.description = description;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public abstract String use();
    @Override
    public String toString() {
        return name + ": " + description;
    }
}
