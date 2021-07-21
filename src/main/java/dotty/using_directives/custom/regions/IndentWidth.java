package dotty.using_directives.custom.regions;

import java.util.Arrays;

abstract public class IndentWidth {

    public static IndentWidth Zero() {
        return new Run(' ', 0);
    }

    public boolean lessOrEqual(IndentWidth that) {
        if(that instanceof Run) {
            return lessOrEqual((Run) that);
        } else {
            return lessOrEqual((Conc) that);
        }
    }

    abstract boolean lessOrEqual(Run that);

    abstract boolean lessOrEqual(Conc that);

    public boolean less(IndentWidth that) {
        return this.lessOrEqual(that) && !that.lessOrEqual(this);
    }

    abstract public String toPrefix();

    protected String kind(char c) {
        switch(c) {
            case ' ':
                return "space";
            case '\t':
                return "tab";
            default:
                return c + "-character";
        }
    }


}
