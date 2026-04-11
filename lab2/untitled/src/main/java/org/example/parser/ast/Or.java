package org.example.parser.ast;

public record Or(Node left, Node right) implements Node {
    public Node reverse() { return new Or(left.reverse(), right.reverse()); }
}