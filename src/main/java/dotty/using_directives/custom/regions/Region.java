package dotty.using_directives.custom.regions;

import dotty.using_directives.custom.Tokens;

import java.util.HashSet;

abstract public class Region {
    public static Region topLevelRegion(IndentWidth width) {
        return new Indented(width, new HashSet<>(), Tokens.EMPTY, null);
    }

    abstract public Region outer();

    public boolean isOutermost() {
        return outer() == null;
    }

    public Region enclosing() {
        Region outer = outer();
        assert(outer != null);
        return outer;
    }

    public IndentWidth knownWidth = null;

    public IndentWidth indentWidth() {
        if (knownWidth == null) {
            return IndentWidth.Zero();
        } else {
            return knownWidth;
        }
    }

    public void proposeKnownWidth(IndentWidth width, Tokens lastToken) {
        if(knownWidth == null) {
            if(this instanceof InParens && lastToken != Tokens.LPAREN) {
                useOuterWidth();
            } else {
                knownWidth = width;
            }
        }
    }

    private void useOuterWidth() {
        if (enclosing().knownWidth == null) {
            enclosing().useOuterWidth();
        }
        knownWidth = enclosing().knownWidth;
    }

    abstract protected String delimiter();

    private String visualize() {
        return indentWidth().toPrefix() +
                delimiter() +
                (outer() != null ? "\n" + outer().visualize() : "");
    }
}
