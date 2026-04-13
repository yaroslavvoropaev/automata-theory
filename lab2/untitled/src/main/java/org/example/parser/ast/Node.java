package org.example.parser.ast;

public sealed interface Node
        permits Literal, AnyChar, Or, Concat, Repeat, NamedGroup, Group {

    Node reverse();
    String toDot();
    default String toDotNode(String label) {
        return "\"" + System.identityHashCode(this) + "\" [label=\"" + label + "\"]";
    }
    default String getNodeId() {
        return "\"" + System.identityHashCode(this) + "\"";
    }
}



