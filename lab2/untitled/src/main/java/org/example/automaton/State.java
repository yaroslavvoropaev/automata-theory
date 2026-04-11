package org.example.automaton;

import java.util.*;

public class State {
    private static int idCounter = 0;
    public final int id;

    public final Map<Character, List<State>> transitions = new HashMap<>();
    public final List<State> epsilons = new ArrayList<>();
    public final Map<String, Boolean> groupInfo = new HashMap<>();
    public boolean isFinal = false;

    public State() {
        this.id = idCounter++;
    }
    public void addTransition(char c, State to)
    {
        transitions.computeIfAbsent(c, k -> new ArrayList<>()).add(to);
    }

    public void addEpsilon(State to) {
        epsilons.add(to);
    }
}