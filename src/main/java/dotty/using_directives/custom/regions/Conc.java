package dotty.using_directives.custom.regions;

import java.util.Objects;

public class Conc extends IndentWidth {
    public IndentWidth l;
    public Run r;

    public Conc(IndentWidth l, Run r) {
        this.l = l;
        this.r = r;
    }

    public boolean lessOrEqual(Run that) {
        return false;
    }

    public boolean lessOrEqual(Conc that) {
        return this.l.equals(that.l) && this.r.lessOrEqual(that.r);
    }

    @Override
    public String toPrefix() {
        return String.format("%s, %s", l, r);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Conc conc = (Conc) o;
        return Objects.equals(l, conc.l) && Objects.equals(r, conc.r);
    }

    @Override
    public int hashCode() {
        return Objects.hash(l, r);
    }

    @Override
    public String toString() {
        return "Conc{" +
                "l=" + l +
                ", r=" + r +
                '}';
    }
}
