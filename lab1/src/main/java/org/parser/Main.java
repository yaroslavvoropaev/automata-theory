package org.parser;

import org.parser.implementations.regex.RegexParser;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        RegexParser parser = new RegexParser();

        String line = scanner.nextLine();

        System.out.println(parser.handleString(line));
        System.out.println(parser.getStatistics());

    }
}