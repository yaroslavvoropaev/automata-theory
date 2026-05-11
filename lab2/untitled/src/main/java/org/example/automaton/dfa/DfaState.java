package org.example.automaton.dfa;


import org.example.automaton.nfa.NfaState;
import java.util.*;

public class DfaState {

    private static int idCounter = 0;
    public final int id;

    public DfaState(boolean isFinal) {
        this.id = idCounter++;
        this.nfaStates = Collections.emptySet();
        this.isFinal = isFinal;
    }

    public final Set<NfaState> nfaStates;         // соотв состояния нка
    public final Map<Character, DfaState> transitions = new HashMap<>();
    public final boolean isFinal;

    public DfaState(Set<NfaState> nfaStates) {
        this.id = idCounter++;
        this.nfaStates = Collections.unmodifiableSet(nfaStates);

        this.isFinal = nfaStates.stream().anyMatch(s -> s.isFinal);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DfaState dfaState = (DfaState) o;

        if (this.nfaStates.isEmpty() && dfaState.nfaStates.isEmpty()) {
            return this.id == dfaState.id;
        }
        return nfaStates.equals(dfaState.nfaStates);
    }

    @Override
    public int hashCode() {
        if (nfaStates.isEmpty()) {
            return Objects.hash(id);
        }
        return Objects.hash(nfaStates);
    }


    private record StatePair(DfaState first, DfaState second) {}

    public static boolean isIsomorhic(DfaState first, DfaState second) {
        Map<DfaState, DfaState> map = new HashMap<>();
        Queue<StatePair> queue = new ArrayDeque<>();

        if (first.isFinal != second.isFinal) {
            return false;
        }

        map.put(first, second);
        queue.add(new StatePair(first, second));

        while (!queue.isEmpty()) {
            StatePair pair = queue.poll();
            DfaState state1 = pair.first();
            DfaState state2 = pair.second();

            Set<Character> symbols = new HashSet<>();
            symbols.addAll(state1.transitions.keySet());
            symbols.addAll(state2.transitions.keySet());

            for (char c : symbols) {
                DfaState next1 = state1.transitions.get(c);
                DfaState next2 = state2.transitions.get(c);

                if ((next1 == null) != (next2 == null)) {
                    return false;
                }
                if (next1 == null) {
                    continue;
                }

                if (map.containsKey(next1)) {
                    if (map.get(next1) != next2) {
                        return false;
                    }
                } else {
                    if (next1.isFinal != next2.isFinal) {
                        return false;
                    }
                    map.put(next1, next2);
                    queue.add(new StatePair(next1, next2));
                }
            }
        }
        return true;
    }


}