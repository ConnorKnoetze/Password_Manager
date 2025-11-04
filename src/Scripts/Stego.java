package Scripts;

import java.io.IOException;
import java.io.OutputStream;

public class Stego {

    private static final String PROJECT_PATH = System.getProperty("user.dir");
    private static final String OPENSTEGO_PATH = PROJECT_PATH + "\\libs\\openstego.jar"; // Path to OpenStego JAR
    private static final String MESSAGE_FILE_PATH = PROJECT_PATH + "\\textfiles\\key.txt"; // Image to hide
    private static final String COVER_IMAGE_PATH = PROJECT_PATH + "\\resources\\bike.png"; // Cover image
    private static final String SECRET_IMAGE_PATH = PROJECT_PATH + "\\resources\\bike1.png"; // Output stego image
    private static final String PASSWORD = "yourPassword"; // Password for encryption (optional)


    public static void hideString(String message) {
        try {
            // Build the OpenStego CLI command
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "java", "-jar", OPENSTEGO_PATH, "embed",
                    "-mf", "-", // Use stdin for the message
                    "-cf", COVER_IMAGE_PATH,
                    "-sf", SECRET_IMAGE_PATH,
                    "-p", PASSWORD,
                    "-A", "AES256"
            );

            // Start the process
            Process process = processBuilder.start();

            // Write the message to the process's stdin
            try (OutputStream os = process.getOutputStream()) {
                os.write(message.getBytes());
                os.flush();
            }

            // Wait for the process to complete
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("String successfully hidden in the image.");
            } else {
                System.err.println("Error occurred while hiding the string.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hideImage() {

        System.out.println(MESSAGE_FILE_PATH);
        System.out.println(COVER_IMAGE_PATH);
        System.out.println(SECRET_IMAGE_PATH);
        // Build the OpenStego CLI command
        String command = String.format(
                "java -jar %s embed -mf %s -cf %s -sf %s -p %s -A AES256",
                OPENSTEGO_PATH, MESSAGE_FILE_PATH, COVER_IMAGE_PATH, SECRET_IMAGE_PATH, PASSWORD
        );

        run(command);
    }

    public static void extractImage() {
        // Build the OpenStego CLI command for extraction
        String command = String.format(
                "java -jar %s extract -sf %s -xd %s -p %s",
                OPENSTEGO_PATH, SECRET_IMAGE_PATH, PROJECT_PATH + "/textfiles", PASSWORD
        );

        run(command);
    }

    public static void run(String cmd){
        try {
            // Execute the command
            Process process = Runtime.getRuntime().exec(cmd);

            // Wait for the process to complete
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("Image hidden/extracted successfully.");
            } else {
                System.err.println("Error occurred while hiding/extracting the image.");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
//        hideImage();
        hideString("This is a secret message.");
        extractImage();
    }
}