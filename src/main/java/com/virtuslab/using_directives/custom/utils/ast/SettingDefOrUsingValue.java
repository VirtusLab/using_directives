package com.virtuslab.using_directives.custom.utils.ast;

import com.virtuslab.using_directives.custom.utils.Position;

public abstract class SettingDefOrUsingValue extends UsingTree {
  public SettingDefOrUsingValue(Position position) {
    super(position);
  }

  public SettingDefOrUsingValue() {}
}
