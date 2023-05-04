package com.virtuslab.using_directives.custom;

import com.virtuslab.using_directives.custom.model.*;
import com.virtuslab.using_directives.custom.utils.KeyValue;
import com.virtuslab.using_directives.custom.utils.ast.*;
import com.virtuslab.using_directives.reporter.Reporter;
import java.util.*;
import java.util.stream.Collectors;

public class Visitor {
  private final UsingTree root;
  private final Reporter reporter;

  public Visitor(UsingTree root, Reporter reporter) {
    this.reporter = reporter;
    this.root = root;
  }

  public UsingTree getRoot() {
    return root;
  }

  public Reporter getReporter() {
    return reporter;
  }

  public UsingDirectives visit() {
    Map<Path, List<Value<?>>> flattenView = getFlatView(root);
    int codeOffset;
    if (root instanceof UsingDefs) {
      codeOffset = ((UsingDefs) root).getCodeOffset();
    } else {
      codeOffset = -1;
    }
    return new UsingDirectivesImpl(null, flattenView, root, codeOffset);
  }

  private Map<String, List<Value<?>>> visitUsingsFlat(UsingTree root) {
    if (root instanceof UsingDefs) {
      return ((UsingDefs) root)
          .getUsingDefs().stream()
              .flatMap(ud -> visitUsingsFlat(ud).entrySet().stream())
              .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, this::merge));
    } else if (root instanceof UsingDef) {
      Map<String, List<Value<?>>> map = new HashMap<>();
      List<KeyValue<String, List<Value<?>>>> keyValueList = visitSettingFlat((UsingDef) root);
      keyValueList.forEach(kv -> map.merge(kv.getKey(), kv.getValue(), this::merge));
      return map;
    } else {
      reporter.error(root.getPosition(), "Provided AST cannot be processed.");
      return null;
    }
  }

  private List<KeyValue<String, List<Value<?>>>> visitSettingFlat(UsingDef using) {
    String key = using.getKey();
    if (using.getValue() == null) {
      return new ArrayList<>();
    } else {
      return new ArrayList<>(
          Collections.singletonList(
              new KeyValue<>(key, parseValue((UsingValue) using.getValue()))));
    }
  }

  private List<Value<?>> parseValue(UsingValue value) {
    if (value instanceof UsingPrimitive) {
      List<Value<?>> lst = new ArrayList<>();
      if (value instanceof BooleanLiteral) {
        lst.add(
            new BooleanValue(
                ((BooleanLiteral) value).getValue(), value, ((UsingPrimitive) value).getScope()));
      } else if (value instanceof StringLiteral) {
        lst.add(
            new StringValue(
                ((StringLiteral) value).getValue(), value, ((UsingPrimitive) value).getScope()));
      } else {
        lst.add(new EmptyValue(value, ((UsingPrimitive) value).getScope()));
      }
      return lst;
    } else {
      return parseValues((UsingValues) value);
    }
  }

  private List<Value<?>> parseValues(UsingValues value) {

    return value.values.stream().flatMap(p -> parseValue(p).stream()).collect(Collectors.toList());
  }

  private List<Value<?>> merge(List<Value<?>> v1, List<Value<?>> v2) {
    List<Value<?>> copied = new ArrayList<>(v1);
    copied.addAll(v2);
    return copied;
  }

  private Map<Path, List<Value<?>>> getFlatView(UsingTree root) {
    Map<String, List<Value<?>>> intermediate = visitUsingsFlat(root);
    return intermediate.entrySet().stream()
        .collect(
            Collectors.toMap(
                e -> new Path(Arrays.asList(e.getKey().split("\\."))), Map.Entry::getValue));
  }
}
