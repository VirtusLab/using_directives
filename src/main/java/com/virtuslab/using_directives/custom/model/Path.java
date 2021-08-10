package com.virtuslab.using_directives.custom.model;

import java.util.List;
import java.util.Objects;

public class Path {
    public List<String> getPath() {
        return path;
    }

    private final List<String> path;

    public Path(List<String> path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Path path1 = (Path) o;
        return path.equals(path1.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }

    @Override
    public String toString() {
        return String.join(".", path);
    }
}
