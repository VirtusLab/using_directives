package com.virtuslab.using_directives.custom.utils.ast;

import com.virtuslab.using_directives.custom.model.UsingDirectiveSyntax;
import com.virtuslab.using_directives.custom.utils.Position;

public abstract class UsingValue extends SettingDefOrUsingValue {
  public UsingValue(Position position) {
    super(position);
  }

  public abstract UsingDirectiveSyntax getSyntax();

  public UsingValue() {}
}
