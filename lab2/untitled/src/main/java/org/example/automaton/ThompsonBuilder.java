package org.example.automaton;


import org.example.parser.ast.*;

import java.util.*;

public class ThompsonBuilder {


    private NfaFragment buildRepeat(Repeat repeat) {
        State finalStart = null;
        State currentEnd = null;

        for (int i = 0; i < repeat.min(); i++) {
            NfaFragment sub = build(repeat.child());
            if (finalStart == null) {
                finalStart = sub.start();
            } else {
                currentEnd.addEpsilon(sub.start());
            }
            currentEnd = sub.end();
        }


        if (repeat.max() == null) {
            NfaFragment loopPart = build(repeat.child());

            State starStart = new State();
            State starEnd = new State();

            starStart.addEpsilon(loopPart.start());
            starStart.addEpsilon(starEnd);

            loopPart.end().addEpsilon(loopPart.start());
            loopPart.end().addEpsilon(starEnd);

            if (finalStart == null) {
                finalStart = starStart;
            } else {
                currentEnd.addEpsilon(starStart);
            }
            currentEnd = starEnd;
        } else if (repeat.max() > repeat.min()) {
            for (int i = repeat.min(); i < repeat.max(); i++) {
                NfaFragment sub = build(repeat.child());
                State optStart = new State();
                State optEnd = new State();

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

        if (finalStart == null) {
            finalStart = new State();
            currentEnd = finalStart;
        }

        return new NfaFragment(finalStart, currentEnd);
    }

    public NfaFragment build(Node node) {

        if (node instanceof Literal literal) {
            State start = new State();
            State end = new State();
            start.addTransition(literal.value(), end);
            return new NfaFragment(start, end);
        }

        if (node instanceof Concat concat) {
            NfaFragment left = build(concat.left());
            NfaFragment right = build(concat.right());

            left.end().addEpsilon(right.start());
            return new NfaFragment(left.start(), right.end());
        }

        if (node instanceof Or or) {
            State start = new State();
            State end = new State();
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
            return buildRepeat(repeat);
        }


        if (node instanceof AnyChar) {
            State start = new State();
            State end = new State();
            start.addTransition('\uFFFF', end);
            return new NfaFragment(start, end);
        }


        if (node instanceof NamedGroup nameGroup) {
            NfaFragment inner = build(nameGroup.child());

            State start = new State();
            State end = new State();

            start.addEpsilon(inner.start());
            inner.end().addEpsilon(end);

            start.groupInfo.put(nameGroup.name(), true);
            end.groupInfo.put(nameGroup.name(), false);

            return new NfaFragment(start, end);
        }

        throw new UnsupportedOperationException("Node " + node.getClass().getSimpleName() + "not realized yet");
    }



    public String toDot(NfaFragment nfa) {
        StringBuilder dot = new StringBuilder();
        dot.append("digraph NFA {\n");
        dot.append("  rankdir=LR;\n");
        dot.append("  node [shape = circle, fontname=\"Arial\"];\n");

        dot.append("  ").append(nfa.end().id).append(" [shape = doublecircle];\n");
        dot.append("  secret_start [style=invis];\n");
        dot.append("  secret_start -> ").append(nfa.start().id).append(";\n");

        Set<State> visited = new HashSet<>();
        Queue<State> queue = new LinkedList<>();
        queue.add(nfa.start());
        visited.add(nfa.start());

        while (!queue.isEmpty()) {
            State s = queue.poll();

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
                for (State target : targets) {
                    dot.append("  ").append(s.id).append(" -> ").append(target.id)
                            .append(" [label=\"").append(ch).append("\"];\n");
                    if (visited.add(target)) queue.add(target);
                }
            });

            for (State eps : s.epsilons) {
                dot.append("  ").append(s.id).append(" -> ").append(eps.id)
                        .append(" [label=\"&epsilon;\", style=dashed];\n");
                if (visited.add(eps)) queue.add(eps);
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

}