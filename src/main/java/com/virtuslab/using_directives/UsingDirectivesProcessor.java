package com.virtuslab.using_directives;

import com.virtuslab.using_directives.custom.*;
import com.virtuslab.using_directives.config.Settings;
import com.virtuslab.using_directives.custom.model.UsingDirectives;
import com.virtuslab.using_directives.reporter.ConsoleReporter;
import com.virtuslab.using_directives.reporter.Reporter;
import com.virtuslab.using_directives.custom.utils.Source;
import com.virtuslab.using_directives.custom.utils.ast.UsingTree;

public class UsingDirectivesProcessor {
    private Context context;

    public UsingDirectivesProcessor(Context context) {
        this.context = context;
    }

    public UsingDirectivesProcessor() {
        this.context = new Context();
    }

    public UsingDirectives extract(char[] content) {
        context.getReporter().reset();
        UsingTree ast = new Parser(new Source(content), context).parse();
        return new Visitor(ast, context).visit();
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
