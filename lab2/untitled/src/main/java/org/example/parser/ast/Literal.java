package org.example.parser.ast;

public record Literal(char value) implements Node {
    public Node reverse() { return this; }
}