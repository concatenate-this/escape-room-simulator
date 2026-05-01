package game;

public class CommandHandler {
    private String action;
    private String target;
    public CommandHandler() {
        this.action = "";
        this.target = "";
    }
    public void parse(String input) {
        String[] parts = input.trim().toLowerCase().split("\\s+", 2);
        this.action = parts.length > 0 ? parts[0] : "";
        this.target = parts.length > 1 ? parts[1] : "";
    }
    public String getAction() {
        return action;
    }
    public String getTarget() {
        return target;
    }
}
