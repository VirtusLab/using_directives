package com.virtuslab.using_directives.custom.utils.ast;

import com.virtuslab.using_directives.custom.utils.Position;
import java.util.List;

public class UsingDefs extends UsingTree {
  private List<UsingDef> usingDefs;
  private int codeOffset;
  private int codeStart;

  public UsingDefs(List<UsingDef> usingDefs, int codeOffset, int codeStart, Position position) {
    super(position);
    this.codeOffset = codeOffset;
    this.usingDefs = usingDefs;
    this.codeStart = codeStart;
  }

  public List<UsingDef> getUsingDefs() {
    return usingDefs;
  }

  public void setUsingDefs(List<UsingDef> usingDefs) {
    this.usingDefs = usingDefs;
  }

  public int getCodeOffset() {
    return codeOffset;
  }

  public int getCodeStart() {
    return codeStart;
  }

  @Override
  public String toString() {
    return "UsingDefs(" + usingDefs + ")";
  }
}
