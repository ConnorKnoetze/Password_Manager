package Scripts;

import DomainModel.PlainText;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Encryptor {
    private static final String EXECUTABLES_PATH = System.getProperty("user.dir")+"\\AES\\encrypt\\encrypt.exe";
    private static final String MASTER_KEY = "QUJDREVGR0hJSktMTU5PUFFSU1RVVldYWVphYmNkZWY=";


    public Encryptor() {}

    public void encrypt(PlainText plainText) throws IOException {
        List<String> cmd = new ArrayList<>();
        cmd.add(EXECUTABLES_PATH);
        cmd.add(plainText.getPlainText());
        cmd.add(MASTER_KEY);

        ProcessBuilder pb = new ProcessBuilder(cmd);
        Process process = pb.start();

        ExecutorService es = Executors.newFixedThreadPool(2);
        try {
            Future<String> stdout = es.submit(() -> new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8));
            Future<String> stderr = es.submit(() -> new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8));

            boolean finished = process.waitFor(30, TimeUnit.SECONDS); // adjust timeout as needed
            if (!finished) {
                process.destroyForcibly();
                throw new TimeoutException("encrypt.exe timed out");
            }

            int exit = process.exitValue();
            String err = stderr.get(1, TimeUnit.SECONDS).trim();

            if (exit != 0) {
                throw new IOException("encrypt.exe failed (exit " + exit + "): " + err);
            }
        } catch (ExecutionException | TimeoutException e) {
            throw new IOException("Failed to read process streams", e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            es.shutdownNow();
        }

    }
    public static void main(String[] args) {
        PlainText plainText = new PlainText("user", "password");
        Encryptor encryptor = new Encryptor();
        try {
            encryptor.encrypt(plainText);
            System.out.println("Encryption successful");
        } catch (IOException e) {
            System.err.println("Encryption failed: " + e.getMessage());
        }
    }
}
