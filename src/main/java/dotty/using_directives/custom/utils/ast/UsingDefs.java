package dotty.using_directives.custom.utils.ast;

import java.util.List;

public class UsingDefs extends UsingTree {
    List<UsingDef> usingdefs;
    public UsingDefs(List<UsingDef> usingdefs) {
        this.usingdefs = usingdefs;
    }

    @Override
    public String toString() {
        return "UsingDefs(" + usingdefs + ")";
    }
}
