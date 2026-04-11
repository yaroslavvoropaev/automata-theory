package org.example.parser.ast;

public record Concat(Node left, Node right) implements Node {
    public Node reverse() { return new Concat(right.reverse(), left.reverse()); }
}