package org.example;

import org.example.automaton.*;
import org.example.lexer.Lexer;
import org.example.parser.Parser;
import org.example.parser.ast.Node;


import java.util.*;


public class Main {

    public static void main(String[] args) {
        /*String regexStr = "(ab){3,2}";

        Lexer lexer = new Lexer(regexStr);
        Parser parser = new Parser(lexer.tokenize());
        Node ast = parser.parse();

        ThompsonBuilder builder = new ThompsonBuilder();
        NfaFragment nfa = builder.build(ast);
        nfa.end().isFinal = true;

        String dotCode = builder.toDot(nfa);
        System.out.println(dotCode);
*/

        Pattern regex = Pattern.compile("(<group>a.)__(<group2>cd)");
        MatchResult result = regex.match("a__cd");

        if (result.matches()) {
            System.out.println(result.group("group2"));
        }
    }
}