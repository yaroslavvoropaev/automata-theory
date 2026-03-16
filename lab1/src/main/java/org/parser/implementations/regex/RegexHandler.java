package org.parser.implementations.regex;

import org.parser.interfaces.IHandler;



import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexHandler implements IHandler {

    private static final Pattern commandPattern = Pattern.compile("^([a-zA-Z0-9./]+)(\\s+-[a-zA-Z0-9]+)*$");
    private static final Pattern keyPattern = Pattern.compile("-([a-zA-Z0-9]+)");
    private final Map<String, Set<Character>> statistics = new HashMap<>();

    @Override
    public boolean handleString(String str) {
        if (str == null) {
            return false;
        }

        Matcher commandMatcher = commandPattern.matcher(str.trim());

        if (!commandMatcher.matches()) {
            return false;
        }
        String commandName = commandMatcher.group(1);
        Set<Character> keys = statistics.computeIfAbsent(commandName, k -> new TreeSet<>());

        Matcher keyMatcher = keyPattern.matcher(str);
        while (keyMatcher.find()) {
            for (char c : keyMatcher.group(1).toCharArray()) {
                keys.add(c);
            }
        }
        return true;
    }


    @Override
    public Map<String, Set<Character>> getStatistics() {
        return statistics;
    }

}