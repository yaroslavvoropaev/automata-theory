package org.example;

public class Main {
    public static void main(String[] args) {
        Pattern p = Pattern.compile("((a|b)c+d+|e){3}");

        System.out.println(p.matches("aaaabb"));
    }
}