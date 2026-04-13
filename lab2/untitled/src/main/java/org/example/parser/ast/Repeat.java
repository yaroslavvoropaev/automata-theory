package org.example.parser.ast;

public record Repeat(Node child, int min, Integer max) implements Node {
    public Node reverse() {
        return new Repeat(child.reverse(), min, max);
    }

    @Override
    public String toDot() {
        String range = max == null ?
                "{" + min + ",}" :
                "{" + min + "," + max + "}";
        return toDotNode("REPEAT " + range) + "\n" +
                getNodeId() + " -> " + child.getNodeId() + "\n" +
                child.toDot();
    }
}
