package org.parser.implementations.jflex;

import org.parser.model.Token;
import org.parser.interfaces.IHandler;
import com.project.jflex.MyLexer;

import java.io.StringReader;
import java.util.*;

public class JFlexHandler implements IHandler {
    private final Map<String, Set<Character>> staticstics = new HashMap<>();

    @Override
    public boolean handleString(String input) {
        MyLexer lexer = new MyLexer(new StringReader(input));
        String currentCommand;
        Set<Character> currentKeys = new TreeSet<>();

        try {
            Token token = lexer.yylex();
            if (token == null || token.getType() != Token.Type.COMMAND) return false;
            currentCommand = token.getText();

            while ((token = lexer.yylex()) != null) {
                switch (token.getType()) {
                    case KEY_SET:
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

            if (currentCommand.isEmpty()) return false;

            staticstics.computeIfAbsent(currentCommand, k -> new TreeSet<>()).addAll(currentKeys);
            return true;

        } catch (Exception e) {
            return false;
        }
    }


    @Override
    public Map<String, Set<Character>> getStatistics() {
        return staticstics;
    }
}