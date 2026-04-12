package org.example.automaton.dfa;

import org.example.automaton.nfa.NfaState;

import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class DfaMinimizer {

    public DfaState minimize(DfaState startState) {
        Set<DfaState> allStates = new HashSet<>();
        Set<Character> alphabet = new HashSet<>();
        Queue<DfaState> queue = new LinkedList<>();

        queue.add(startState);
        allStates.add(startState);

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

        Set<DfaState> finalStates = new HashSet<>();
        Set<DfaState> nonFinalStates = new HashSet<>();
        for (DfaState s : allStates) {
            if (s.isFinal) finalStates.add(s);
            else nonFinalStates.add(s);
        }

        List<Set<DfaState>> partitions = new ArrayList<>();
        if (!finalStates.isEmpty()) partitions.add(finalStates);
        if (!nonFinalStates.isEmpty()) partitions.add(nonFinalStates);

        boolean changed = true;
        while (changed) {
            changed = false;
            List<Set<DfaState>> newPartitions = new ArrayList<>();

            Map<DfaState, Integer> stateToPartitionIndex = new HashMap<>();
            for (int i = 0; i < partitions.size(); i++) {
                for (DfaState s : partitions.get(i)) {
                    stateToPartitionIndex.put(s, i);
                }
            }

            for (Set<DfaState> group : partitions) {
                Map<Map<Character, Integer>, Set<DfaState>> splits = new HashMap<>();

                for (DfaState state : group) {
                    Map<Character, Integer> signature = new HashMap<>();
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

        Map<Set<DfaState>, DfaState> newStatesMap = new HashMap<>();
        DfaState newStart = null;

        for (Set<DfaState> group : partitions) {
            Set<NfaState> mergedNfaStates = new HashSet<>();
            for (DfaState s : group) {
                mergedNfaStates.addAll(s.nfaStates);
            }

            DfaState newState = new DfaState(mergedNfaStates);
            newStatesMap.put(group, newState);

            if (group.contains(startState)) {
                newStart = newState;
            }
        }

        for (Set<DfaState> group : partitions) {
            DfaState newState = newStatesMap.get(group);
            DfaState representative = group.iterator().next();

            for (Map.Entry<Character, DfaState> transition : representative.transitions.entrySet()) {
                char c = transition.getKey();
                DfaState target = transition.getValue();


                for (Set<DfaState> targetGroup : partitions) {
                    if (targetGroup.contains(target)) {
                        newState.transitions.put(c, newStatesMap.get(targetGroup));
                        break;
                    }
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


    public void toDotImage(DfaState startState, String filename) {
        try {
            String dotContent = toDot(startState);

            String dotFile = filename + ".dot";
            Files.write(Paths.get(dotFile), dotContent.getBytes());

            ProcessBuilder pb = new ProcessBuilder("dot", "-Tpng", dotFile, "-o", filename + ".png");
            pb.inheritIO();
            Process process = pb.start();
            process.waitFor();


            System.out.println("Граф сохранён: " + filename + ".png");

        } catch (Exception e) {
            System.err.println("Ошибка при создании изображения: " + e.getMessage());
            e.printStackTrace();
        }
    }
}