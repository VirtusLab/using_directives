package dotty.using_directives.custom;

import dotty.using_directives.custom.model.UsingDirectives;
import dotty.using_directives.custom.reporter.ConsoleReporter;
import dotty.using_directives.custom.reporter.Reporter;
import dotty.using_directives.custom.utils.Source;
import dotty.using_directives.custom.utils.ast.UsingTree;

public class UsingDirectivesProcessor {
    private Reporter reporter;

    public UsingDirectivesProcessor(Reporter reporter) {
        this.reporter = reporter;
    }

    public UsingDirectivesProcessor() {
        this.reporter = new ConsoleReporter();
    }

    public UsingDirectives extract(char[] content) {
        reporter.reset();
        UsingTree ast = new Parser(new Source(content), reporter).parse();
        return new Visitor(ast, reporter).visit();
    }

    public Reporter getReporter() {
        return reporter;
    }

    public void setReporter(Reporter reporter) {
        this.reporter = reporter;
    }
}
