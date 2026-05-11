package org.example;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DfaTest {
    @Test
    @DisplayName("Комбинация: Выбор, Группировка и Позитивное замыкание")
    void testOrWithPositiveClosure() {
        Pattern pattern = Pattern.compile("(a|b)+c");

        assertTrue(pattern.matches("ac"));
        assertTrue(pattern.matches("bc"));
        assertTrue(pattern.matches("abababc"));
        assertFalse(pattern.matches("c"));
        assertFalse(pattern.matches("abc d"));
    }

    @Test
    @DisplayName("Комбинация: Опциональность и Диапазон повторений")
    void testOptionalWithRange() {
        Pattern pattern = Pattern.compile("a?b{2,3}");

        assertTrue(pattern.matches("bb"));
        assertTrue(pattern.matches("bbb"));
        assertTrue(pattern.matches("abb"));
        assertTrue(pattern.matches("abbb"));
        assertFalse(pattern.matches("a"));
        assertFalse(pattern.matches("abbbb"));
    }


    @Test
    @DisplayName("Комбинация: Любой символ и Явная конкатенация")
    void testDotWithExplicitConcat() {
        Pattern pattern = Pattern.compile(".-a-b+");

        assertTrue(pattern.matches("xab"));
        assertTrue(pattern.matches("!abbb"));
        assertTrue(pattern.matches(" ab"));
        assertFalse(pattern.matches("ab"));
    }

    @Test
    @DisplayName("Сложная комбинация с экранированием")
    void testComplexEscapeCombo() {
        Pattern pattern = Pattern.compile(".+|&?");

        assertTrue(pattern.matches("any"));
        assertTrue(pattern.matches("?"));
        assertTrue(pattern.matches("+++"));
        assertFalse(pattern.matches("?any"));
    }

    @Test
    @DisplayName("Вложенные повторы и границы диапазона")
    void testNestedRanges() {
        Pattern pattern = Pattern.compile("(ab?){2,}");

        assertTrue(pattern.matches("abab"));
        assertTrue(pattern.matches("aa"));
        assertTrue(pattern.matches("aba"));
        assertTrue(pattern.matches("aaaaa"));
        assertFalse(pattern.matches("a"));
    }

    @Test
    @DisplayName("Диапазон с отсутствующими границами")
    void testOpenRange() {
        Pattern p1 = Pattern.compile("a{2,}");
        assertTrue(p1.matches("aa"));
        assertTrue(p1.matches("aaaaa"));
        assertFalse(p1.matches("a"));


        Pattern p2 = Pattern.compile("b{,2}");
        assertTrue(p2.matches(""));
        assertTrue(p2.matches("b"));
        assertTrue(p2.matches("bb"));
        assertFalse(p2.matches("bbb"));
    }

    @Test
    @DisplayName("Полный фарш: все операции вместе")
    void testKitchenSink() {
        Pattern pattern = Pattern.compile("(a|b)?-c+-&.-d{1,2}");

        assertTrue(pattern.matches("ac.d"));
        assertTrue(pattern.matches("bcccc.dd"));
        assertTrue(pattern.matches("ccc.d"));
        assertFalse(pattern.matches("abc.d"));
    }
}