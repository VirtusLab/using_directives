package dotty.using_directives.custom.model;

import dotty.using_directives.custom.utils.ast.UsingTree;

import java.util.Map;

public class UsingDirectivesImpl implements UsingDirectives {
    private final Map<String, ValueOrSetting<?>> nestedMap;
    private final Map<Path, Value<?>> flattenedMap;
    private final UsingTree ast;
    private final int codeOffset;

    public UsingDirectivesImpl(Map<String, ValueOrSetting<?>> nestedMap, Map<Path, Value<?>> flattenedMap, UsingTree ast, int codeOffset) {
        this.nestedMap = nestedMap;
        this.flattenedMap = flattenedMap;
        this.ast = ast;
        this.codeOffset = codeOffset;
    }


    @Override
    public Map<Path, Value<?>> getFlattenedMap() {
        return flattenedMap;
    }

    @Override
    public Map<String, ValueOrSetting<?>> getNestedMap() {
        return nestedMap;
    }

    @Override
    public UsingTree getAst() {
        return ast;
    }

    @Override
    public int getCodeOffset() {
        return codeOffset;
    }
}
