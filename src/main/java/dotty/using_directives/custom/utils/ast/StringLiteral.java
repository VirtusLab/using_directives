package dotty.using_directives.custom.utils.ast;

import dotty.using_directives.custom.utils.Position;

public class StringLiteral extends UsingPrimitive {
    String value;

    public StringLiteral(String value, Position position) {
        super(position);
        this.value = value;
    }

    @Override
    public String toString() {
        return "\"" + value + "\"";
    }
}
