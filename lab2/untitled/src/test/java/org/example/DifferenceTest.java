package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DifferenceTest {
    @Test
    @DisplayName("Базовое вычитание: удаление конкретных слов")
    void testSimpleDifference() {
        Pattern p1 = Pattern.compile("(a|b){2}");
        Pattern p2 = Pattern.compile("ab");

        Pattern diff = Pattern.difference(p1, p2);

        assertTrue(diff.matches("aa"));
        assertTrue(diff.matches("ba"));
        assertTrue(diff.matches("bb"));

        assertFalse(diff.matches("ab"));
    }

    @Test
    @DisplayName("Вычитание из бесконечного языка (позитивное замыкание)")
    void testClosureDifference() {
        Pattern p1 = Pattern.compile("a+");
        Pattern p2 = Pattern.compile("aa");

        Pattern diff = Pattern.difference(p1, p2);

        assertTrue(diff.matches("a"));
        assertTrue(diff.matches("aaa"));
        assertTrue(diff.matches("aaaaa"));

        assertFalse(diff.matches("aa"));
        assertFalse(diff.matches(""));
    }

    @Test
    @DisplayName("Разность с использованием оператора 'любой символ' (.)")
    void testDotOperatorDifference() {
        Pattern p1 = Pattern.compile("...");
        Pattern p2 = Pattern.compile("z..");

        Pattern diff = Pattern.difference(p1, p2);

        assertTrue(diff.matches("abc"));
        assertTrue(diff.matches("123"));

        assertFalse(diff.matches("z12"));
        assertFalse(diff.matches("zap"));
    }

    @Test
    @DisplayName("Вычитание непересекающихся языков")
    void testDisjointLanguages() {
        Pattern p1 = Pattern.compile("cat");
        Pattern p2 = Pattern.compile("dog");

        Pattern diff = Pattern.difference(p1, p2);

        assertTrue(diff.matches("cat"));
        assertFalse(diff.matches("dog"));
    }

    @Test
    @DisplayName("Вычитание языка из самого себя (пустой результат)")
    void testSubtractSelf() {
        Pattern p1 = Pattern.compile("a{1,5}b+");
        Pattern diff = Pattern.difference(p1, p1);

        assertFalse(diff.matches("ab"));
        assertFalse(diff.matches("aaabbb"));
    }


    @Test
    @DisplayName("Сложные диапазоны r{x,y}")
    void testRangeDifference() {
        Pattern p1 = Pattern.compile("a{1,3}");
        Pattern p2 = Pattern.compile("a{2,5}");

        Pattern diff = Pattern.difference(p1, p2);

        assertTrue(diff.matches("a"));
        assertFalse(diff.matches("aa"));
        assertFalse(diff.matches("aaa"));
        assertFalse(diff.matches("aaaa"));
    }

    @Test
    @DisplayName("Разность с 'любым символом' и опциональностью")
    void testDifferenceDotAndOptional() {
        Pattern p1 = Pattern.compile(".");
        Pattern p2 = Pattern.compile("a?");
        Pattern diff = Pattern.difference(p1, p2);

        assertTrue(diff.matches("b"));
        assertTrue(diff.matches("c"));
        assertFalse(diff.matches("a"));
        assertFalse(diff.matches(""));
    }

    @Test
    @DisplayName("Разность с использованием квантификатора '?'")
    void testDifferenceWithOptional() {
        Pattern p1 = Pattern.compile("a?");
        Pattern p2 = Pattern.compile("a");
        Pattern diff1 = Pattern.difference(p1, p2);

        assertTrue(diff1.matches(""));
        assertFalse(diff1.matches("a"));
    }
}
