package org.example.parser.ast;


public record Group(Node child) implements Node {

    @Override
    public String toDot() {
        return toDotNode("GROUP") + "\n" +
                getNodeId() + " -> " + child.getNodeId() + "\n" +
                child.toDot();
    }

}