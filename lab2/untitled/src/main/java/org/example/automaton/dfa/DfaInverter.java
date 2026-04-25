package org.example.automaton.dfa;

import org.example.automaton.nfa.NfaFragment;
import org.example.automaton.nfa.NfaState;

import java.util.*;

public class DfaInverter {

    public DfaState invert(DfaState startState) {
        Set<DfaState> dfaStates = getAllStates(startState);

        Map<DfaState, NfaState> dfaToNfaMap = new HashMap<>();
        for (DfaState state : dfaStates) {
            dfaToNfaMap.put(state, new NfaState());
        }

        NfaState newNfaStart = new NfaState();
        NfaState newNfaEnd = dfaToNfaMap.get(startState);

        for (DfaState state : dfaStates) {
            NfaState currentNfa = dfaToNfaMap.get(state);

            if (state.isFinal) {
                newNfaStart.epsilons.add(currentNfa);
            }

            for (Map.Entry<Character, DfaState> transition : state.transitions.entrySet()) {
                char symbol = transition.getKey();
                DfaState targetDfa = transition.getValue();
                NfaState targetNfa = dfaToNfaMap.get(targetDfa);
                targetNfa.transitions.computeIfAbsent(symbol, k -> new ArrayList<>()).add(currentNfa);
            }
        }

        NfaFragment invertedNfa = new NfaFragment(newNfaStart, newNfaEnd);

        DfaBuilder builder = new DfaBuilder();
        DfaState invertedDfa = builder.build(invertedNfa);
        DfaMinimizer minimizer = new DfaMinimizer();

        return minimizer.minimize(invertedDfa);
    }

    private Set<DfaState> getAllStates(DfaState start) {
        Set<DfaState> visited = new HashSet<>();
        Queue<DfaState> queue = new ArrayDeque<>();
        queue.add(start);
        visited.add(start);
        while (!queue.isEmpty()) {
            DfaState curr = queue.poll();
            for (DfaState target : curr.transitions.values()) {
                if (visited.add(target)) {
                    queue.add(target);
                }
            }
        }
        return visited;
    }
}
