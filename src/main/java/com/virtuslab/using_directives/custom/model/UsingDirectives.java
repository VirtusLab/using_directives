package com.virtuslab.using_directives.custom.model;

import com.virtuslab.using_directives.custom.utils.ast.UsingTree;

import java.util.List;
import java.util.Map;

public interface UsingDirectives {
    Map<String, ValueOrSetting<?>> getNestedMap();
    Map<Path, List<Value<?>>> getFlattenedMap();
    UsingTree getAst();
    int getCodeOffset();
}
