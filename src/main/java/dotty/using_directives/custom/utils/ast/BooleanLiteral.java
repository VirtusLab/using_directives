package dotty.using_directives.custom.utils.ast;

import dotty.using_directives.custom.utils.Position;

public class BooleanLiteral extends UsingPrimitive {
    Boolean value;

    public BooleanLiteral( Boolean value, Position position) {
        super(position);
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
