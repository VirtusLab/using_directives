package dotty.using_directives.custom.model;

public class NumericValue implements Value<String> {
    private final String v;

    public NumericValue(String v) {
        this.v = v;
    }


    @Override
    public String get() {
        return v;
    }

    @Override
    public String toString() {
        return v;
    }
}
