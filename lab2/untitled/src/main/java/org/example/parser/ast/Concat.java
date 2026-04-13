package org.example.parser.ast;

public record Concat(Node left, Node right) implements Node {
    public Node reverse() { return new Concat(right.reverse(), left.reverse()); }

    @Override
    public String toDot() {
        return toDotNode("CONCAT") + "\n" +
                getNodeId() + " -> " + left.getNodeId() + "\n" +
                getNodeId() + " -> " + right.getNodeId() + "\n" +
                left.toDot() +
                right.toDot();
    }
}