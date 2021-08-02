package dotty.using_directives.custom.utils.ast;

import dotty.using_directives.custom.utils.Position;

public class NumericLiteral extends UsingPrimitive {
    String value;

    public NumericLiteral(String value, Position position) {
        super(position);
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
