package dotty.using_directives.custom.utils.ast;

import dotty.using_directives.custom.utils.Position;

public class StringLiteral extends UsingPrimitive {
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    private String value;

    public StringLiteral(String value, Position position) {
        super(position);
        this.value = value;
    }

    public StringLiteral() { }

    @Override
    public String toString() {
        return "\"" + value + "\"";
    }
}
