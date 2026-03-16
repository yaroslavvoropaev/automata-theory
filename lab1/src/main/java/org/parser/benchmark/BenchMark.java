package org.parser.benchmark;

import org.parser.implementations.jflex.JFlexHandler;
import org.parser.implementations.smc.SmcHandler;
import org.parser.interfaces.IHandler;
import org.parser.implementations.regex.RegexHandler;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class BenchMark {

    public static void main(String[] args) throws IOException {
        for (int i = 1; i <= 10  ; ++i) {
            String file = i + ".txt";
            String content = Files.readString(Path.of("timing", file));
            long ns = 0;
            for (int j = 0; j < 100; ++j) {
                IHandler regexHandler = new RegexHandler();
                long start = System.nanoTime();
                regexHandler.handleString(content);
                long end = System.nanoTime();
                ns += (end - start);
            }
            System.out.println(ns / 100 / 1000);
        }

        System.out.println("\n\n");

        for (int i = 1; i <= 10  ; ++i) {
            String file = i + ".txt";
            String content = Files.readString(Path.of("timing", file));
            long ns = 0;
            for (int j = 0; j < 100; ++j) {
                IHandler jflex = new JFlexHandler();
                long start = System.nanoTime();
                jflex.handleString(content);
                long end = System.nanoTime();
                ns += (end - start);
            }
            System.out.println(ns / 100 / 1000);
        }

        System.out.println("\n\n");
        for (int i = 1; i <= 10  ; ++i) {
            String file = i + ".txt";
            String content = Files.readString(Path.of("timing", file));
            long ns = 0;
            for (int j = 0; j < 100; ++j) {
                IHandler smc = new SmcHandler();
                long start = System.nanoTime();
                smc.handleString(content);
                long end = System.nanoTime();
                ns += (end - start);
            }
            System.out.println(ns / 100 / 1000);
        }
    }
}