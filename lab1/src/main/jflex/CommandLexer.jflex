package com.project.jflex;
import org.parser.model.Token;

%%

%public
%class MyLexer
%type Token
%unicode
%line
%column

Whitespace = [ \t\f\r\n]+
Command    = [a-zA-Z0-9./]+
KeyChar    = [a-zA-Z0-9]

%%

"-" {KeyChar}+    {
    String keys = yytext().substring(1); // отрезаем первый дефис
    return new Token(Token.Type.KEY_SET, keys);
}

{Whitespace}      { return new Token(Token.Type.SPACE, yytext()); }

{Command}         { return new Token(Token.Type.COMMAND, yytext()); }

.                 { return new Token(Token.Type.ERROR, yytext()); }