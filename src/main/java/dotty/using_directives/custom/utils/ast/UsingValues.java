package dotty.using_directives.custom.utils.ast;

import dotty.using_directives.custom.utils.Position;

import java.util.List;

public class UsingValues extends UsingValue {
    public List<UsingPrimitive> values;

    public UsingValues(List<UsingPrimitive> values, Position position) {
        super(position);
        this.values = values;
    }

    @Override
    public String toString() {
        return "UsingValues(" + values + ')';
    }
}
