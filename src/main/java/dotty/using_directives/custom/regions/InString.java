package dotty.using_directives.custom.regions;

public class InString extends Region {
    public boolean multiLine;
    public Region outer;

    public InString(boolean multiLine, Region outer) {
        this.multiLine = multiLine;
        this.outer = outer;
    }

    @Override
    public Region outer() {
        return outer;
    }

    @Override
    protected String delimiter() {
        return "}(in string)";
    }
}
