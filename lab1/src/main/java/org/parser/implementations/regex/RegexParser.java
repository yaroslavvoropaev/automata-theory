package org.parser.implementations.regex;

import org.parser.interfaces.IParser;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexParser implements IParser {

    private static final String commandStr = "^[a-zA-Z0-9./]+(?: -[a-zA-Z0-9]+)*$";
    private static final Pattern commandPattern = Pattern.compile(commandStr);
    private static final Pattern keyGroupPattern = Pattern.compile(" -([a-zA-Z0-9]+)");

    private final Map<String, Set<Character>> statistics = new HashMap<>();

    @Override
    public boolean handleString(String str) {
        if (str == null) {
            return false;
        }

        String trimStr = str.trim();

        if (!commandPattern.matcher(trimStr).matches()) {
            return false;
        }

        String[] parts = trimStr.split( " ", 2);
        Set<Character> keys = statistics.computeIfAbsent(parts[0], k -> new TreeSet<>());

        if (parts.length > 1) {
            String keysPart = parts[1];
            Matcher matcher = keyGroupPattern.matcher(" " + keysPart);

            while(matcher.find()) {
                String keyWithoutHyphen = matcher.group(1);
                for (char symb : keyWithoutHyphen.toCharArray()) {
                    keys.add(symb);
                }
            }
        }
        return true;
    }

    @Override
    public Map<String, Set<Character>> getStatistics() {
        Map<String, Set<Character>> copyMap = new HashMap<>();
        for (Map.Entry<String, Set<Character>> pair : statistics.entrySet()) {
            copyMap.put(pair.getKey(), new TreeSet<>(pair.getValue()));
        }
        return Collections.unmodifiableMap(copyMap);
     }

    @Override
    public void clearStatistics() {
        statistics.clear();
    }
}
