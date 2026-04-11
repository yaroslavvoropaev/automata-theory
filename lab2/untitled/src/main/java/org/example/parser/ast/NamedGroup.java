package org.example.parser.ast;

public record NamedGroup(String name, Node child) implements Node {
    public Node reverse() { return new NamedGroup(name, child.reverse()); }
}