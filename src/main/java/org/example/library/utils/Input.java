package org.example.library.utils;

import java.util.Scanner;

public class Input {
    private static final Scanner scanner = new Scanner(System.in);

    public static String text(String msg) {
        System.out.print(msg);
        return scanner.nextLine();
    }

    public static int number(String msg) {
        System.out.print(msg);
        return Integer.parseInt(scanner.nextLine());
    }
}
