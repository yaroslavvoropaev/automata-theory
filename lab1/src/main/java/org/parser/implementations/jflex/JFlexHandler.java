package org.parser.implementations.jflex;


import com.commandparser.implementations.jflex.generated.CommandLexer;
import com.commandparser.implementations.jflex.generated.CommandLexer.Token;

import org.parser.interfaces.IHandler;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

public class JFlexHandler implements IHandler {

    private CommandLexer lexer;
    private final Map<String, Set<Character>> statistics = new HashMap<>();

    private boolean parseLine(String line) throws IOException {
        StringReader reader = new StringReader(line);
        lexer = new CommandLexer(reader);
        lexer.reset();

        Token token;
        boolean hasCommand = false;

        while ((token = lexer.yylex()) != null) {
            switch (token) {
                case COMMAND:
                    hasCommand = true;
                    break;
                case ERROR:
                    return false;

                default:
                    break;
            }
        }
        return hasCommand && lexer.isValid();
    }

    private void mergeStatistics() {
        Map<String, Set<Character>> lexerStats = lexer.getStatistics();

        for (Map.Entry<String, Set<Character>> entry : lexerStats.entrySet()) {
            String command = entry.getKey();
            Set<Character> keys = entry.getValue();

            Set<Character> commandKeys = statistics.computeIfAbsent(command, k -> new TreeSet<>());

            commandKeys.addAll(keys);
        }
    }


    @Override
    public boolean handleString(String fileName) {
        if (fileName == null) {
            return false;
        }

        String trimmedLine = fileName.trim();
        if (trimmedLine.isEmpty()) {
            return false;
        }

        try {
            boolean isValid = parseLine(trimmedLine);
            if (isValid) {
                mergeStatistics();
            }
            return isValid;
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public Map<String, Set<Character>> getStatistics() {
        Map<String, Set<Character>> copyMap = new HashMap<>();
        for (Map.Entry<String, Set<Character>> pair : statistics.entrySet()) {
            copyMap.put(pair.getKey(), new TreeSet<>(pair.getValue()));
        }
        return Collections.unmodifiableMap(copyMap);
    }

}
