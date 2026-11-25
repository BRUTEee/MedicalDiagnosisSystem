package com.medical.util;

import java.util.Arrays;

public final class PasswordHashTool {

    private PasswordHashTool() {
    }

    static String hashPassword(char[] password, char[] confirmation) {

        try {
            if (password == null || confirmation == null) {
                throw new IllegalArgumentException("Password input is required.");
            }

            if (password.length < PasswordUtil.MIN_LENGTH
                    || password.length > PasswordUtil.MAX_LENGTH) {
                throw new IllegalArgumentException("Password must be between "
                        + PasswordUtil.MIN_LENGTH + " and "
                        + PasswordUtil.MAX_LENGTH + " characters.");
            }

            if (!Arrays.equals(password, confirmation)) {
                throw new IllegalArgumentException("Passwords do not match.");
            }

            return PasswordUtil.hash(new String(password));
        } finally {
            if (password != null) {
                Arrays.fill(password, '\0');
            }
            if (confirmation != null) {
                Arrays.fill(confirmation, '\0');
            }
        }
    }

    public static void main(String[] args) {

        if (args.length != 0) {
            System.err.println("This tool accepts no command-line arguments.");
            System.exit(2);
        }

        java.io.Console console = System.console();

        if (console == null) {
            System.err.println("Run this tool in an interactive terminal.");
            System.exit(2);
        }

        char[] first = console.readPassword("Initial account password: ");
        char[] second = console.readPassword("Confirm password: ");

        try {
            System.out.println(hashPassword(first, second));
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(2);
        }
    }
}
