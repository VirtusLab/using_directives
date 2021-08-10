package com.virtuslab.using_directives.custom.model;

public class BooleanValue implements Value<Boolean> {
    private final Boolean v;

    public BooleanValue(Boolean v) {
        this.v = v;
    }

    @Override
    public Boolean get() {
        return v;
    }

    @Override
    public String toString() {
        return v.toString();
    }
}


