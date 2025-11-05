package Scripts;

import DomainModel.Credential;
import DomainModel.Domain;
import Utilities.FileReader;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

public class Decryptor extends FileReader {
    private static final String EXECUTABLES_PATH = System.getProperty("user.dir")+"\\AES\\decrypt\\decrypt.exe";
    private static final String OUTPUT_PATH = System.getProperty("user.dir")+"\\textfiles\\output.txt";
    private static String MASTER_KEY;

    public Decryptor(String masterKey) {
        MASTER_KEY = masterKey;
    }
    public String decrypt(String key, String keyIv, String passIv, String cipherText) throws IOException {
        List<String> cmd = new ArrayList<>();
        cmd.add(EXECUTABLES_PATH);
        cmd.add(MASTER_KEY);
        cmd.add(key);
        cmd.add(keyIv);
        cmd.add(cipherText);
        cmd.add(passIv);
        cmd.add(MASTER_KEY.length()+1+"");
        cmd.add(key.length()+1 +"");
        cmd.add(keyIv.length()+1+"");
        cmd.add(cipherText.length()+1+"");
        cmd.add(passIv.length()+1+"");

        ProcessBuilder pb = new ProcessBuilder(cmd);
        Process process = pb.start();

        ExecutorService es = Executors.newFixedThreadPool(1);
        try {
            Future<String> stderr = es.submit(() -> new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8));

            boolean finished = process.waitFor(30, TimeUnit.SECONDS); // adjust timeout as needed
            if (!finished) {
                process.destroyForcibly();
                throw new TimeoutException("decrypt.exe timed out");
            }

            int exit = process.exitValue();
            String err = stderr.get(1, TimeUnit.SECONDS).trim();

        } catch (ExecutionException | TimeoutException e) {
            throw new IOException("Failed to read process streams", e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            es.shutdownNow();
        }

        return super.readFile(new File(OUTPUT_PATH));
    }

    public Credential decryptSingleCredential(HashMap<String, String> jsonMap, String domain) throws IOException {
        String key = jsonMap.get("key");
        String keyIv = jsonMap.get("key_iv");
        String passIv = jsonMap.get("pass_iv");
        String cipherText = jsonMap.get("password");

        String decrypted = decrypt(key, keyIv, passIv, cipherText);
        String[] parts = decrypted.split(";", 2);

        String username = parts[0];
        String password = parts[1];
        return new Credential(new Domain(domain), username, password);
    }

}
