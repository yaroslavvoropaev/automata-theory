package org.example.matcher;

import org.example.automaton.nfa.NfaFragment;
import org.example.automaton.nfa.NfaState;

import java.util.*;

public class NfaMatcher {
    private final NfaFragment nfa;
    public NfaMatcher(NfaFragment nfa) {
        this.nfa = nfa;
    }

    public MatchResult match(String text) {
        Map<String, Integer> groupStarts = new HashMap<>();    // ключ - имя группы, значение - индекс начала в тексте
        Map<String, String> capturedGroups = new HashMap<>();  // ключ - имя группы, значение - захваченный текст
        boolean isMatch = dfs(nfa.start(), text, 0, groupStarts, capturedGroups, new HashSet<>());

        if (isMatch) {
            return new MatchResult(true, capturedGroups);
        }
        return new MatchResult();
    }
    private boolean dfs(NfaState state, String text, int index,
                        Map<String, Integer> groupStarts,
                        Map<String, String> capturedGroups,
                        Set<NfaState> visitedEpsilons) {

        for (Map.Entry<String, Boolean> entry : state.groupInfo.entrySet()) {
            String groupName = entry.getKey();
            boolean isStart = entry.getValue();

            if (isStart) {
                groupStarts.put(groupName, index);
            } else {
                Integer startIdx = groupStarts.get(groupName);
                if (startIdx != null && startIdx <= index) {
                    capturedGroups.put(groupName, text.substring(startIdx, index));
                }
            }
        }

        if (index == text.length()) {
            if (state.isFinal) return true;

            for (NfaState eps : state.epsilons) {
                if (!visitedEpsilons.contains(eps)) {
                    visitedEpsilons.add(eps);
                    if (dfs(eps, text, index, groupStarts, capturedGroups, visitedEpsilons)) {
                        return true;
                    }
                    visitedEpsilons.remove(eps);
                }
            }
            return false;
        }

        for (NfaState eps : state.epsilons) {
            if (!visitedEpsilons.contains(eps)) {
                visitedEpsilons.add(eps);
                if (dfs(eps, text, index, groupStarts, capturedGroups, visitedEpsilons)) return true;
                visitedEpsilons.remove(eps);
            }
        }

        char c = text.charAt(index);

        List<NfaState> nextStates = state.transitions.get(c);
        if (nextStates != null) {
            for (NfaState next : nextStates) {
                if (dfs(next, text, index + 1, groupStarts, capturedGroups, new HashSet<>())) return true;
            }
        }

        List<NfaState> anyCharStates = state.transitions.get('\uFFFF');
        if (anyCharStates != null) {
            for (NfaState next : anyCharStates) {
                if (dfs(next, text, index + 1, groupStarts, capturedGroups, new HashSet<>())) return true;
            }
        }
        return false;
    }
}