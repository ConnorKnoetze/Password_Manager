package DomainModel;

public class CipherText {
    private String cipherText;

    public CipherText() {
        this.cipherText = "";
    }
    public CipherText(String cipherText) {
        this.cipherText = cipherText;
    }

    public String getCipherText() {
        return cipherText;
    }

    public void setCipherText(String cipherText) {
        if (cipherText != null){
            this.cipherText = cipherText;
        }
    }
}
