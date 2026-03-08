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
        String currentCommand = null;
        Set<Character> currentKeys = new TreeSet<>();

        try {
            Token token;
            while ((token = lexer.yylex()) != null) {
                switch (token.getType()) {
                    case COMMAND:
                        if (currentCommand != null) return false;

                        currentCommand = token.getText();
                        break;
                    case KEY_SET:
                        if (currentCommand == null) return false;

                        String keys = token.getText();
                        for (int i = 0; i < keys.length(); i++) {
                            currentKeys.add(keys.charAt(i));
                        }
                        break;

                    case SPACE:
                        break;

                    default:
                        return false;
                }
            }
            statistics.computeIfAbsent(currentCommand, k -> new TreeSet<>()).addAll(currentKeys);
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