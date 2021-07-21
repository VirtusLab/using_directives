package dotty.using_directives.custom.regions;

import dotty.using_directives.custom.Tokens;

public class InParens extends Region {

    public Region outer;

    public Tokens prefix;

    @Override
    public Region outer() {
        return outer;
    }

    @Override
    protected String delimiter() {
        switch(prefix) {
            case LPAREN:
                return ")";
            case LBRACKET:
                return "]";
            default:
                return null;
        }
    }

    public InParens(Tokens prefix, Region outer) {
        this.outer = outer;
        this.prefix = prefix;
    }
}
