package com.virtuslab.using_directives.custom.model;

public enum UsingDirectiveSyntax {
  Using("using"), // e.g: using foo 1
  Require("require"), // require foo
  AtRequire("@require"); // @require foo

  private String keyword;

  UsingDirectiveSyntax(String keyword) {
    this.keyword = keyword;
  }

  public String getKeyword() {
    return keyword;
  }
}
