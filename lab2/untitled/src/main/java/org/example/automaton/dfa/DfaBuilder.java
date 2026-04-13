package org.example.automaton.dfa;

import org.example.automaton.nfa.NfaFragment;
import org.example.automaton.nfa.NfaState;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import java.util.*;

public class DfaBuilder {
    public DfaState build(NfaFragment nfa) {
        nfa.end().isFinal = true;

        Set<NfaState> startClosure = epsilonClosure(Collections.singleton(nfa.start()));
        DfaState startDfaState = new DfaState(startClosure);

        Map<Set<NfaState>, DfaState> dfaStatesMap = new HashMap<>();   // ключ множество nfa состояние, значение соответсвующее dfa состояние
        dfaStatesMap.put(startClosure, startDfaState);

        Queue<DfaState> unmarked = new LinkedList<>();
        unmarked.add(startDfaState);

        while (!unmarked.isEmpty()) {
            DfaState currentDfaState = unmarked.poll();

            Set<Character> alphabet = getAlphabet(currentDfaState.nfaStates);

            for (char c : alphabet) {
                Set<NfaState> moveSet = move(currentDfaState.nfaStates, c);
                Set<NfaState> closureSet = epsilonClosure(moveSet);

                if (!closureSet.isEmpty()) {
                    DfaState nextDfaState = dfaStatesMap.get(closureSet);
                    if (nextDfaState == null) {
                        nextDfaState = new DfaState(closureSet);
                        dfaStatesMap.put(closureSet, nextDfaState);
                        unmarked.add(nextDfaState);
                    }
                    currentDfaState.transitions.put(c, nextDfaState);
                }
            }
        }

        return startDfaState;
    }

    private Set<NfaState> epsilonClosure(Set<NfaState> states) {
        Stack<NfaState> stack = new Stack<>();
        Set<NfaState> closure = new HashSet<>(states);
        stack.addAll(states);

        while (!stack.isEmpty()) {
            NfaState current = stack.pop();
            for (NfaState eps : current.epsilons) {
                if (closure.add(eps)) {
                    stack.push(eps);
                }
            }
        }
        return closure;
    }

    private Set<NfaState> move(Set<NfaState> states, char c) {
        Set<NfaState> result = new HashSet<>();
        for (NfaState s : states) {
            List<NfaState> targets = s.transitions.get(c);
            if (targets != null) {
                result.addAll(targets);
            }
        }
        return result;
    }

    private Set<Character> getAlphabet(Set<NfaState> states) {
        Set<Character> alphabet = new HashSet<>();
        for (NfaState s : states) {
            alphabet.addAll(s.transitions.keySet());
        }
        return alphabet;
    }

    public String toDot(DfaState startState) {
        StringBuilder dot = new StringBuilder();
        dot.append("digraph DFA {\n");
        dot.append("  rankdir=LR;\n");
        dot.append("  node [shape = circle, fontname=\"Arial\"];\n");
        dot.append("  secret_start [style=invis];\n");
        dot.append("  secret_start -> ").append(startState.id).append(";\n");

        Set<DfaState> visited = new HashSet<>();
        Queue<DfaState> queue = new LinkedList<>();
        queue.add(startState);
        visited.add(startState);

        while (!queue.isEmpty()) {
            DfaState s = queue.poll();

            if (s.isFinal) {
                dot.append("  ").append(s.id).append(" [shape=doublecircle];\n");
            } else {
                dot.append("  ").append(s.id).append(" [shape=circle];\n");
            }

            s.transitions.forEach((ch, target) -> {
                dot.append("  ").append(s.id).append(" -> ").append(target.id)
                        .append(" [label=\"").append(ch).append("\"];\n");

                if (visited.add(target)) queue.add(target);
            });
        }
        dot.append("}");
        return dot.toString();
    }

    public void toDotImage(DfaState dfa, String filename) {
        Path dotPath = Paths.get(filename + ".dot");
        Path pngPath = Paths.get(filename + ".png");

        try {
            Files.writeString(dotPath, toDot(dfa));
            ProcessBuilder pb = new ProcessBuilder("dot", "-Tpng", dotPath.toString(), "-o", pngPath.toString());
            pb.redirectErrorStream(true);
            Process p = pb.start();

            String errorOutput = new String(p.getInputStream().readAllBytes());

            int exitCode = p.waitFor();
            if (exitCode != 0) {
                throw new IOException("Graphviz error: " + errorOutput);
            }

            System.out.println("Граф сохранен в: " + pngPath.toAbsolutePath());

        } catch (IOException | InterruptedException e) {
            System.err.println("Ошибка генерации. Проверьте Graphviz: dot -V");
            e.printStackTrace();
        } finally {
            try {
                Files.deleteIfExists(dotPath);
            } catch (IOException ignored) {}
        }
    }
}