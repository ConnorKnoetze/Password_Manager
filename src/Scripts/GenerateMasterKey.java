package Scripts;

public class GenerateMasterKey {
    private static final String[] base64Chars = {
            "A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P",
            "Q","R","S","T","U","V","W","X","Y","Z",
            "a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p",
            "q","r","s","t","u","v","w","x","y","z",
            "0","1","2","3","4","5","6","7","8","9",
            "+","/"
    };

    public static String generateMasterKey() {
        StringBuilder masterKey = new StringBuilder();
        for (int i = 0; i < 43; i++) {
            int index = (int) (Math.random() * base64Chars.length);
            masterKey.append(base64Chars[index]);
        }
        return masterKey.append("=").toString();
    }

    public static void main(String[] args) {
        String masterKey = generateMasterKey();
        System.out.println("Generated Master Key: " + masterKey);
    }

}
