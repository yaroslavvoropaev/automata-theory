package org.example.parser.ast;

public record Repeat(Node child, int min, Integer max) implements Node {
    public Node reverse() {
        return new Repeat(child.reverse(), min, max);
    }
}
