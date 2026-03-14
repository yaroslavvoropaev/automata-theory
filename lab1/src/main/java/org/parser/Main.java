package org.parser;

import org.parser.implementations.jflex.JFlexHandler;
import org.parser.implementations.regex.RegexHandler;
//import org.parser.implementations.smc.CommandHandler;
import org.parser.interfaces.IHandler;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        IHandler handler = new JFlexHandler();

        String line = scanner.nextLine();
        System.out.println(handler.handleString(line));
        line = scanner.nextLine();
        System.out.println(handler.handleString(line));
        System.out.println(handler.getStatistics());
    }
}