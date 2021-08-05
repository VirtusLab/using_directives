package dotty.using_directives.custom.model;

import java.util.List;
import java.util.Map;

public interface UsingDirectives {
    Map<String, ValueOrSetting<?>> getNestedMap();
    Map<Path, Value<?>> getFlattenedMap();
}
