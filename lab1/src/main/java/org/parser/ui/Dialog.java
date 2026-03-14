package org.parser.ui;

import org.parser.interfaces.IHandler;
import org.parser.implementations.regex.RegexHandler;
import org.parser.implementations.jflex.JFlexHandler;
import org.parser.implementations.smc.CommandHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Dialog {
    private static final Map<Integer, IHandlerFactory> handlers = new HashMap<>();

    static {
        handlers.put(1, RegexHandler::new);
        handlers.put(2, JFlexHandler::new);
        handlers.put(3, CommandHandler::new);
    }

    @FunctionalInterface
    private interface IHandlerFactory {
        IHandler create();
    }

    private static int readInt(Scanner scanner) {
        while(true) {
            try {
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch(NumberFormatException ex) {
                System.out.print("Please, input number: ");
            }
        }
    }

    private static IHandler selectHandler(Scanner scanner) {
        System.out.println("0 - Выход");
        System.out.println("1 - Regex");
        System.out.println("2 - JFlex");
        System.out.println("3 - SMC");
        System.out.print("Выберите реализацию: ");

        int choice = readInt(scanner);

        if (choice == 0) {
            System.out.println("Программа завершена.");
            System.exit(0);
        }

        IHandlerFactory factory = handlers.get(choice);
        if (factory == null) {
            System.out.println("Неверный выбор. Пожалуйста, выберите 1, 2 или 3.");
            return null;
        }
        return factory.create();
    }


    private static boolean processInput(Scanner scanner, IHandler handler) {
        System.out.println("\n1 - Ввод с терминала");
        System.out.println("2 - Чтение из файла");
        System.out.print("Выберите источник ввода: ");


        int choice = readInt(scanner);

        return switch (choice) {
            case 1 -> processTerminalInput(scanner, handler);
            case 2 -> processFileInput(scanner, handler);
            default -> {
                yield false;
            }
        };
    }

    private static boolean processTerminalInput(Scanner scanner, IHandler handler) {
        System.out.println("\nВведите строки: ");

        while (true) {
            System.out.print("> ");
            String line;

            try {
                line = scanner.nextLine();
            } catch (NoSuchElementException e) {
                break;
            }

            boolean result = handler.handleString(line);
            System.out.println("Результат обработки: " + (result ? "корретно" : "некорректно"));
        }
        return true;
    }

    private static boolean processFileInput(Scanner scanner, IHandler handler) {
        System.out.print("\nВведите путь к файлу: ");
        String filePath = scanner.nextLine().trim();

        Path path = Paths.get(filePath);

        if (!Files.exists(path)) {
            System.out.println("Файл не найден: " + filePath);
            return false;
        }

        if (!Files.isRegularFile(path)) {
            System.out.println("Указанный путь не является файлом: " + filePath);
            return false;
        }

        try {
            List<String> lines = Files.readAllLines(path);
            int successCount = 0;
            int totalCount = 0;

            System.out.println("\nОбработка файла " + filePath + "...");

            for (String line : lines) {
                totalCount++;
                boolean result = handler.handleString(line);
                if (result) {
                    successCount++;
                }

                System.out.println("Строка " + totalCount + ": " +
                        (result ? "✓" : "✗") + " - " +
                        (line.length() > 50 ? line.substring(0, 47) + "..." : line));
            }

            System.out.println("\nИтоги обработки файла:");
            System.out.println("Всего строк: " + totalCount);
            System.out.println("Успешно обработано: " + successCount);
            System.out.println("Не удалось обработать: " + (totalCount - successCount));

        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + e.getMessage());
            return false;
        }

        return true;
    }

    public static void dialog() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            try {
                System.out.println("\n=== Обработчик строк ===");

                IHandler handler = selectHandler(scanner);
                if (handler == null) continue;

                if (!processInput(scanner, handler)) {
                    continue;
                }

                System.out.println(handler.getStatistics());
                break;

            } catch (Exception ex) {
                System.err.println("Ошибка: " + ex.getMessage());
            }
        }
        scanner.close();
    }
}
