package com.virtuslab.using_directives.custom.model;

import java.util.Map;

public class Setting implements ValueOrSetting<Map<String, ValueOrSetting<?>>> {
  private final Map<String, ValueOrSetting<?>> v;

  public Setting(Map<String, ValueOrSetting<?>> v) {
    this.v = v;
  }

  @Override
  public Map<String, ValueOrSetting<?>> get() {
    return v;
  }

  @Override
  public String toString() {
    return "Setting{" + "v=" + v + '}';
  }
}
