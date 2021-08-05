package dotty.using_directives.custom;

import dotty.using_directives.custom.model.*;
import dotty.using_directives.custom.utils.KeyValue;
import dotty.using_directives.custom.utils.ast.*;

import java.util.*;
import java.util.stream.Collectors;

public class Visitor {
    public UsingDirectives visit(UsingTree root) {
        Map<String, ValueOrSetting<?>> rawView = new HashMap<>(visitSettings(root));
        Map<Path, Value<?>> flattenView = flatten(rawView);
        Map<String, ValueOrSetting<?>> nestedView = nest(rawView);
        return new UsingDirectivesImpl(nestedView, flattenView);
    }

    private Map<String, ValueOrSetting<?>> visitSettings(UsingTree root) {
        if(root instanceof UsingDefs) {
            return ((UsingDefs) root)
                    .getUsingDefs()
                    .stream()
                    .flatMap(ud -> visitSettings(ud).entrySet().stream())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, this::merge));
        }
        else if(root instanceof UsingDef) {
            return visitSettings(((UsingDef) root).getSettingDefs());
        }
        else if(root instanceof SettingDefs) {
            return ((SettingDefs) root)
                    .getSettings()
                    .stream()
                    .map(this::visitSetting)
                    .collect(Collectors.toMap(KeyValue::getKey, KeyValue::getValue, this::merge));

        }
        else if(root instanceof SettingDef) {
            Map<String, ValueOrSetting<?>> map = new HashMap<>();
            KeyValue<String, ValueOrSetting<?>> keyValue = visitSetting((SettingDef) root);
            map.put(keyValue.getKey(), keyValue.getValue());
            return map;
        }
        else {
            //report error
            return null;
        }
    }

    private KeyValue<String, ValueOrSetting<?>> visitSetting(SettingDef setting) {
        String key = setting.getKey();
        ValueOrSetting<?> v;
        if(setting.getValue() instanceof SettingDefs) {
            v = new Setting(visitSettings(setting.getValue()));
        }
        else {
            v = parseValue((UsingValue) setting.getValue());
        }
        return new KeyValue<>(key, v);
    }

    private Value<?> parseValue(UsingValue value) {
        if(value instanceof UsingPrimitive) {
            if(value instanceof BooleanLiteral) {
                return new BooleanValue(((BooleanLiteral) value).getValue());
            }
            else if(value instanceof NumericLiteral) {
                return new NumericValue(((NumericLiteral) value).getValue());
            }
            else {
                return new StringValue(((StringLiteral) value).getValue());
            }
        }
        else {
            return parseValues((UsingValues) value);
        }
    }

    private ListValue parseValues(UsingValues value) {
        return new ListValue(value.values.stream().map(this::parseValue).collect(Collectors.toList()));
    }

    private ValueOrSetting<?> merge(ValueOrSetting<?> v1, ValueOrSetting<?> v2) {
        if(v1 instanceof Value<?> && v2 instanceof Value<?>) {
            // report warning
            return v1;
        }
        else if(v1 instanceof Value<?> || v2 instanceof Value<?>) {
            // report error
            return v1 instanceof Value<?> ? v1 : v2;
        }
        else {
            Setting s1 = (Setting) v1;
            Setting s2 = (Setting) v2;
            s2.get().forEach((key, value) -> s1.get().merge(key, value, this::merge));
            return s1;
        }
    }

    private Value<?> merge(Value<?> v1, Value<?> v2) {
        // report warning
        return v1;
    }

    private Map<String, ValueOrSetting<?>> nest(Map<String, ValueOrSetting<?>> raw) {
        // TODO: Implement merge
        return raw;
    }

    private Map<Path, Value<?>> flatten(Map<String, ValueOrSetting<?>> raw) {
        return flattenHelper(raw).entrySet().stream()
                .collect(
                        Collectors.toMap(e ->
                                new Path(Arrays.asList(e.getKey().split("\\."))), Map.Entry::getValue
                        )
                );
    }


    private Map<String, Value<?>> flattenHelper(Map<String, ValueOrSetting<?>> raw) {
        return raw.entrySet().stream().flatMap(e -> {
            if(e.getValue() instanceof Setting) {
                Setting s = (Setting) e.getValue();
                Map<String, Value<?>> flattenedChild = flattenHelper(s.get());
                Map<String, Value<?>> updatedFlattenedChild = new HashMap<>();
                flattenedChild.forEach((k, v) -> updatedFlattenedChild.merge(String.format("%s.%s", e.getKey(), k), v, this::merge));
                return updatedFlattenedChild.entrySet().stream();
            }
            else {
                Map<String, Value<?>> map = new HashMap<>();
                map.put(e.getKey(), (Value<?>) e.getValue());
                return map.entrySet().stream();
            }
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,this::merge));
    }
}
