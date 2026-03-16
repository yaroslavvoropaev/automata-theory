package com.project.jflex;
import org.parser.model.Token;

%%

%public
%class MyLexer
%type Token


%state COMMAND
%state WAIT_KEYS
%state DASH
%state KEY_SET

Dash = "-"
Letter = [a-zA-Z0-9./]
Space = [ \t]+
EndOfLine = \n
Command = [a-zA-Z0-9./]+
KeyChar = [a-zA-Z0-9]

%%

<YYINITIAL> {
    {Space}+ { }
    {Letter} {
        yybegin(COMMAND);
        return new Token(Token.Type.PART_COMMAND, yytext());
    }

    . { return new Token(Token.Type.ERROR, yytext()); }
}


<COMMAND> {
    {Space}+ { }
    {Command} {
        yybegin(WAIT_KEYS);
        return new Token(Token.Type.PART_COMMAND, yytext());
    }
    {EndOfLine} {
        return new Token(Token.Type.END_OF_LINE, yytext());
    }
    . { return new Token(Token.Type.ERROR, yytext()); }
}

<WAIT_KEYS> {
    {Space}+ {
        yybegin(DASH);
    }
    {EndOfLine} {
        return new Token(Token.Type.END_OF_LINE, yytext());
    }
    . { return new Token(Token.Type.ERROR, yytext()); }
}

<DASH> {
    {Dash} {
        yybegin(KEY_SET);
    }
    . { return new Token(Token.Type.ERROR, yytext()); }
}

<KEY_SET> {
    {KeyChar}+ {
        yybegin(WAIT_KEYS);
        return new Token(Token.Type.KEY_SET, yytext());
    }
    . { return new Token(Token.Type.ERROR, yytext()); }
}

