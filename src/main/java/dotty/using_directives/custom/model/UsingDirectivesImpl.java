package dotty.using_directives.custom.model;

import java.util.List;
import java.util.Map;

public class UsingDirectivesImpl implements UsingDirectives {
    private final Map<String, ValueOrSetting<?>> nestedMap;
    private final Map<Path, Value<?>> flattenedMap;

    public UsingDirectivesImpl(Map<String, ValueOrSetting<?>> nestedMap, Map<Path, Value<?>> flattenedMap) {
        this.nestedMap = nestedMap;
        this.flattenedMap = flattenedMap;
    }


    @Override
    public Map<Path, Value<?>> getFlattenedMap() {
        return flattenedMap;
    }

    @Override
    public Map<String, ValueOrSetting<?>> getNestedMap() {
        return nestedMap;
    }
}
