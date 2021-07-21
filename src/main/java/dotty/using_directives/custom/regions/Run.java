package dotty.using_directives.custom.regions;

import java.util.Objects;

public class Run extends IndentWidth {
    public char ch;
    public int n;

    public Run(char ch, int n) {
        this.ch = ch;
        this.n = n;
    }

    public boolean lessOrEqual(Run that) {
        return this.n <= that.n && (this.ch == that.ch || this.n == 0);
    }

    public boolean lessOrEqual(Conc that) {
        return lessOrEqual(that.l);
    }

    @Override
    public String toPrefix() {
        return String.format("%s %s %s", n, kind(ch), n == 1 ? "" : "s");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Run run = (Run) o;
        return ch == run.ch && n == run.n;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ch, n);
    }

    @Override
    public String toString() {
        return "Run{" +
                "ch=" + ch +
                ", n=" + n +
                '}';
    }
}
