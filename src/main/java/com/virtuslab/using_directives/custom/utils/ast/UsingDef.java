package com.virtuslab.using_directives.custom.utils.ast;

import com.virtuslab.using_directives.custom.model.UsingDirectiveSyntax;
import com.virtuslab.using_directives.custom.utils.Position;

public class UsingDef extends UsingTree {
  private SettingDefs settingDefs;
  private UsingDirectiveSyntax syntax;

  public UsingDef(SettingDefs settings, UsingDirectiveSyntax syntax, Position position) {
    super(position);
    this.settingDefs = settings;
    this.syntax = syntax;
  }

  public UsingDef() {}

  public void setSettingDefs(SettingDefs settingDefs) {
    this.settingDefs = settingDefs;
  }

  @Override
  public String toString() {
    return "UsingDef(" + settingDefs + ")";
  }

  public SettingDefs getSettingDefs() {
    return settingDefs;
  }

  public UsingDirectiveSyntax getSyntax() {
    return syntax;
  }
}
