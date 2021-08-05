package dotty.using_directives.custom.utils.ast;

import dotty.using_directives.custom.utils.Position;

public class NumericLiteral extends UsingPrimitive {
    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    private String value;

    public NumericLiteral(String value, Position position) {
        super(position);
        this.value = value;
    }

    public NumericLiteral() { }

    @Override
    public String toString() {
        return value;
    }
}
