package org.example.automaton.dfa;

import java.util.*;

public class KPathConverter {
    public static String convertToRegex(DfaState startState) {
        List<DfaState> states = new ArrayList<>();  // список всех достижимых состояний
        Set<DfaState> visited = new HashSet<>();    // посещенные сост, чтобы не добавлять пвторно в список
        Queue<DfaState> queue = new ArrayDeque<>();  // bfs

        queue.add(startState);
        visited.add(startState);

        while (!queue.isEmpty()) {
            DfaState current = queue.poll();
            states.add(current);
            for (DfaState target : current.transitions.values()) {
                if (visited.add(target)) {
                    queue.add(target);
                }
            }
        }

        int n = states.size();
        String[][][] R = new String[n + 1][n][n];   //   k,i,j

        for (int i = 0; i < n; i++) {        // инициализация баазового слоя  R[0][i][j]
            for (int j = 0; j < n; j++) {
                DfaState state_i = states.get(i);
                DfaState state_j = states.get(j);

                List<Character> transitions = new ArrayList<>();         // символы которые переводят из i в j
                for (Map.Entry<Character, DfaState> entry : state_i.transitions.entrySet()) {
                    if (entry.getValue().equals(state_j)) {
                        transitions.add(entry.getKey());
                    }
                }

                String expression = null;
                if (!transitions.isEmpty()) {
                    expression = escape(transitions.get(0));
                    for (int k = 1; k < transitions.size(); k++) {
                        expression = union(expression, escape(transitions.get(k)));
                    }
                }

                if (i == j) {
                    R[0][i][j] = union("", expression);   //петля
                } else {
                    R[0][i][j] = expression;
                }
            }
        }

        for (int k = 1; k <= n; k++) {         // k - какие промежуточные состояния разрешены
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    int k_idx = k - 1;           // новое разрешеннное промежуточное сост на это шаге
                    String r_ij = R[k - 1][i][j];         // путь без k_idx
                    String r_ik = R[k - 1][i][k_idx];       // путь от i до k_idx
                    String r_kk = R[k - 1][k_idx][k_idx];   // петля
                    String r_kj = R[k - 1][k_idx][j];       //  путь от k_idx до j

                    String pathViaK = concat(r_ik, concat(star(r_kk), r_kj));
                    R[k][i][j] = union(r_ij, pathViaK);
                }
            }
        }

        String finalRegex = null;
        int startIndex = states.indexOf(startState);

        for (int i = 0; i < n; i++) {
            if (states.get(i).isFinal) {
                finalRegex = union(finalRegex, R[n][startIndex][i]); // объединение путей от стратового состояния ко всем финальным
            }
        }
        return finalRegex == null ? "" : finalRegex;
    }

    private static String escape(char c) {
        if (c == '\uFFFF') {
            return ".";
        }

        String meta = "|+?.-{}()&";
        if (meta.indexOf(c) != -1) {
            return "&" + c;
        }
        return String.valueOf(c);
    }

    private static String union(String a, String b) {
        if (a == null) return b;
        if (b == null) return a;
        if (a.equals(b)) return a;
        if (a.isEmpty() && !b.isEmpty()) return "(" + b + ")?";
        if (b.isEmpty() && !a.isEmpty()) return "(" + a + ")?";
        if (a.isEmpty()) return "";
        return "(" + a + "|" + b + ")";
    }

    private static String concat(String a, String b) {
        if (a == null || b == null) return null;
        if (a.isEmpty()) return b;
        if (b.isEmpty()) return a;
        return "(" + a + b + ")";
    }

    private static String star(String a) {
        if (a == null || a.isEmpty()) return "";
        return "(" + a + "+)?";
    }
}