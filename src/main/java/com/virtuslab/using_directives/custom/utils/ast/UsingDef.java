package com.virtuslab.using_directives.custom.utils.ast;

import com.virtuslab.using_directives.custom.utils.Position;

public class UsingDef extends UsingTree {
  private String key;
  private UsingValue value;

  public UsingDef(String key, UsingValue value, Position position) {
    super(position);
    this.key = key;
    this.value = value;
  }

  public UsingDef() {}

  public String getKey() {
    return key;
  }

  public UsingValue getValue() {
    return value;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public void setValue(UsingValue value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "UsingDef(" + key + ", " + value + ')';
  }
}
