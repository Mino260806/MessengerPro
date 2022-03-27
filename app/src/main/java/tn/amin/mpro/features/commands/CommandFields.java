package tn.amin.mpro.features.commands;

public class CommandFields {
    public String name = "";
    public String description = "";
    public CommandFields(String name, String description) {
        this.name = name;
        this.description = description;
    }
    public String getIconName() {
        return "command_" + name;
    }
}
