package org.example.automaton.dfa;

import org.example.automaton.nfa.NfaState;

import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class DfaMinimizer {

    public DfaState minimize(DfaState startState) {
        Set<DfaState> allStates = new HashSet<>();  //все состояния
        Set<Character> alphabet = new HashSet<>();  // весь алфавит
        Queue<DfaState> queue = new ArrayDeque<>();

        queue.add(startState);
        allStates.add(startState);

        // собрали все состояния и все символы
        while (!queue.isEmpty()) {
            DfaState current = queue.poll();
            for (Map.Entry<Character, DfaState> entry : current.transitions.entrySet()) {
                alphabet.add(entry.getKey());
                DfaState target = entry.getValue();
                if (allStates.add(target)) {
                    queue.add(target);
                }
            }
        }

        // разделение на принимающие и непринимающие
        Set<DfaState> finalStates = new HashSet<>();
        Set<DfaState> nonFinalStates = new HashSet<>();
        for (DfaState state : allStates) {
            if (state.isFinal) {
                finalStates.add(state);
            } else {
                nonFinalStates.add(state);
            }
        }



        List<Set<DfaState>> partitions = new ArrayList<>();  // группы состояний
        if (!finalStates.isEmpty()) partitions.add(finalStates);
        if (!nonFinalStates.isEmpty()) partitions.add(nonFinalStates);


        boolean changed = true;
        while (changed) {
            changed = false;
            List<Set<DfaState>> newPartitions = new ArrayList<>();
            Map<DfaState, Integer> stateToPartitionIndex = new HashMap<>();     // состояние -> номер группы

            for (int i = 0; i < partitions.size(); i++) {
                for (DfaState state : partitions.get(i)) {
                    stateToPartitionIndex.put(state, i);
                }
            }

            for (Set<DfaState> group : partitions) {
                Map<Map<Character, Integer>, Set<DfaState>> splits = new HashMap<>();       // мапа для разделения груп на подгруппы

                for (DfaState state : group) {
                    Map<Character, Integer> signature = new HashMap<>();               //для каждого символаа — индекс группы назначения
                    for (char c : alphabet) {
                        DfaState target = state.transitions.get(c);
                        signature.put(c, target == null ? -1 : stateToPartitionIndex.get(target));
                    }
                    splits.computeIfAbsent(signature, k -> new HashSet<>()).add(state);
                }

                newPartitions.addAll(splits.values());
                if (splits.size() > 1) {
                    changed = true;
                }
            }
            partitions = newPartitions;
        }

        Map<Set<DfaState>, DfaState> groupToNewState = new HashMap<>();      // отображает множесвтво старых групп в новон состояние
        DfaState newStart = null;

        for (Set<DfaState> group : partitions) {
            Set<NfaState> mergedNfaStates = new HashSet<>();
            boolean isGroupFinal = false;

            for (DfaState state : group) {
                mergedNfaStates.addAll(state.nfaStates);
                if (state.isFinal) isGroupFinal = true;
            }

            DfaState newState = mergedNfaStates.isEmpty()
                    ? new DfaState(isGroupFinal)
                    : new DfaState(mergedNfaStates);

            groupToNewState.put(group, newState);
            if (group.contains(startState)) {
                newStart = newState;
            }
        }

        Map<DfaState, Set<DfaState>> stateToGroup = new HashMap<>();
        for (Set<DfaState> group : partitions) {
            for (DfaState s : group) {
                stateToGroup.put(s, group);
            }
        }

        for (Set<DfaState> group : partitions) {
            DfaState newState = groupToNewState.get(group);
            DfaState representative = group.iterator().next();

            for (Map.Entry<Character, DfaState> transition : representative.transitions.entrySet()) {
                char c = transition.getKey();
                DfaState target = transition.getValue();
                Set<DfaState> targetGroup = stateToGroup.get(target);
                if (targetGroup != null) {
                    newState.transitions.put(c, groupToNewState.get(targetGroup));
                }
            }
        }

        return newStart;
    }

    public String toDot(DfaState startState) {
        StringBuilder sb = new StringBuilder();
        sb.append("digraph DFA {\n");
        sb.append("    rankdir=LR;\n");
        sb.append("    node [shape = circle];\n");

        Set<DfaState> visited = new HashSet<>();
        Queue<DfaState> queue = new LinkedList<>();

        queue.add(startState);
        visited.add(startState);

        sb.append("    node [shape = none, label=\"\"]; start_node;\n");
        sb.append("    start_node -> ").append(startState.id).append(";\n");

        while (!queue.isEmpty()) {
            DfaState current = queue.poll();

            String shape = current.isFinal ? "doublecircle" : "circle";
            sb.append("    ").append(current.id)
                    .append(" [shape = ").append(shape)
                    .append(", label = \"q").append(current.id).append("\"];\n");

            for (Map.Entry<Character, DfaState> entry : current.transitions.entrySet()) {
                char symbol = entry.getKey();
                DfaState target = entry.getValue();

                sb.append("    ").append(current.id)
                        .append(" -> ").append(target.id)
                        .append(" [label = \"").append(symbol).append("\"];\n");

                if (!visited.contains(target)) {
                    visited.add(target);
                    queue.add(target);
                }
            }
        }

        sb.append("}");
        return sb.toString();
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

//            System.out.println("Граф сохранен в: " + pngPath.toAbsolutePath());

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