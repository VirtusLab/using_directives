package com.virtuslab.using_directives;

import com.virtuslab.using_directives.custom.CommentExtractor;
import com.virtuslab.using_directives.custom.Parser;
import com.virtuslab.using_directives.custom.Visitor;
import com.virtuslab.using_directives.custom.model.UsingDirectives;
import com.virtuslab.using_directives.custom.utils.CommentSource;
import com.virtuslab.using_directives.custom.utils.Source;
import com.virtuslab.using_directives.custom.utils.ast.UsingDefs;

public class UsingDirectivesProcessor {
  private Context context;

  public UsingDirectivesProcessor(Context context) {
    this.context = context;
  }

  public UsingDirectivesProcessor() {
    this.context = new Context();
  }

  public UsingDirectives extract(char[] content) {
    // Try to extract comments from the start of file
    CommentExtractor extractor = new CommentExtractor(content);
    CommentSource cs = extractor.getCommentSource();

    context.getReporter().reset();

    // Parse comment and standard syntax
    UsingDefs astFromComments = new Parser(cs, context).parse();
    UsingDefs ast = new Parser(new Source(content), context).parse();

    // If standard syntax is empty, fallback to comments
    if (ast.getUsingDefs().isEmpty()) {
      return new Visitor(astFromComments, context).visit();
    } else {
      return new Visitor(ast, context).visit();
    }
  }

  public Context getContext() {
    return context;
  }

  public void setContext(Context context) {
    this.context = context;
  }
}
