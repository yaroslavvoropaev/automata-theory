package org.example;

import org.example.lexer.Lexer;
import org.example.parser.Parser;
import org.example.parser.ast.Node;
import org.example.automaton.ThompsonBuilder;
import org.example.automaton.NfaFragment;
import org.example.automaton.Matcher;
import org.example.automaton.MatchResult;

public class Pattern {
    private final NfaFragment nfa;

    private Pattern(NfaFragment nfa) {
        this.nfa = nfa;
    }

    public static Pattern compile(String regex) {
        Lexer lexer = new Lexer(regex);
        Parser parser = new Parser(lexer.tokenize());
        Node ast = parser.parse();

        ThompsonBuilder builder = new ThompsonBuilder();
        NfaFragment fragment = builder.build(ast);

        fragment.end().isFinal = true;
        return new Pattern(fragment);
    }

    public MatchResult match(String text) {
        Matcher matcher = new Matcher(nfa.start(), text);
        return matcher.match();
    }
}