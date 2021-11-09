package com.virtuslab.using_directives.custom.utils.ast;

import com.virtuslab.using_directives.custom.utils.Position;
import java.util.List;

public class UsingValues extends UsingValue {
  public List<UsingPrimitive> values;

  public UsingValues(List<UsingPrimitive> values, Position position) {
    super(position);
    this.values = values;
  }

  public UsingValues() {}

  public List<UsingPrimitive> getValues() {
    return values;
  }

  public void setValues(List<UsingPrimitive> values) {
    this.values = values;
  }

  @Override
  public String toString() {
    return "UsingValues(" + values + ')';
  }
}
