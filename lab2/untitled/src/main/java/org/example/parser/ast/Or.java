package org.example.parser.ast;

public record Or(Node left, Node right) implements Node {
    public Node reverse() { return new Or(left.reverse(), right.reverse()); }

    @Override
    public String toDot() {
        return toDotNode("OR") + "\n" +
                getNodeId() + " -> " + left.getNodeId() + "\n" +
                getNodeId() + " -> " + right.getNodeId() + "\n" +
                left.toDot() +
                right.toDot();
    }
}