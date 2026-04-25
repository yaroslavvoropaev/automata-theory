package org.example;

import org.example.matcher.MatchResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NfaTest {

    @Test
    @DisplayName("Проверка извлечения данных из именованной группы")
    void shouldCaptureNamedGroup() {
        Pattern pattern = Pattern.compile("(<user>(a|b)+)@gmail.com");
        MatchResult result = pattern.matchWithGroups("abbabab@gmail.com");

        assertTrue(result.isMatch());
        assertEquals("abbabab", result.get("user"));
    }

    @Test
    @DisplayName("Проверка работы с несколькими группами в НКА")
    void shouldCaptureMultipleGroups() {
        Pattern pattern = Pattern.compile("(<key>pr.ced?):(<value>10{1,3})");
        MatchResult result = pattern.matchWithGroups("price:100");

        assertTrue(result.isMatch());
        assertEquals("price", result.get("key"));
        assertEquals("100", result.get("value"));
    }

    @Test
    @DisplayName("Проверка отсутствия совпадения при наличии групп")
    void shouldNotMatchInvalidString() {
        Pattern pattern = Pattern.compile("(<id>(a|b){2,})");
        MatchResult result = pattern.matchWithGroups("a");

        assertFalse(result.isMatch());
    }


    @Test
    @DisplayName("Проверка сложных вложенных групп")
    void testNestedCaptureGroups() {
        Pattern pattern = Pattern.compile("(<outer>abc(<inner>def)ghi)");
        MatchResult result = pattern.matchWithGroups("abcdefghi");

        assertTrue(result.isMatch());

        assertEquals("abcdefghi", result.get("outer"));
        assertEquals("def", result.get("inner"));
    }

    @Test
    @DisplayName("Проверка вложенных групп с квантификаторами")
    void testNestedGroupsWithQuantifiers() {
        Pattern pattern = Pattern.compile("(<container>(<item>a|b)+)");
        MatchResult result = pattern.matchWithGroups("abbab");

        assertTrue(result.isMatch());

        assertEquals("abbab", result.get("container"));
        assertEquals("b", result.get("item"));
    }


    @Test
    @DisplayName("Операция ИЛИ (r1|r2) и Конкатенация с '-'")
    void testOrAndExplicitConcat() {
        Pattern pattern = Pattern.compile("(<test>abc|def-ghi)");

        assertTrue(pattern.matches("abc"));
        assertTrue(pattern.matches("defghi"));
        assertFalse(pattern.matches("def-ghi"));
    }

    @Test
    @DisplayName("Позитивное замыкание (+) и Опциональная часть (?)")
    void testQuantifiers() {
        Pattern pattern = Pattern.compile("(<test>a+b?)");

        assertTrue(pattern.matches("a"));
        assertTrue(pattern.matches("aaab"));
        assertFalse(pattern.matches("b"));
    }

    @Test
    @DisplayName("Любой символ (.) и Экранирование (&)")
    void testDotAndEscape() {
        Pattern pattern = Pattern.compile("(<test>.)&.");

        assertTrue(pattern.matches("x."));
        assertTrue(pattern.matches("5."));
        assertFalse(pattern.matches(".?"));
    }

    @Test
    @DisplayName("Повтор в диапазоне {x,y}")
    void testRangeRepeat() {
        Pattern p1 = Pattern.compile("(<test>a{2,3})");

        assertTrue(p1.matches("aa"));
        assertTrue(p1.matches("aaa"));
        assertFalse(p1.matches("a"));
        assertFalse(p1.matches("aaaa"));

        Pattern p2 = Pattern.compile("(<test>b{2})");

        assertTrue(p2.matches("bb"));
        assertFalse(p2.matches("b"));

        Pattern p3 = Pattern.compile("(<test>c{,})");

        assertTrue(p3.matches("cccccccccccc"));
        assertTrue(p3.matches(""));
        assertFalse(p3.matches("b"));
    }

    @Test
    @DisplayName("Повтор в диапазоне {,}")
    void testRangeInf() {
        Pattern p1 = Pattern.compile("(<test>a{,})");

        assertTrue(p1.matches("aa"));
        assertTrue(p1.matches("aaa"));
        assertTrue(p1.matches(""));
    }

    @Test
    @DisplayName("Приоритет операторов через скобки ( )")
    void testOperatorPriority() {
        Pattern pattern = Pattern.compile("(<test>(ab|.d)+)");

        assertTrue(pattern.matches("ababcd"));
        assertFalse(pattern.matches("abd"));
    }

}