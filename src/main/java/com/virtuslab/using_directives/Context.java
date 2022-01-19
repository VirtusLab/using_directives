package com.virtuslab.using_directives;

import com.virtuslab.using_directives.config.Settings;
import com.virtuslab.using_directives.reporter.ConsoleReporter;
import com.virtuslab.using_directives.reporter.Reporter;

public class Context {
  private final Reporter reporter;

  private final Settings settings;

  public Context() {
    reporter = new ConsoleReporter();
    settings = new Settings();
  }

  public Context(Reporter reporter, Settings settings) {
    this.reporter = reporter;
    this.settings = settings;
  }

  public Context(Reporter reporter) {
    this.reporter = reporter;
    this.settings = new Settings();
  }

  public Context(Settings settings) {
    this.reporter = new ConsoleReporter();
    this.settings = settings;
  }

  public Reporter getReporter() {
    return reporter;
  }

  public Settings getSettings() {
    return settings;
  }
}
