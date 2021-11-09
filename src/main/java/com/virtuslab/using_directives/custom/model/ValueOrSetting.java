package com.virtuslab.using_directives.custom.model;

import com.virtuslab.using_directives.custom.utils.ast.UsingTree;

public interface ValueOrSetting<T> {
  T get();

  UsingTree getRelatedASTNode();
}
