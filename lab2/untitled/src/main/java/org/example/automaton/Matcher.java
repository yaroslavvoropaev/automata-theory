package org.example.automaton;

import java.util.HashMap;
import java.util.Map;

public class Matcher {
    private final String text;
    private final State startState;

    public Matcher(State startState, String text) {
        this.startState = startState;
        this.text = text;
    }

    public MatchResult match() {
        Map<String, Integer> starts = new HashMap<>();
        Map<String, Integer> ends = new HashMap<>();

        boolean result = dfs(startState, 0, starts, ends);

        if (!result) {
            return new MatchResult(false, null);
        }

        Map<String, String> capturedGroups = new HashMap<>();
        for (String groupName : starts.keySet()) {
            int s = starts.get(groupName);
            int e = ends.getOrDefault(groupName, s);
            capturedGroups.put(groupName, text.substring(s, e));
        }

        return new MatchResult(true, capturedGroups);
    }


    private boolean dfs(State currentState, int index, Map<String, Integer> starts, Map<String, Integer> ends) {
        Map<String, Integer> oldStarts = new HashMap<>();
        Map<String, Integer> oldEnds = new HashMap<>();

        for (Map.Entry<String, Boolean> entry : currentState.groupInfo.entrySet()) {
            String name = entry.getKey();
            boolean isStart = entry.getValue();
            if (isStart) {
                if (starts.containsKey(name)) {
                    oldStarts.put(name, starts.get(name));
                }
                starts.put(name, index);
            } else {
                if (ends.containsKey(name)) {
                    oldEnds.put(name, ends.get(name));
                }
                ends.put(name, index);
            }
        }

        if (index == text.length() && currentState.isFinal) {
            return true;
        }

        for (State nextState : currentState.epsilons) {
            if (dfs(nextState, index, starts, ends))  {
                return true;
            }
        }

        if (index < text.length()) {
            char c = text.charAt(index);

            if (currentState.transitions.containsKey(c)) {
                for (State nextState : currentState.transitions.get(c)) {
                    if (dfs(nextState, index + 1, starts, ends)) {
                        return true;
                    }
                }
            }

            if (currentState.transitions.containsKey('\uFFFF')) {
                for (State nextState : currentState.transitions.get('\uFFFF')) {
                    if (dfs(nextState, index + 1, starts, ends)) {
                        return true;
                    }
                }
            }
        }

        for (Map.Entry<String, Boolean> entry : currentState.groupInfo.entrySet()) {
            String name = entry.getKey();
            if (entry.getValue()) {
                if (oldStarts.containsKey(name)) {
                    starts.put(name, oldStarts.get(name));
                } else {
                    starts.remove(name);
                }
            } else {
                if (oldEnds.containsKey(name)) {
                    ends.put(name, oldEnds.get(name));
                } else {
                    ends.remove(name);
                }
            }
        }
        return false;
    }
}