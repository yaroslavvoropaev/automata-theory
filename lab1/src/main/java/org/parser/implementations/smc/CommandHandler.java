package org.parser.implementations.smc;

import org.parser.implementations.smc.generated.CommandRecognizerContext;
import org.parser.interfaces.IHandler;
import java.util.*;

public class CommandHandler implements IHandler {
    private CommandRecognizerContext fsm;

    private final Map<String, Set<Character>> statistics = new HashMap<>();

    private final StringBuilder currentCommand = new StringBuilder();
    private final Set<Character> currentKeys = new TreeSet<>();
    private boolean isError = false;

    public CommandHandler() {
        fsm = new CommandRecognizerContext(this);
    }

    @Override
    public boolean handleString(String str) {

        fsm = new CommandRecognizerContext(this);
        currentCommand.setLength(0);
        currentKeys.clear();
        isError = false;

        if (str == null || str.trim().isEmpty()) return false;

        for (char c : str.toCharArray()) {
            if (isError) break;
            processCharacter(c);
        }

        if (!isError) {
            fsm.EOF();
        }

        boolean isValid = fsm.getState().getName().contains("Valid");

        if (isValid) {
            String cmd = currentCommand.toString();
            statistics.putIfAbsent(cmd, new HashSet<>());
            statistics.get(cmd).addAll(currentKeys);
        }

        return isValid;
    }

    private void processCharacter(char c) {
        try {
            if (isAlphaNum(c) || c == '.' || c == '/' || c == '-' || Character.isWhitespace(c)) {
                fsm.currentChar(c);
            } else {
                isError = true;
            }
        } catch (Exception e) {
            isError = true;
        }
    }

    private boolean isAlphaNum(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9');
    }

    public void addCommandChar(char c) { currentCommand.append(c); }
    public void addKey(char c) { currentKeys.add(c); }

    @Override
    public Map<String, Set<Character>> getStatistics() {
        return statistics;
    }
}