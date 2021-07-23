package dotty.using_directives.custom.regions;

import dotty.using_directives.custom.Tokens;

import java.util.Objects;
import java.util.Set;

public class Indented extends Region {
    public Region outer;
    public Tokens prefix;
    public Set<IndentWidth> others;
    public IndentWidth width;


    @Override
    public Region outer() {
        return outer;
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

    @Override
    public String toString() {
        return "Indented{" +
                "outer=" + outer +
                ", prefix=" + prefix +
                ", others=" + others +
                ", width=" + width +
                ", knownWidth=" + knownWidth +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Indented indented = (Indented) o;
        return outer.equals(indented.outer) &&
                prefix == indented.prefix &&
                others.equals(indented.others) &&
                width.equals(indented.width);
    }

    @Override
    public int hashCode() {
        return Objects.hash(outer, prefix, others, width);
    }
}
