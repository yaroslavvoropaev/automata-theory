package org.example.lexer;

public record Token(TokenType type, String value, int position) {
    @Override
    public String toString() {
        return String.format("Token{%s, '%s', pos=%d}", type, value, position);
    }
}
