package com.virtuslab.using_directives.custom.model;

public class StringValue implements Value<String> {
    private final String v;

    public StringValue(String v) {
        this.v = v;
    }

    @Override
    public String get() {
        return v;
    }

    @Override
    public String toString() {
        return v;
    }
}
