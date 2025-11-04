package Scripts;

import DomainModel.Credential;
import DomainModel.CredentialsManager;
import DomainModel.Domain;
import DomainModel.DomainsList;
import Utilities.FileReader;
import Utilities.JsonParser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

public class Decryptor extends FileReader {
    private static final String EXECUTABLES_PATH = System.getProperty("user.dir")+"\\AES\\decrypt\\decrypt.exe";
    private static final String OUTPUT_PATH = System.getProperty("user.dir")+"\\textfiles\\output.txt";
    private static final String MASTER_KEY = "QUJDREVGR0hJSktMTU5PUFFSU1RVVldYWVphYmNkZWY=";

    private final JsonParser jsonParser;
    private final CredentialsManager credentialsManager;
    public Decryptor(JsonParser jsonParser, CredentialsManager credentialsManager) {
        this.jsonParser = jsonParser;
        this.credentialsManager = credentialsManager;
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

        ExecutorService es = Executors.newFixedThreadPool(2);
        try {
            Future<String> stdout = es.submit(() -> new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8));
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

    public void decryptAllCredentials() throws IOException {
        ArrayList<HashMap<String, String>> jsonList = jsonParser.getJsonList();
        DomainsList domains = jsonParser.getDomains();

        int credsCount = domains.size();
        for (int i = 0; i < credsCount; i++) {
            String key = jsonList.get(i).get("key");
            String keyIv = jsonList.get(i).get("key_iv");
            String passIv = jsonList.get(i).get("pass_iv");
            String cipherText = jsonList.get(i).get("password");

            String decrypted = decrypt(key, keyIv, passIv, cipherText);
            String[] parts = decrypted.split(";", 2);
            if (parts.length == 2) {
                System.out.println(Arrays.toString(parts));
                String username = parts[0];
                String password = parts[1];
                credentialsManager.addCredential(new Credential(domains.get(i), username, password));
            }
        }
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

    public static void main(String[] args) {
        JsonParser jsonParser = new JsonParser("");
        CredentialsManager credentialsManager = new CredentialsManager();
        Decryptor decryptor = new Decryptor(jsonParser, credentialsManager);
        try {
            String decrypted = decryptor.decrypt(
                    "g8podBQuS/2afMtf9Ow7Ntt8p4Vswcw1UZ9LK+DoZxUSL3SGILIidzNjzbuJ9qOl",
                    "CwUdCPOrkH1c0GFpsPhT7A==",
                    "SZ5wJD9THAGe/nP8sk+eJw==",
                    "X3YXN/omxBNz6jr2U5qW2Q=="
            );
            System.out.println("Decryption successful: " + decrypted);
        } catch (IOException ignored) {}

    }

}
