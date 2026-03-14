package org.parser.model;

public class Token {
    public enum Type {
        PART_COMMAND,
        DASH,
        KEY_SET,
        ERROR,
        SPACE,
        END_OF_LINE,
    }

    private final Type type;
    private final String text;

    public Token(Type type, String text) {
        this.type = type;
        this.text = text;
    }
    public Type getType() {
        return type;
    }
    public String getText() {
        return text;
    }
}