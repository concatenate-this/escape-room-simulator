package items;

public class Clue extends Item{
    private String hint;
    public Clue(String name, String description, String hint) {
        super(name, description);
        this.hint = hint;
    }
    public String getHint() {
        return hint;
    }
    @Override
    public String use() {
        return "The clue reads: \"" + hint + "\"";
    }
}
