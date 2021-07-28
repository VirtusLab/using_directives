package dotty.using_directives.custom.utils.ast;

public class NumericLiteral extends UsingPrimitive {
    String value;

    public NumericLiteral(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
