package org.parser.implementations.smc;

import com.project.smc.CommandRecognizerContext;
import org.parser.interfaces.IHandler;
import java.util.*;

public class SmcHandler implements IHandler {
    private CommandRecognizerContext fsm;

    private final Map<String, Set<Character>> statistics = new HashMap<>();

    private final StringBuilder currentCommand = new StringBuilder();
    private final Set<Character> currentKeys = new TreeSet<>();

    public SmcHandler() {
        fsm = new CommandRecognizerContext(this);
    }

    @Override
    public boolean handleString(String str) {
        if (str == null)  {
            return false;
        }

        fsm = new CommandRecognizerContext(this);
        currentCommand.setLength(0);
        currentKeys.clear();

        for (char c : str.toCharArray()) {
            if (fsm.getState().getName().contains("Error")) {
                return false;
            }
            fsm.currentChar(c);
        }


        fsm.EOF();
        boolean isValid = fsm.getState().getName().contains("Valid");

        if (isValid) {
            String command = currentCommand.toString();
            statistics.putIfAbsent(command, new TreeSet<>());
            statistics.get(command).addAll(currentKeys);
        }
        return isValid;
    }

    public void addCommandChar(char c) {
        currentCommand.append(c);
    }
    public void addKey(char c) {
        currentKeys.add(c);
    }

    @Override
    public Map<String, Set<Character>> getStatistics() {
        return statistics;
    }
}