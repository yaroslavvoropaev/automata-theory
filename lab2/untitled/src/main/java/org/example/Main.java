package org.example;

public class Main {
    public static void main(String[] args) {
        Pattern p = Pattern.compile("(a|b){0,3}");
        String restored = p.restoreRegex();

        System.out.println("Исходное: (a|b)+?");
        System.out.println("Восстановленное: " + restored);

        Pattern p2 = Pattern.compile(restored);

        System.out.println(p.matches(""));
        System.out.println(p2.matches(""));
    }
}