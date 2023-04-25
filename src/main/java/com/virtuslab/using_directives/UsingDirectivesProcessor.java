package com.virtuslab.using_directives;

import com.virtuslab.using_directives.custom.Parser;
import com.virtuslab.using_directives.custom.SimpleCommentExtractor;
import com.virtuslab.using_directives.custom.Visitor;
import com.virtuslab.using_directives.custom.model.UsingDirectives;
import com.virtuslab.using_directives.custom.utils.Source;
import com.virtuslab.using_directives.custom.utils.ast.UsingDefs;
import com.virtuslab.using_directives.reporter.ConsoleReporter;
import com.virtuslab.using_directives.reporter.Reporter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UsingDirectivesProcessor {
  private Reporter reporter;

  public UsingDirectivesProcessor(Reporter reporter) {
    this.reporter = reporter;
  }

  public UsingDirectivesProcessor() {
    this.reporter = new ConsoleReporter();
  }

  private UsingDirectives extractFromComment(char[] content, boolean commentIndicator) {
    SimpleCommentExtractor extractor = new SimpleCommentExtractor(content, commentIndicator);
    UsingDefs ast = new Parser(new Source(extractor.extractComments()), reporter).parse();
    return new Visitor(ast, reporter).visit();
  }

  public List<UsingDirectives> extract(char[] content) {
    List<UsingDirectives> result = new ArrayList<>();
    UsingDefs ast = new Parser(new Source(content), reporter).parse();
    // this could be done better...
    char[] commentContent = Arrays.copyOfRange(content, 0, ast.getCodeStart());

    result.add(extractFromComment(commentContent, true));

    return result;
  }

  public Reporter getReporter() {
    return reporter;
  }

  public void setReporter(Reporter reporter) {
    this.reporter = reporter;
  }
}
