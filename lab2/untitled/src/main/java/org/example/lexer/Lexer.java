package org.example.lexer;

import java.util.List;
import java.util.ArrayList;

public class Lexer {
    private final String input;
    private int pos = 0;

    public Lexer(String input) {
        this.input = input;
    }

    private Token consumeBlock(char endChar, TokenType type) {
        int startPos = pos;
        ++pos;
        StringBuilder stringBuilder = new StringBuilder();

        while (pos < input.length() && input.charAt(pos) != endChar) {
            stringBuilder.append(input.charAt(pos++));
        }

        if (pos >= input.length() || input.charAt(pos) != endChar) {
            throw new RuntimeException("Mistake: the closing character was not found '" + endChar + "' for a block in a position " + startPos);
        }

        ++pos;
        return new Token(type, stringBuilder.toString(), startPos);
    }

    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        while(pos < input.length()) {
            char c = input.charAt(pos);
            if (c == '&') {
                pos++;
                if (pos < input.length()) {
                    tokens.add(new Token(TokenType.CHAR, String.valueOf(input.charAt(pos++)), pos - 2));
                } else {
                    throw new RuntimeException("Error: the character after the '&' was expected in the position" + pos);
                }
                continue;
            }

            switch(c) {
                case '|':
                    tokens.add(new Token(TokenType.OR, "|", pos++));
                    break;
                case '+':
                    tokens.add(new Token(TokenType.PLUS, "+", pos++));
                    break;
                case '?':
                    tokens.add(new Token(TokenType.QUESTION, "?", pos++));
                    break;
                case '.':
                    tokens.add(new Token(TokenType.DOT, ".", pos++));
                    break;
                case '-':
                    tokens.add(new Token(TokenType.CONCAT_OP, "-", pos++));
                    break;
                case '(':
                    tokens.add(new Token(TokenType.LPAREN, "(", pos++));
                    break;
                case ')':
                    tokens.add(new Token(TokenType.RPAREN, ")", pos++));
                    break;
                case '{':
                    tokens.add(consumeBlock('}', TokenType.RANGE));
                    break;
                case '<':
                    tokens.add(consumeBlock('>', TokenType.NAMED_GROUP));
                    break;
                default:
                    tokens.add(new Token(TokenType.CHAR, String.valueOf(c), pos++));
                    break;
            }
        }
        tokens.add(new Token(TokenType.EOF, "", pos));
        return tokens;
    }
}
