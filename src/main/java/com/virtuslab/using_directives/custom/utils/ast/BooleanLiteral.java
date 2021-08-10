package com.virtuslab.using_directives.custom.utils.ast;

import com.virtuslab.using_directives.custom.utils.Position;

public class BooleanLiteral extends UsingPrimitive {
    public void setValue(Boolean value) {
        this.value = value;
    }

    public Boolean getValue() {
        return value;
    }

    private Boolean value;

    public BooleanLiteral( Boolean value, Position position) {
        super(position);
        this.value = value;
    }

    public BooleanLiteral() { }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
