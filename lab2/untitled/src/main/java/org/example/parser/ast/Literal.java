package org.example.parser.ast;

public record Literal(char value) implements Node {

    @Override
    public String toDot() {
        String display = value == '"' ? "\\\"" : String.valueOf(value);
        return toDotNode("LITERAL: '" + display + "'") + "\n";
    }
}