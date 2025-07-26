package util;

import java.io.*;

public class FileCleaner {

    public static void runAsyncChecks(String filePath) {
        Thread sizeCheckThread = new Thread(() -> clearFileIfTooLarge(filePath));
        Thread lineCheckThread = new Thread(() -> clearFileIfTooManyLines(filePath));

        sizeCheckThread.start();
        lineCheckThread.start();
    }

    public static void clearFileIfTooLarge(String filePath) {
        final long MAX_SIZE = 1024 * 1024;

        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("File does not exist: " + filePath);
            return;
        }

        if (file.length() > MAX_SIZE) {
            try (FileWriter fw = new FileWriter(file, false)) {
                fw.write("");
                System.out.println("Logging File exceeded 1MB. Contents cleared.");
            } catch (IOException e) {
                System.err.println("Error while clearing file: " + e.getMessage());
            }
        } else {
            System.out.println("Logging File is within size limit.");
        }
    }

    public static void clearFileIfTooManyLines(String filePath) {
        final int MAX_LINES = 2000;

        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("File does not exist: " + filePath);
            return;
        }

        int lineCount = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            while (reader.readLine() != null) {
                lineCount++;
                if (lineCount > MAX_LINES) break;
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return;
        }

        if (lineCount > MAX_LINES) {
            try (FileWriter fw = new FileWriter(file, false)) {
                fw.write("");
                System.out.println("Logging File had more than 1000 lines. Contents cleared.");
            } catch (IOException e) {
                System.err.println("Error clearing file: " + e.getMessage());
            }
        } else {
            System.out.println("Logging File has " + lineCount + " lines. No need to clear.");
        }
    }
}
