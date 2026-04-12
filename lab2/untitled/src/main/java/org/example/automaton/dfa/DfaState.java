package org.example.automaton.dfa;


import org.example.automaton.nfa.NfaState;
import java.util.*;

public class DfaState {
    private static int idCounter = 0;
    public final int id;

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
        if (o == null || getClass() != o.getClass()) return false;
        DfaState dfaState = (DfaState) o;
        return nfaStates.equals(dfaState.nfaStates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nfaStates);
    }
}