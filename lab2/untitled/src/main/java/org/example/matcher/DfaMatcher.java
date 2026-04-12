package org.example.matcher;

import org.example.automaton.dfa.DfaState;

public class DfaMatcher {
    private final DfaState startState;

    public DfaMatcher(DfaState startState) {
        this.startState = startState;
    }

    public boolean match(String text) {
        DfaState current = startState;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            DfaState next = current.transitions.get(c);

            if (next == null) {
                next = current.transitions.get('\uFFFF');
            }

            if (next == null) {
                return false;
            }

            current = next;
        }

        return current.isFinal;
    }
}