# Password Manager

My password manager that encrypts and decrypts saved user credentials using AES-256-CBC.

A simple desktop (Swing) password manager written in Java that:
- Encrypts credentials using an external AES CLI tool (AES executables bundled/expected under `AES/`).
- Stores encrypted credential parts as text files and serializes entries to `creds.json`.
- Hides / extracts the master key using steganography (OpenStego) so the app can retrieve the master key at runtime.
- Provides a Swing UI to add, view (reveal), and delete credentials.

> Note: This repository is a developer project/demonstration and has platform assumptions (Windows-style paths). Review the security notes below before using it for any real secrets.

## Quick overview — how it works

1. Master key
   - The app obtains a master key at startup using `Scripts.Stego.extractString()`, which uses an OpenStego command to extract a hidden `key.txt` from `resources/bike1.png` (via `libs/openstego.jar`) into a temporary folder.
   - `Scripts.GenerateMasterKey` can generate a suitable master key string (base64-like, 44 characters ending with `=`).

2. Encryption flow (saving a credential)
   - When you add a credential, the app constructs a plaintext string `username;password` (`DomainModel.PlainText`).
   - `Scripts.Encryptor` (constructed with the master key) encrypts that plaintext using the external AES CLI (the repository expects executables under `AES/`).
   - The encryption step writes/produces text files (under `textfiles/`) such as `key.txt`, `key_iv.txt`, `pass_iv.txt`, `password.txt` (see `Utilities.EncryptedFilesReader`).
   - `Scripts.DataWriter` reads these textfiles and writes a `creds.json`-style file at the project root with a custom serialized format:
     domain{
       key{...}
       key_iv{...}
       pass_iv{...}
       password{...}
     }
     Multiple entries are separated with `;`.

3. Decryption / reveal
   - When you request to reveal a credential, `Scripts.Decryptor` runs the AES decrypt executable (`AES/decrypt/decrypt.exe`) as a process, passing the master key and the encrypted pieces to it and reads the decrypted result from the process output.
   - The decrypted result is expected to be `username;password` and the UI updates accordingly.

4. UI
   - The Swing application is implemented in `App.java` and page components under `Pages/` (notably `Pages/ViewPage/View.java`). You get pages for authentication and viewing/adding credentials.

## Features
- GUI (Swing) for adding, viewing, and deleting credentials.
- Master key extracted from a steganographically hidden file in an image (OpenStego).
- AES encryption and decryption performed by an external CLI (the Java classes call the executables).
- Simple, human-readable `creds.json` storage format (custom format produced by `DataWriter`).

## Requirements / Prerequisites
- Java 8+ (JDK) — the project uses standard Java and Swing.
- OpenStego JAR placed at `libs/openstego.jar` (used by `Scripts.Stego`).
- AES CLI executables:
  - `AES/decrypt/decrypt.exe` (and likely `AES/encrypt/encrypt.exe`) — the Java code invokes `AES/decrypt/decrypt.exe`.
  - These executables are external binaries and must accept the arguments used in the `Scripts.Decryptor`/`Encryptor` calls. The repository expects them at runtime.
- Resources:
  - `resources/bike.png` (cover image), `resources/bike1.png` (stego image).
  - `textfiles/` folder (used as temporary storage for the AES output/input files).
- Platform: The code uses backslash paths and `System.getProperty("user.dir")` combined with Windows-style backslashes. It is currently biased toward Windows; update paths if running on Linux/macOS.

## Repository layout (important files)
- src/main/java/
  - App.java — main Swing application entry point.
  - DomainModel/
    - Credential.java, CredentialsManager.java, PlainText.java — domain classes & manager.
  - Pages/
    - ViewPage/View.java — UI for listing/ revealing credentials.
  - Scripts/
    - Encryptor.java, Decryptor.java — wrappers that call the AES CLI executables.
    - DataReader.java, DataWriter.java — read/write `creds.json`.
    - Stego.java — uses OpenStego to hide / extract the master key (calls `libs/openstego.jar`).
    - GenerateMasterKey.java — utility to create a new master key string.
  - Utilities/
    - EncryptedFilesReader.java — reads AES-generated text files into a storable string.
    - JsonParser.java, FileReader/FileWriter utilities.
- AES/ (expected)
  - decrypt/decrypt.exe (required at runtime)
  - (encrypt executable location expected by Encryptor)
- libs/openstego.jar (required for stego actions)
- resources/
  - bike.png, bike1.png — cover and stego images
- creds.json — stored credentials file placed in project root at runtime (created/updated by DataWriter).
- textfiles/ — temporary files created/used by encryption/decryption (`key.txt`, `key_iv.txt`, `pass_iv.txt`, `password.txt`, output files, etc.)

## Build & run (local developer steps)
There is no provided Gradle/Maven wrapper in the repository by default; build/run using your preferred Java workflow:

1. Prepare environment
   - Ensure JDK 8+ installed and `java`/`javac` available.
   - Put `libs/openstego.jar` in the `libs/` folder.
   - Place AES encrypt/decrypt executables in `AES/encrypt/` and `AES/decrypt/` as expected by the code (`decrypt.exe` at `AES/decrypt/decrypt.exe`).
   - Ensure `resources/` and `textfiles/` folders exist and the image files are in `resources/`.

2. Compile
   - From the project root:
     - Example (basic javac):
       - mkdir -p out
       - javac -d out $(find src/main/java -name "*.java")
     - Or import the project into an IDE (IntelliJ IDEA, Eclipse) and run `App.java`.

3. Run
   - Example (after compilation):
     - java -cp out App
   - The application will call `Stego.extractString()` to retrieve the master key and then open the GUI.

4. Generating & embedding a master key (optional)
   - Run `Scripts.GenerateMasterKey.main()` to print a generated key.
   - Use `Scripts.Stego.hideString(String message)` or the `Stego` main to embed the key into `resources/bike1.png` using OpenStego (the code calls the OpenStego CLI).
   - The `Stego` class uses a hard-coded stego password (see security notes). Adjust as needed.

## Usage
- Launch the `App` GUI.
- Add a credential: choose a domain, username, and password — when saved the app will encrypt the credential pieces (via the external AES CLI) and update `creds.json`.
- View credentials: entries appear with encrypted placeholders; clicking "Reveal" runs `Decryptor` which invokes the AES decryption executable and updates the UI with plaintext username/password.
- Delete credentials: UI provides a delete action which removes the entry and updates the stored data.

## Troubleshooting & notes
- If reveal/add operations fail, check:
  - That the AES executables exist and are runnable from the project's working directory.
  - That `textfiles/` folder is writable and the process has permission to create/read files there.
  - The OpenStego JAR path `libs/openstego.jar` is correct and Java can execute `java -jar ...`.
  - On non-Windows OS, update path separators in `Scripts.Stego`, `Scripts.Decryptor`, and any other places that hard-code backslashes.
- Decryptor uses a 30-second process wait timeout; long-running external tools may require increasing the wait time.

## Security considerations
 - This is my personal attempt at recreating a secure password storage system from my own knowledge. It likely doesn't meet stringent security standards and as such should not be used in any serious capacity.
