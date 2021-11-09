package com.virtuslab.using_directives.custom.utils.ast;

import com.virtuslab.using_directives.custom.utils.Position;

public class SettingDef extends UsingTree {
  private String key;
  private SettingDefOrUsingValue value;

  public SettingDef(String key, SettingDefOrUsingValue value, Position position) {
    super(position);
    this.key = key;
    this.value = value;
  }

  public SettingDef() {}

  public String getKey() {
    return key;
  }

  public SettingDefOrUsingValue getValue() {
    return value;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public void setValue(SettingDefOrUsingValue value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return "SettingDef(" + key + ", " + value + ')';
  }
}
