package dotty.using_directives.custom.utils.ast;

import java.util.List;

public class UsingValues extends UsingValue {
    public List<UsingPrimitive> values;

    public UsingValues(List<UsingPrimitive> values) {
        this.values = values;
    }

    @Override
    public String toString() {
        return "UsingValues(" + values + ')';
    }
}
