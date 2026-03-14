package org.parser;


import org.parser.implementations.smc.CommandHandler;
import org.parser.interfaces.IHandler;
import org.parser.ui.Dialog;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Dialog.dialog();
        /*IHandler handler = new CommandHandler();
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        System.out.println(handler.handleString(line));
        line = scanner.nextLine();
        System.out.println(handler.handleString(line));*/
    }
}