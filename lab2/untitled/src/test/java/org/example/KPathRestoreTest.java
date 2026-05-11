package org.example;

import org.example.automaton.dfa.DfaState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KPathRestoreTest {

    @Test
    @DisplayName("Простая конкатенация")
    void testSimpleConcatenation() {
        Pattern pattern = Pattern.compile("abc");
        String restored = pattern.restoreRegex();

        Pattern restoredPattern = Pattern.compile(restored);
        assertTrue(restoredPattern.matches("abc"));
        assertFalse(restoredPattern.matches("abd"));

        assertTrue(DfaState.isIsomorhic(pattern.minDfaStart, restoredPattern.minDfaStart));


    }

    @Test
    @DisplayName("Операция ИЛИ")
    void testUnion() {
        Pattern pattern = Pattern.compile("a|b");
        String restored = pattern.restoreRegex();

        Pattern restoredPattern = Pattern.compile(restored);
        assertTrue(restoredPattern.matches("a"));
        assertTrue(restoredPattern.matches("b"));
        assertFalse(restoredPattern.matches("c"));
    }

    @Test
    @DisplayName("Замыкание")
    void testPositiveClosure() {

        Pattern pattern = Pattern.compile("a+");
        String restored = pattern.restoreRegex();

        Pattern restoredPattern = Pattern.compile(restored);
        assertTrue(restoredPattern.matches("a"));
        assertTrue(restoredPattern.matches("aaaaa"));

        assertTrue(DfaState.isIsomorhic(pattern.minDfaStart, restoredPattern.minDfaStart));

    }

    @Test
    @DisplayName("Опциональность")
    void testOptional() {
        Pattern pattern = Pattern.compile("a?");
        String restored = pattern.restoreRegex();

        Pattern restoredPattern = Pattern.compile(restored);
        assertTrue(restoredPattern.matches(""));
        assertTrue(restoredPattern.matches("a"));

        assertTrue(DfaState.isIsomorhic(pattern.minDfaStart, restoredPattern.minDfaStart));

    }

    @Test
    @DisplayName("Сложный цикл и вложенность")
    void testComplexLoops() {
        Pattern pattern = Pattern.compile("(ab)+c");
        String restored = pattern.restoreRegex();

        Pattern restoredPattern = Pattern.compile(restored);
        assertTrue(restoredPattern.matches("abc"));
        assertTrue(restoredPattern.matches("abababc"));
        assertFalse(restoredPattern.matches("ac"));

        assertTrue(DfaState.isIsomorhic(pattern.minDfaStart, restoredPattern.minDfaStart));

    }

    @Test
    @DisplayName("Любой символ: .")
    void testAnyChar() {
        Pattern pattern = Pattern.compile(".");
        String restored = pattern.restoreRegex();

        Pattern restoredPattern = Pattern.compile(restored);
        assertTrue(restoredPattern.matches("f"));
        assertTrue(restoredPattern.matches("7"));
        assertTrue(restoredPattern.matches("&"));

        assertTrue(DfaState.isIsomorhic(pattern.minDfaStart, restoredPattern.minDfaStart));

    }

    @Test
    @DisplayName("Экранирование метасимволов")
    void testEscaping() {
        Pattern pattern = Pattern.compile("&+");
        String restored = pattern.restoreRegex();

        assertTrue(restored.contains("&+"));
        assertTrue(Pattern.compile(restored).matches("+"));

    }


    @Test
    @DisplayName("Фиксированный повтор: a{3}")
    void testFixedRepeat() {
        Pattern pattern = Pattern.compile("a{3,3}");
        String restored = pattern.restoreRegex();

        Pattern restoredPattern = Pattern.compile(restored);
        assertTrue(restoredPattern.matches("aaa"));
        assertFalse(restoredPattern.matches("aa"));
        assertFalse(restoredPattern.matches("aaaa"));

        assertTrue(DfaState.isIsomorhic(pattern.minDfaStart, restoredPattern.minDfaStart));
    }

    @Test
    @DisplayName("Закрытый диапазон: a{2,4}")
    void testClosedRange() {
        Pattern pattern = Pattern.compile("a{2,4}");
        String restored = pattern.restoreRegex();

        Pattern restoredPattern = Pattern.compile(restored);
        assertFalse(restoredPattern.matches("a"));
        assertTrue(restoredPattern.matches("aa"));
        assertTrue(restoredPattern.matches("aaa"));
        assertTrue(restoredPattern.matches("aaaa"));
        assertFalse(restoredPattern.matches("aaaaa"));

        assertTrue(DfaState.isIsomorhic(pattern.minDfaStart, restoredPattern.minDfaStart));
    }

    @Test
    @DisplayName("Открытая верхняя граница: a{2,}")
    void testOpenUpperRange() {
        Pattern pattern = Pattern.compile("a{2,}");
        String restored = pattern.restoreRegex();

        Pattern restoredPattern = Pattern.compile(restored);
        assertFalse(restoredPattern.matches("a"));
        assertTrue(restoredPattern.matches("aa"));
        assertTrue(restoredPattern.matches("aaaaa"));

        assertTrue(DfaState.isIsomorhic(pattern.minDfaStart, restoredPattern.minDfaStart));
    }

    @Test
    @DisplayName("Открытая нижняя граница: a{,2}")
    void testOpenLowerRange() {
        Pattern pattern = Pattern.compile("a{,2}");
        String restored = pattern.restoreRegex();

        Pattern restoredPattern = Pattern.compile(restored);

        assertTrue(restoredPattern.matches(""));
        assertTrue(restoredPattern.matches("a"));
        assertTrue(restoredPattern.matches("aa"));
        assertFalse(restoredPattern.matches("aaa"));

        assertTrue(DfaState.isIsomorhic(pattern.minDfaStart, restoredPattern.minDfaStart));
    }
}
