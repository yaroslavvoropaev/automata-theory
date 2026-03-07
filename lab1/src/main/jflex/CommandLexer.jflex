package com.commandparser.implementations.jflex.generated;

import java.util.*;

%%

%class CommandLexer
%public
%type Token


%state COMMAND
%state WAIT_KEY
%state KEY_GROUP
%state ERROR


%{
    private StringBuilder commandName = new StringBuilder();
    private StringBuilder currentKeyGroup = new StringBuilder();
    private Set<Character> keys = new TreeSet<>();
    private Map<String, Set<Character>> statistics = new HashMap<>();
    private boolean isValid = true;
    private String currentCommand = null;

    public enum Token {
        COMMAND, KEY_GROUP, WHITESPACE, ERROR, EOF
    }

    public void reset() {
        yybegin(YYINITIAL);
        commandName.setLength(0);
        currentKeyGroup.setLength(0);
        keys.clear();
        isValid = true;
        currentCommand = null;
    }

    public Map<String, Set<Character>> getStatistics() {
        return statistics;
    }

    public boolean isValid() {
        return isValid;
    }

    private void saveCommand() {
        if (currentCommand != null && !currentCommand.isEmpty()) {
            Set<Character> commandKeys = statistics.computeIfAbsent(
                currentCommand, k -> new TreeSet<>());
            commandKeys.addAll(keys);
        }
    }
%}

Letter = [a-zA-Z]
Digit = [0-9]
CommandChar = {Letter} | {Digit} | [./]
KeyChar = {Letter} | {Digit}
Whitespace = [ \t]+

%%

<YYINITIAL> {
    {CommandChar}+ {
        commandName.append(yytext());
        yybegin(COMMAND);
        return Token.COMMAND;
    }

    {Whitespace} {

    }

    [^] {
        yybegin(ERROR);
        isValid = false;
        return Token.ERROR;
    }
}

<COMMAND> {
    {Whitespace} {
        currentCommand = commandName.toString();
        commandName.setLength(0);
        yybegin(WAIT_KEY);
        return Token.WHITESPACE;
    }

    <<EOF>> {
        currentCommand = commandName.toString();
        saveCommand();
        keys.clear();
        yybegin(YYINITIAL);
        return Token.EOF;
    }

    [^] {
        yybegin(ERROR);
        isValid = false;
        return Token.ERROR;
    }
}

<WAIT_KEY> {
    "-" {
        currentKeyGroup.setLength(0);
        yybegin(KEY_GROUP);
    }

    {Whitespace} {

    }

    <<EOF>> {
        saveCommand();
        keys.clear();
        yybegin(YYINITIAL);
        return Token.EOF;
    }

    [^] {
        yybegin(ERROR);
        isValid = false;
        return Token.ERROR;
    }
}

<KEY_GROUP> {
    {KeyChar}+ {
        String group = yytext();
        for (char c : group.toCharArray()) {
            keys.add(c);
        }
        yybegin(WAIT_KEY);
        return Token.KEY_GROUP;
    }

    {Whitespace} {
        yybegin(WAIT_KEY);
    }

    <<EOF>> {
        saveCommand();
        keys.clear();
        yybegin(YYINITIAL);
        return Token.EOF;
    }

    [^] {
        yybegin(ERROR);
        isValid = false;
        return Token.ERROR;
    }
}

<ERROR> {
    [^\n]* {
        isValid = false;
        return Token.ERROR;
    }
}

[^] {
    yybegin(ERROR);
    isValid = false;
    return Token.ERROR;
}