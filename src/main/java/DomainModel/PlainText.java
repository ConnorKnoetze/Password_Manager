package DomainModel;

public class PlainText {
    private String plainText;

    public PlainText(String username, String password) {
        this.plainText = String.format("%s;%s", username, password);

    }

    public String getPlainText() {
        return plainText;
    }
}
