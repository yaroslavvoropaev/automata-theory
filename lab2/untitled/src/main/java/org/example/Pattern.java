package org.example;

import org.example.automaton.dfa.*;
import org.example.lexer.Lexer;
import org.example.lexer.Token;
import org.example.parser.Parser;
import org.example.parser.ast.Node;
import org.example.automaton.nfa.NfaBuilder;
import org.example.automaton.nfa.NfaFragment;
import org.example.matcher.MatchResult;
import org.example.matcher.DfaMatcher;
import org.example.matcher.NfaMatcher;

import java.util.List;


public class Pattern {
    private final NfaFragment nfa;
    public DfaState minDfaStart;
    private final boolean hasGroups;

    private Pattern(String regex) {
        Lexer lexer = new Lexer(regex);
        List<Token> tokens = lexer.tokenize();

        Parser parser = new Parser(tokens);
        Node ast = parser.parse();
        parser.toDotImage(ast, "my_ast_graph");
        this.hasGroups = parser.hasNamedGroups();

        NfaBuilder nfaBuilder = new NfaBuilder();
        this.nfa = nfaBuilder.build(ast);
        nfaBuilder.toDotImage(nfa, "my_nfa_graph");

        this.nfa.end().isFinal = true;

        if (!hasGroups) {
            DfaBuilder dfaBuilder = new DfaBuilder();
            DfaState dfa = dfaBuilder.build(nfa);
            dfaBuilder.toDotImage(dfa, "my_dfa_graph");

            DfaMinimizer minimizer = new DfaMinimizer();
            minDfaStart = minimizer.minimize(dfa);
            minimizer.toDotImage(minDfaStart, "my_min_dfa_graph");

        }
    }

    public static Pattern compile(String regex) {
        return new Pattern(regex);
    }


    public boolean matches(String text) {
        if (hasGroups) {
            return new NfaMatcher(nfa).match(text).isMatch();
        } else {
            return new DfaMatcher(minDfaStart).match(text);
        }
    }


    public MatchResult matchWithGroups(String text) {
        if (hasGroups) {
            return new NfaMatcher(nfa).match(text);
        } else {
            boolean isMatch = new DfaMatcher(minDfaStart).match(text);
            return new MatchResult(isMatch, null);
        }
    }


    public String restoreRegex() {
        if (hasGroups) {
            throw new IllegalStateException("Cannot restore regex with named groups accurately from automaton.");
        }
        return org.example.automaton.dfa.KPathConverter.convertToRegex(minDfaStart);
    }

    public void invert() {
        if (hasGroups) {
            throw new UnsupportedOperationException("The inversion operation is not supported for expressions with capture groups.");
        }

        DfaInverter inverter = new DfaInverter();
        this.minDfaStart = inverter.invert(this.minDfaStart);

        DfaMinimizer minimizer = new DfaMinimizer();
        this.minDfaStart = minimizer.minimize(this.minDfaStart);
        minimizer.toDotImage(this.minDfaStart, "my_inverted_min_dfa_graph");
    }


    private Pattern(DfaState minDfaStart) {
        this.nfa = null;
        this.minDfaStart = minDfaStart;
        this.hasGroups = false;
    }

    public static Pattern difference(Pattern p1, Pattern p2) {
        if (p1.hasGroups || p2.hasGroups) {
            throw new UnsupportedOperationException("The difference operation is not supported for expressions with capture groups");
        }

        DfaDifferenceBuilder diffBuilder = new DfaDifferenceBuilder();
        DfaState diffStart = diffBuilder.buildDifference(p1.minDfaStart, p2.minDfaStart);

        DfaMinimizer minimizer = new DfaMinimizer();
        DfaState minDiffStart = minimizer.minimize(diffStart);
        minimizer.toDotImage(minDiffStart, "my_difference_min_dfa_graph");

        return new Pattern(minDiffStart);
    }



}