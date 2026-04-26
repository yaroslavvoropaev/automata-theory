package org.example.parser.ast;

public record AnyChar() implements Node {

    @Override
    public String toDot() {
        return toDotNode("ANY_CHAR: .") + "\n";
    }
}
