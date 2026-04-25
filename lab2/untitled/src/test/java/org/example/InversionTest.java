package org.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InversionTest {

    @Test
    @DisplayName("Проверка базовой инверсии простзы строк")
    void testSimpleStringInversion() {
        Pattern pattern = Pattern.compile("abc");
        pattern.invert();

        assertTrue(pattern.matches("cba"));
        assertFalse(pattern.matches("abc"));

        pattern = Pattern.compile("a");
        pattern.invert();

        assertTrue(pattern.matches("a"));
    }

    @Test
    @DisplayName("Инверсия позитивного замыкания (r+)")
    void testPositiveClosureInversion() {
        Pattern pattern = Pattern.compile("ab+");
        pattern.invert();

        assertTrue(pattern.matches("ba"));
        assertTrue(pattern.matches("bba"));
        assertTrue(pattern.matches("bbba"));

        assertFalse(pattern.matches("ab"));
        assertFalse(pattern.matches("abb"));
    }



    @Test
    @DisplayName("Инверсия диапазонов r{x,y}")
    void testRangeInversion() {
        Pattern pattern = Pattern.compile("(ac){1,2}b");
        pattern.invert();

        assertTrue(pattern.matches("bca"));
        assertTrue(pattern.matches("bcaca"));
        assertFalse(pattern.matches("acb"));
        assertFalse(pattern.matches("acacb"));
        assertFalse(pattern.matches("bacacac"));
    }

    @Test
    @DisplayName("Инверсия палиндромов (язык не должен измениться)")
    void testPalindromeInversion() {
        Pattern pattern = Pattern.compile("aba|racecar");
        pattern.invert();

        assertTrue(pattern.matches("aba"));
        assertTrue(pattern.matches("racecar"));
    }


    @Test
    @DisplayName("Сложный тест: инверсия с любым символом (.)")
    void testDotOperatorInversion() {
        Pattern pattern = Pattern.compile("ac.b");
        pattern.invert();

        assertTrue(pattern.matches("bxca"));
        assertTrue(pattern.matches("b1ca"));
        assertTrue(pattern.matches("bbca"));
        assertFalse(pattern.matches("axb"));
    }
}
