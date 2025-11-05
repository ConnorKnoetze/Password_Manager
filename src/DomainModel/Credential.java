package DomainModel;

public class Credential {
    private Domain domain;
    private String username;
    private String password;

    public Credential(Domain domain, String username, String password) {
        this.domain = domain;
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
    public String getDomain() {
        return this.domain.getDomain();
    }

    public Domain getDomainObject() {
        return this.domain;
    }

    @Override
    public String toString(){
        return String.format("Credential(domain=%s, username=%s, password=%s)", this.domain.getDomain(), this.username, this.password);
    }
}
