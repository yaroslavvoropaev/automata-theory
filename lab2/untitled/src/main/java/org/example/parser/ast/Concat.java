package org.example.parser.ast;

public record Concat(Node left, Node right) implements Node {

    @Override
    public String toDot() {
        return toDotNode("CONCAT") + "\n" +
                getNodeId() + " -> " + left.getNodeId() + "\n" +
                getNodeId() + " -> " + right.getNodeId() + "\n" +
                left.toDot() +
                right.toDot();
    }
}