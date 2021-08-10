package com.virtuslab.using_directives.custom.model;

import java.util.List;

public class ListValue implements Value<List<Value<?>>> {
    private final List<Value<?>> v;

    public ListValue(List<Value<?>> v) {
        this.v = v;
    }

    @Override
    public List<Value<?>> get() {
        return v;
    }

    @Override
    public String toString() {
        return v.toString();
    }
}
