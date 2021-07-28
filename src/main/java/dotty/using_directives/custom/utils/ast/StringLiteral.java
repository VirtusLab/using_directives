package dotty.using_directives.custom.utils.ast;

public class StringLiteral extends UsingPrimitive {
    String value;

    public StringLiteral(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "\"" + value + "\"";
    }
}
