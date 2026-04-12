package org.example;

import org.example.matcher.MatchResult;


import java.util.Map;

public class Main {
    public static void main(String[] args) {

        Pattern p = Pattern.compile("abb.");

        if (p.matches("abb0")) {
            System.out.println("Success");
        }
    }
}