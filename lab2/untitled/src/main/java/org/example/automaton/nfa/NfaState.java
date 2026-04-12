package org.example.automaton.nfa;

import java.util.*;

public class NfaState {
    private static int idCounter = 0;
    public final int id;

    public final Map<Character, List<NfaState>> transitions = new HashMap<>();
    public final List<NfaState> epsilons = new ArrayList<>();
    public final Map<String, Boolean> groupInfo = new HashMap<>();
    public boolean isFinal = false;

    public NfaState() {
        this.id = idCounter++;
    }
    public void addTransition(char c, NfaState to)
    {
        transitions.computeIfAbsent(c, k -> new ArrayList<>()).add(to);
    }

    public void addEpsilon(NfaState to) {
        epsilons.add(to);
    }
}