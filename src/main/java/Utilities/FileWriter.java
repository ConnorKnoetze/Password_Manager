package Utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;

public class FileWriter {
    public void write(String path, String content) {
        File credsFile = new File(path);
        try (BufferedWriter writer = new BufferedWriter(new java.io.FileWriter(credsFile, false))) {
            writer.write(content);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Failed to write credentials to file: " + e.getMessage());
        }
    }
}
