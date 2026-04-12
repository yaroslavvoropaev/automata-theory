package org.example.parser;

import org.example.lexer.Token;
import org.example.lexer.TokenType;
import org.example.parser.ast.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Parser {

    private final List<Token> tokens;
    private int pos = 0;
    private final Map<String, Node> namedGroupsStore = new HashMap<>();

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public boolean hasNamedGroups() {
        return !namedGroupsStore.isEmpty();
    }

    public Node parse() {
        Node result = parseExpression();
        if (peek().type() != TokenType.EOF) {
            throw new RuntimeException("Extra characters at the end of the expression in the position " + peek().position());
        }
        return result;
    }

    private Node parseExpression() {
        Node left = parseSequence();
        while (match(TokenType.OR)) {
            Node right = parseSequence();
            left = new Or(left, right);
        }
        return left;
    }

    private Node parseSequence() {          // конкантенация
        Node left = parsePostfix();

        while (canStartExpression(peek().type())) {
            match(TokenType.CONCAT_OP);     // если есть -, скушать его
            Node right = parsePostfix();
            left = new Concat(left, right);
        }
        return left;
    }

    private Node parsePostfix() {            // квантификаторы
        Node node = parsePrimary();

        while (true) {
            if (match(TokenType.PLUS)) {
                node = new Repeat(node, 1, null);
            } else if (match(TokenType.QUESTION)) {
                node = new Repeat(node, 0, 1);
            } else if (peek().type() == TokenType.RANGE) {
                Token rangeToken = consume(TokenType.RANGE);
                int[] range = parseRange(rangeToken.value());
                node = new Repeat(node, range[0], range[1] == -1 ? null : range[1]);
            } else {
                break;
            }
        }
        return node;
    }

    private Node parsePrimary() {

        if (match(TokenType.LPAREN)) {
            if (peek().type() == TokenType.NAMED_GROUP) {
                String name = consume(TokenType.NAMED_GROUP).value();
                Node content = parseExpression();
                consume(TokenType.RPAREN);

                namedGroupsStore.put(name, content);
                return new NamedGroup(name, content);
            }

            Node content = parseExpression();
            consume(TokenType.RPAREN);
            return new Group(content);
        }

        if (peek().type() == TokenType.NAMED_GROUP) {
            String name = consume(TokenType.NAMED_GROUP).value();
            Node savedNode = namedGroupsStore.get(name);

            if (savedNode == null) {
                throw new RuntimeException("Error: The named group <" + name + "> was used before it was defined");
            }
            return savedNode;
        }

        if (match(TokenType.DOT)) {
            return new AnyChar();
        }

        Token token = peek();
        if (match(TokenType.CHAR)) {
            return new Literal(token.value().charAt(0));
        }

        throw new RuntimeException("A character or an opening parenthesis was expected at the position " + peek().position());
    }

    private boolean canStartExpression(TokenType type) {
        return type == TokenType.CHAR ||
                type == TokenType.DOT ||
                type == TokenType.LPAREN ||
                type == TokenType.NAMED_GROUP ||
                type == TokenType.CONCAT_OP;
    }

    private int[] parseRange(String value) {
        try {
            if (!value.contains(",")) {
                int n = Integer.parseInt(value);
                return new int[]{n, n};
            }
            String[] parts = value.split(",", -1);
            int min = parts[0].isEmpty() ? 0 : Integer.parseInt(parts[0]);
            int max = (parts.length < 2 || parts[1].isEmpty()) ? -1 : Integer.parseInt(parts[1]);
            return new int[]{min, max};
        } catch (NumberFormatException e) {
            throw new RuntimeException("Incorrect range format: {" + value + "}");
        }
    }

    private Token peek() {
        return tokens.get(pos);
    }

    private boolean match(TokenType type) {
        if (peek().type() == type) {
            pos++;
            return true;
        }
        return false;
    }

    private Token consume(TokenType type) {
        if (peek().type() == type) {
            return tokens.get(pos++);
        }
        throw new RuntimeException("The token " + type + " was expected, but " + peek().type() + " was found at the position " + peek().position());
    }
}