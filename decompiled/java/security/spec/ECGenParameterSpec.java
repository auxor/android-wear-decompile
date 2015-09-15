package java.security.spec;

public class ECGenParameterSpec implements AlgorithmParameterSpec {
    private final String name;

    public ECGenParameterSpec(String name) {
        this.name = name;
        if (this.name == null) {
            throw new NullPointerException("name == null");
        }
    }

    public String getName() {
        return this.name;
    }
}
