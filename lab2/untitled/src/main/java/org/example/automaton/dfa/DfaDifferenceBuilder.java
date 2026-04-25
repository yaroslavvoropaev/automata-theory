package org.example.automaton.dfa;

import java.util.*;

public class DfaDifferenceBuilder {

   private record StatePair(DfaState stateA, DfaState stateB) {}

    public DfaState buildDifference(DfaState startA, DfaState startB) {
        Map<StatePair, DfaState> stateMap = new HashMap<>();
        Queue<StatePair> queue = new ArrayDeque<>();

        StatePair startPair = new StatePair(startA, startB);
        DfaState newStart = new DfaState(startA.isFinal && (startB == null || !startB.isFinal));

        stateMap.put(startPair, newStart);
        queue.add(startPair);

        while (!queue.isEmpty()) {
            StatePair currentPair = queue.poll();
            DfaState currentNewState = stateMap.get(currentPair);

            DfaState qA = currentPair.stateA;
            DfaState qB = currentPair.stateB;

            if (qA == null) continue;

            Set<Character> combinedAlphabet = new HashSet<>(qA.transitions.keySet());
            if (qB != null) {
                combinedAlphabet.addAll(qB.transitions.keySet());
            }
            combinedAlphabet.remove('\uFFFF');

            for (char c : combinedAlphabet) {
                DfaState targetA = qA.transitions.get(c);
                if (targetA == null) {
                    targetA = qA.transitions.get('\uFFFF');
                }

                DfaState targetB = null;
                if (qB != null) {
                    targetB = qB.transitions.get(c);
                    if (targetB == null) {
                        targetB = qB.transitions.get('\uFFFF');
                    }
                }

                if (targetA != null) {
                    StatePair nextPair = new StatePair(targetA, targetB);
                    DfaState nextNewState = getOrCreateState(nextPair, stateMap, queue);
                    currentNewState.transitions.put(c, nextNewState);
                }
            }

            DfaState fallbackA = qA.transitions.get('\uFFFF');
            DfaState fallbackB = (qB != null) ? qB.transitions.get('\uFFFF') : null;

            if (fallbackA != null) {
                StatePair nextFallbackPair = new StatePair(fallbackA, fallbackB);
                DfaState nextNewState = getOrCreateState(nextFallbackPair, stateMap, queue);
                currentNewState.transitions.put('\uFFFF', nextNewState);
            }
        }

        return newStart;
    }

    private DfaState getOrCreateState(StatePair pair, Map<StatePair, DfaState> stateMap, Queue<StatePair> queue) {
        if (!stateMap.containsKey(pair)) {

            boolean isAFinal = (pair.stateA != null && pair.stateA.isFinal);
            boolean isBFinal = (pair.stateB != null && pair.stateB.isFinal);

            DfaState newState = new DfaState(isAFinal && !isBFinal);

            stateMap.put(pair, newState);
            queue.add(pair);
        }
        return stateMap.get(pair);
    }
}