package org.example.parser.ast;


public record Group(Node child) implements Node {
    public Node reverse() { return new Group(child.reverse()); }
}