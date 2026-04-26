package org.example.automaton.dfa;

import java.util.*;

public class KPathConverter {

    public static String convertToRegex(DfaState startState) {
        List<DfaState> states = new ArrayList<>();
        Set<DfaState> visited = new HashSet<>();
        Queue<DfaState> queue = new ArrayDeque<>();

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
        String[][][] R = new String[n + 1][n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                DfaState s_i = states.get(i);
                DfaState s_j = states.get(j);

                List<Character> transitions = new ArrayList<>();
                for (Map.Entry<Character, DfaState> entry : s_i.transitions.entrySet()) {
                    if (entry.getValue().equals(s_j)) {
                        transitions.add(entry.getKey());
                    }
                }

                String expr = null;
                if (!transitions.isEmpty()) {
                    expr = escape(transitions.get(0));
                    for (int t = 1; t < transitions.size(); t++) {
                        expr = union(expr, escape(transitions.get(t)));
                    }
                }

                if (i == j) {
                    R[0][i][j] = union("", expr);
                } else {
                    R[0][i][j] = expr;
                }
            }
        }

        for (int k = 1; k <= n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    int k_idx = k - 1;
                    String r_ij = R[k - 1][i][j];
                    String r_ik = R[k - 1][i][k_idx];
                    String r_kk = R[k - 1][k_idx][k_idx];
                    String r_kj = R[k - 1][k_idx][j];

                    String pathViaK = concat(r_ik, concat(star(r_kk), r_kj));
                    R[k][i][j] = union(r_ij, pathViaK);
                }
            }
        }

        String finalRegex = null;
        int startIndex = states.indexOf(startState);

        for (int i = 0; i < n; i++) {
            if (states.get(i).isFinal) {
                finalRegex = union(finalRegex, R[n][startIndex][i]);
            }
        }

        return finalRegex == null ? "" : finalRegex;
    }

    private static String escape(char c) {
        if (c == '\uFFFF') return ".";

        String metas = "|+?.-{}<>()&";
        if (metas.indexOf(c) != -1) {
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