package com.virtuslab.using_directives.custom.model;

import com.virtuslab.using_directives.custom.utils.ast.UsingTree;
import java.util.Map;

public class Setting implements ValueOrSetting<Map<String, ValueOrSetting<?>>> {
  private final Map<String, ValueOrSetting<?>> v;
  private final UsingTree astNode;

  public Setting(Map<String, ValueOrSetting<?>> v, UsingTree astNode) {
    this.v = v;
    this.astNode = astNode;
  }

  @Override
  public Map<String, ValueOrSetting<?>> get() {
    return v;
  }

  @Override
  public UsingTree getRelatedASTNode() {
    return astNode;
  }

  @Override
  public String toString() {
    return "Setting{" + "v=" + v + '}';
  }
}
