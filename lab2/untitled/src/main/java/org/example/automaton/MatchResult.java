package org.example.automaton;

import java.util.Map;

public class MatchResult {
    private final boolean isMatch;
    private final Map<String, String> groups;

    public MatchResult(boolean isMatch, Map<String, String> groups) {
        this.isMatch = isMatch;
        this.groups = groups;
    }

    public boolean matches() {
        return isMatch;
    }


    public String group(String name) {
        return groups.get(name);
    }
}