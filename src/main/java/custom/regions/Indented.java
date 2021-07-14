package dotty.using_directives.custom.regions;

import dotty.using_directives.custom.Tokens;

import java.util.Set;

public class Indented extends Region {
    public Region outer;
    public Tokens prefix;
    public Set<IndentWidth> others;
    public IndentWidth width;


    @Override
    public Region outer() {
        return null;
    }

    @Override
    protected String delimiter() {
        return null;
    }

    public Indented(IndentWidth width, Set<IndentWidth> others, Tokens prefix, Region outer) {
        this.outer = outer;
        this.prefix = prefix;
        this.others = others;
        this.width = width;
        this.knownWidth = width;
    }
}
