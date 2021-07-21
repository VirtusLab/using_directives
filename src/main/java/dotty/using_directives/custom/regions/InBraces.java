package dotty.using_directives.custom.regions;

public class InBraces extends Region {
    public Region outer;
    @Override
    public Region outer() {
        return outer;
    }

    @Override
    protected String delimiter() {
        return "}";
    }

    public InBraces(Region outer) {
        this.outer = outer;
    }
}
