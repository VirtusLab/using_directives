package dotty.using_directives.custom.utils.ast;

import dotty.using_directives.custom.utils.Position;

import java.util.List;

public class UsingDefs extends UsingTree {
    private List<UsingDef> usingDefs;
    public UsingDefs(List<UsingDef> usingDefs, Position position) {
        super(position);
        this.usingDefs = usingDefs;
    }

    public List<UsingDef> getUsingDefs() {
        return usingDefs;
    }

    public void setUsingDefs(List<UsingDef> usingDefs) {
        this.usingDefs = usingDefs;
    }

    public UsingDefs(){ }

    @Override
    public String toString() {
        return "UsingDefs(" + usingDefs + ")";
    }
}
