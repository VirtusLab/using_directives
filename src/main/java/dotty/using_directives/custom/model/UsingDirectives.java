package dotty.using_directives.custom.model;

import dotty.using_directives.custom.utils.ast.UsingTree;

import java.util.List;
import java.util.Map;

public interface UsingDirectives {
    Map<String, ValueOrSetting<?>> getNestedMap();
    Map<Path, Value<?>> getFlattenedMap();
    UsingTree getAst();
    int getCodeOffset();
}
