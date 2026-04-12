package org.example.matcher;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

public class MatchResult implements Iterable<Map.Entry<String, String>> {
    private final boolean isMatch;
    private final Map<String, String> groups;

    public MatchResult() {
        this.isMatch = false;
        this.groups = Collections.emptyMap();
    }

    public MatchResult(boolean isMatch, Map<String, String> groups) {
        this.isMatch = isMatch;
        this.groups = groups != null ? groups : new HashMap<>();
    }

    public boolean isMatch() {
        return isMatch;
    }

    public String get(String groupName) {
        if (!isMatch) throw new IllegalStateException("No match found");
        return groups.get(groupName);
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator() {
        return groups.entrySet().iterator();
    }
}