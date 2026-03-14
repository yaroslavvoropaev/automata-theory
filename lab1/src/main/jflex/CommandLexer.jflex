package com.project.jflex;
import org.parser.model.Token;

%%

%public
%class MyLexer
%type Token


%state COMMAND
%state WAIT_KEYS
%state KEY_SET

Dash = "-"
Letter = [a-zA-Z0-9./]
Space = [ \t]+
EndOfLine = \n
Command = [a-zA-Z0-9./]+
KeyChar = [a-zA-Z0-9]

%%

<YYINITIAL> {
    {Space} { return new Token(Token.Type.SPACE, yytext()); }
    {Letter} {
        yybegin(COMMAND);
        return new Token(Token.Type.PART_COMMAND, yytext());
    }
    . { return new Token(Token.Type.ERROR, yytext()); }
}


<COMMAND> {
    {Space} {
        yybegin(WAIT_KEYS);
        return new Token(Token.Type.SPACE, yytext());
    }
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
    {Space} {
        return new Token(Token.Type.SPACE, yytext());
    }
    {Dash} {
        yybegin(KEY_SET);
        return new Token(Token.Type.DASH, yytext());
    }
    {EndOfLine} {
            return new Token(Token.Type.END_OF_LINE, yytext());
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


