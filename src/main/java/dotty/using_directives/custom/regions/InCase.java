package dotty.using_directives.custom.regions;

public class InCase extends Region {

    public Region outer;

    @Override
    public Region outer() {
        return outer;
    }

    @Override
    protected String delimiter() {
        return "=>";
    }

    public InCase(Region outer) {
        this.outer = outer;
    }
}
