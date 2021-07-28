package dotty.using_directives.custom.utils.ast;

public class BooleanLiteral extends UsingPrimitive {
    Boolean value;
    public BooleanLiteral(Boolean value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
