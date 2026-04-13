package org.example;


public class Main {
    public static void main(String[] args) {

        Pattern p = Pattern.compile("(((a|b)+)&.){2,3}");

        if (p.matches("aaaacd")) {
            System.out.println("Success");
        }
    }
}