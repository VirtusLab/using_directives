package com.virtuslab.using_directives;

import com.virtuslab.using_directives.config.Settings;
import com.virtuslab.using_directives.reporter.ConsoleReporter;
import com.virtuslab.using_directives.reporter.Reporter;

public class Context {
    private Reporter reporter;

    private Settings settings;

    public Context() {
        reporter = new ConsoleReporter();
        settings = new Settings();
    }

    public Context(Reporter reporter, Settings settings) {
        this.reporter = reporter;
        this.settings = settings;
    }

    public Reporter getReporter() {
        return reporter;
    }

    public void setReporter(Reporter reporter) {
        this.reporter = reporter;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }
}
