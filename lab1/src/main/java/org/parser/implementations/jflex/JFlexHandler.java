package org.parser.implementations.jflex;

import org.parser.model.Token;
import org.parser.interfaces.IHandler;
import com.project.jflex.MyLexer;

import java.io.StringReader;
import java.util.*;

public class JFlexHandler implements IHandler {
    private final Map<String, Set<Character>> statistics = new HashMap<>();

    @Override
    public boolean handleString(String input) {
        MyLexer lexer = new MyLexer(new StringReader(input));
        StringBuilder currentCommand = new StringBuilder();
        Set<Character> currentKeys = new TreeSet<>();

        try {
            Token token;
            while ((token = lexer.yylex()) != null && token.getType() != Token.Type.END_OF_LINE) {
                switch (token.getType()) {
                    case PART_COMMAND -> {
                        currentCommand.append(token.getText());
                    }
                    case KEY_SET -> {
                        String keys = token.getText();
                        for (int i = 0; i < keys.length(); i++) {
                            currentKeys.add(keys.charAt(i));
                        }
                    }
                    case SPACE -> {}
                    case ERROR -> {
                        return false;
                    }
                }
            }
            statistics.computeIfAbsent(currentCommand.toString(), k -> new TreeSet<>()).addAll(currentKeys);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    @Override
    public Map<String, Set<Character>> getStatistics() {
        return statistics;
    }
}