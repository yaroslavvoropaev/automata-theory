package org.example.parser.ast;

public record NamedGroup(String name, Node child) implements Node {

    @Override
    public String toDot() {
        return toDotNode("NAMED_GROUP: " + name) + "\n" +
                getNodeId() + " -> " + child.getNodeId() + "\n" +
                child.toDot();
    }
}