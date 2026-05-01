package items;

public class Key extends Item{
    private String keyId;
    public Key(String name, String description, String keyId) {
        super(name, description);
        this.keyId = keyId;
    }
    public String getKeyId() {
        return keyId;
    }
    @Override
    public String use() {
        return "You hold the " + name + ". It might unlock something.";
    }
}
