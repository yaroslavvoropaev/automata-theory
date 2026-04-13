package org.example.automaton.nfa;


import org.example.parser.ast.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.io.IOException;
import java.nio.file.Paths;

public class NfaBuilder {

    private NfaFragment buildRepeatHard(Repeat repeat) {
        NfaState finalStart = null;
        NfaState currentEnd = null;

        for (int i = 0; i < repeat.min(); i++) {        // построение обязательных повторений a{3}
            NfaFragment sub = build(repeat.child());
            if (finalStart == null) {
                finalStart = sub.start();
            } else {
                currentEnd.addEpsilon(sub.start());
            }
            currentEnd = sub.end();
        }


        if (repeat.max() == null) {                     //  построение бесконечности a{3,}
            NfaFragment loopPart = build(repeat.child());

            NfaState start = new NfaState();
            NfaState end = new NfaState();

            start.addEpsilon(loopPart.start());
            start.addEpsilon(end);

            loopPart.end().addEpsilon(loopPart.start());
            loopPart.end().addEpsilon(end);

            if (finalStart == null) {
                finalStart = start;
            } else {
                currentEnd.addEpsilon(start);
            }
            currentEnd = end;
        } else if (repeat.max() > repeat.min()) {
            for (int i = repeat.min(); i < repeat.max(); i++) {// a{3,5}

                NfaFragment sub = build(repeat.child());
                NfaState optStart = new NfaState();
                NfaState optEnd = new NfaState();

                optStart.addEpsilon(sub.start());
                optStart.addEpsilon(optEnd);
                sub.end().addEpsilon(optEnd);

                if (finalStart == null) {
                    finalStart = optStart;
                } else {
                    currentEnd.addEpsilon(optStart);
                }
                currentEnd = optEnd;
            }
        }

        if (finalStart == null) {         // a{0}
            finalStart = new NfaState();
            currentEnd = finalStart;
        }

        return new NfaFragment(finalStart, currentEnd);
    }


    private NfaFragment buildRepeatEasy(Repeat repeat) {
        NfaFragment sub = build(repeat.child());

        if (repeat.min() == 1 && repeat.max() == null) {   // a+
            NfaState start = new NfaState();
            NfaState end = new NfaState();

            start.addEpsilon(sub.start());
            sub.end().addEpsilon(sub.start());
            sub.end().addEpsilon(end);

            return new NfaFragment(start, end);
        }

        if (repeat.min() == 0 && repeat.max() == 1) {      //a?
            sub.start().addEpsilon(sub.end());
            return sub;
        }

        return buildRepeatHard(repeat);
    }

    public NfaFragment build(Node node) {

        if (node instanceof Literal literal) {
            NfaState start = new NfaState();
            NfaState end = new NfaState();
            start.addTransition(literal.value(), end);
            return new NfaFragment(start, end);
        }


        if (node instanceof Concat concat) {
            NfaFragment left = build(concat.left());
            NfaFragment right = build(concat.right());

            left.end().epsilons.addAll(right.start().epsilons);

            right.start().transitions.forEach((c, targets) -> {
                for (NfaState target : targets) {
                    left.end().addTransition(c, target);
                }
            });

            left.end().groupInfo.putAll(right.start().groupInfo);

            return new NfaFragment(left.start(), right.end());
        }

        if (node instanceof Or or) {
            NfaState start = new NfaState();
            NfaState end = new NfaState();

            NfaFragment left = build(or.left());
            NfaFragment right = build(or.right());

            start.addEpsilon(left.start());
            start.addEpsilon(right.start());

            left.end().addEpsilon(end);
            right.end().addEpsilon(end);

            return new NfaFragment(start, end);
        }

        if (node instanceof Group g) {
            return build(g.child());
        }

        if (node instanceof Repeat repeat) {
            return buildRepeatEasy(repeat);
        }

        if (node instanceof AnyChar) {
            NfaState start = new NfaState();
            NfaState end = new NfaState();
            start.addTransition('\uFFFF', end);
            return new NfaFragment(start, end);
        }

        if (node instanceof NamedGroup nameGroup) {
            NfaFragment inner = build(nameGroup.child());

            NfaState start = new NfaState();
            NfaState end = new NfaState();

            start.addEpsilon(inner.start());
            inner.end().addEpsilon(end);

            start.groupInfo.put(nameGroup.name(), true);
            end.groupInfo.put(nameGroup.name(), false);

            return new NfaFragment(start, end);
        }

        throw new UnsupportedOperationException("Node " + node.getClass().getSimpleName() + "not realized yet");
    }




    public String toDot(NfaFragment nfa) {
        nfa.end().isFinal = true;
        StringBuilder dot = new StringBuilder();
        dot.append("digraph NFA {\n");
        dot.append("  rankdir=LR;\n");
        dot.append("  node [shape = circle, fontname=\"Arial\"];\n");

        dot.append("  ").append(nfa.end().id).append(" [shape = doublecircle];\n");
        dot.append("  secret_start [style=invis];\n");
        dot.append("  secret_start -> ").append(nfa.start().id).append(";\n");

        Set<NfaState> visited = new HashSet<>();
        Queue<NfaState> queue = new LinkedList<>();
        queue.add(nfa.start());
        visited.add(nfa.start());

        while (!queue.isEmpty()) {
            NfaState s = queue.poll();

            if (s.isFinal) {
                dot.append("  ").append(s.id).append(" [shape=doublecircle];\n");
            } else {
                dot.append("  ").append(s.id).append(" [shape=circle];\n");
            }

            if (!s.groupInfo.isEmpty()) {
                dot.append("  ").append(s.id)
                        .append(" [color=blue, fontcolor=blue, xlabel=\"")
                        .append(formatGroupInfo(s.groupInfo))
                        .append("\"];\n");
            }


            s.transitions.forEach((ch, targets) -> {
                for (NfaState target : targets) {
                    dot.append("  ").append(s.id).append(" -> ").append(target.id)
                            .append(" [label=\"").append(ch).append("\"");

                    if (visited.contains(target)) {
                        dot.append(", constraint=false");
                    } else {
                        visited.add(target);
                        queue.add(target);
                    }
                    dot.append("];\n");
                }
            });

            for (NfaState eps : s.epsilons) {
                dot.append("  ").append(s.id).append(" -> ").append(eps.id);

                if (visited.contains(eps)) {
                    dot.append(" [label=\"&epsilon;\", style=dashed, constraint=false];\n");
                } else {
                    dot.append(" [label=\"&epsilon;\", style=dashed];\n");
                    visited.add(eps);
                    queue.add(eps);
                }
            }
        }

        dot.append("}");
        return dot.toString();
    }

    private String formatGroupInfo(Map<String, Boolean> info) {
        StringBuilder sb = new StringBuilder();
        info.forEach((name, isStart) -> sb.append(isStart ? "START:" : "END:").append(name).append(" "));
        return sb.toString().trim();
    }

    public void toDotImage(NfaFragment nfa, String filename) {
        Path dotPath = Paths.get(filename + ".dot");
        Path pngPath = Paths.get(filename + ".png");

        try {
            Files.writeString(dotPath, toDot(nfa));
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