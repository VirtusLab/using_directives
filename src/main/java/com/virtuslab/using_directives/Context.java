package com.virtuslab.using_directives;

import com.virtuslab.using_directives.reporter.ConsoleReporter;
import com.virtuslab.using_directives.reporter.Reporter;

public class Context {
  private final Reporter reporter;

  public Context() {
    reporter = new ConsoleReporter();
  }

  public Context(Reporter reporter) {
    this.reporter = reporter;
  }

  public Reporter getReporter() {
    return reporter;
  }
}
