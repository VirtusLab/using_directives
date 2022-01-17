package com.virtuslab.using_directives.custom.model;

import com.virtuslab.using_directives.custom.utils.ast.UsingTree;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UsingDirectivesImpl implements UsingDirectives {
  private final Map<String, ValueOrSetting<?>> nestedMap;
  private final Map<Path, List<Value<?>>> flattenedMap;
  private final UsingTree ast;
  private final int codeOffset;
  private final UsingDirectiveKind kind;

  public UsingDirectivesImpl(
      Map<String, ValueOrSetting<?>> nestedMap,
      Map<Path, List<Value<?>>> flattenedMap,
      UsingTree ast,
      int codeOffset,
      UsingDirectiveKind kind) {
    this.nestedMap = nestedMap;
    this.flattenedMap = flattenedMap;
    this.ast = ast;
    this.codeOffset = codeOffset;
    this.kind = kind;
  }

  @Override
  public Map<Path, List<Value<?>>> getFlattenedMap() {
    return flattenedMap;
  }

  @Override
  public Map<String, ValueOrSetting<?>> getNestedMap() {
    return nestedMap;
  }

  @Override
  public UsingTree getAst() {
    return ast;
  }

  @Override
  public int getCodeOffset() {
    return codeOffset;
  }

  @Override
  public UsingDirectiveKind getKind() {
    return kind;
  }

  @Override
  public String toString() {
    return "UsingDirectivesImpl("
        + kind
        + "){"
        + flattenedMap.entrySet().stream()
            .map(
                e ->
                    e.getKey().toString()
                        + ": "
                        + e.getValue().stream()
                            .map(Value::toString)
                            .collect(Collectors.joining(", ", "[", "]")))
            .collect(Collectors.joining(",\n\t", "\n\t", "\n"))
        + "}";
  }
}
