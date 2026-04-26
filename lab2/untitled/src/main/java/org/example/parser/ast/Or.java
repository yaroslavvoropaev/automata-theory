package org.example.parser.ast;

public record Or(Node left, Node right) implements Node {

    @Override
    public String toDot() {
        return toDotNode("OR") + "\n" +
                getNodeId() + " -> " + left.getNodeId() + "\n" +
                getNodeId() + " -> " + right.getNodeId() + "\n" +
                left.toDot() +
                right.toDot();
    }
}