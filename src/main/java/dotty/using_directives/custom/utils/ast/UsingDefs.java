package dotty.using_directives.custom.utils.ast;

import dotty.using_directives.custom.utils.Position;

import java.util.List;

public class UsingDefs extends UsingTree {
    List<UsingDef> usingdefs;
    public UsingDefs(List<UsingDef> usingdefs, Position position) {
        super(position);
        this.usingdefs = usingdefs;
    }

    @Override
    public String toString() {
        return "UsingDefs(" + usingdefs + ")";
    }
}
