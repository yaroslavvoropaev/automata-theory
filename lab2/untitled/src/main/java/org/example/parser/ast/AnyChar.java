package org.example.parser.ast;

public record AnyChar() implements Node {
    public Node reverse() { return this; }
}
