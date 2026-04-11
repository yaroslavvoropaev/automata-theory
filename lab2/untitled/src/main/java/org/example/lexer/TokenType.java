package org.example.lexer;

public enum TokenType {
    CHAR,
    OR,               // |
    CONCAT_OP,        // -
    PLUS,             // +
    QUESTION,         // ?
    DOT,              // .
    LPAREN,           // (
    RPAREN,           // )
    RANGE,            // то, что внутри {x, y}
    NAMED_GROUP,      // то, что внутри <name>
    EOF               // EOF
}

