package org.example.parser.ast;

public sealed interface Node
        permits Literal, AnyChar, Or, Concat, Repeat, NamedGroup, Group {

    Node reverse();
}



