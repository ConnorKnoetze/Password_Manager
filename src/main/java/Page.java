enum Page {
    HOME("home"),
    VIEW("view"),
    ADD("add"),
    GENERATE("generate"),
    AUTH("auth");

    private final String name;
    Page(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
}