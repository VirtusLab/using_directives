package com.virtuslab.using_directives;

import com.virtuslab.using_directives.custom.Parser;
import com.virtuslab.using_directives.custom.SimpleCommentExtractor;
import com.virtuslab.using_directives.custom.Visitor;
import com.virtuslab.using_directives.custom.model.UsingDirectiveKind;
import com.virtuslab.using_directives.custom.model.UsingDirectives;
import com.virtuslab.using_directives.custom.utils.Source;
import com.virtuslab.using_directives.custom.utils.ast.UsingDefs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UsingDirectivesProcessor {
    private Context context;

    public UsingDirectivesProcessor(Context context) {
        this.context = context;
    }

    public UsingDirectivesProcessor() {
        this.context = new Context();
    }

    private UsingDirectives extractFromComment(char[] content, boolean commentIndicator) {
        SimpleCommentExtractor extractor = new SimpleCommentExtractor(content, commentIndicator);
        char[] comments = extractor.extractComments();
        UsingDefs ast = new Parser(new Source(comments), context).parse();
        return new Visitor(ast, context)
                .visit(
                        commentIndicator ? UsingDirectiveKind.SpecialComment : UsingDirectiveKind.PlainComment);
    }

    public List<UsingDirectives> extract(
            char[] content, boolean specialComments, boolean plainComments) {
        List<UsingDirectives> result = new ArrayList<>();
        UsingDefs ast = new Parser(new Source(content), context).parse();
        result.add(new Visitor(ast, context).visit(UsingDirectiveKind.Code));
        if (specialComments || plainComments) {
            // this could be done better...
            char[] commentContent = Arrays.copyOfRange(content, 0, ast.getCodeStart());

            if (specialComments) result.add(extractFromComment(commentContent, true));
            if (plainComments) result.add(extractFromComment(commentContent, false));
        }
        return result;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
