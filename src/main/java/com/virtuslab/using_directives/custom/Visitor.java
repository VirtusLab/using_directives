package com.virtuslab.using_directives.custom;

import com.virtuslab.using_directives.custom.model.*;
import com.virtuslab.using_directives.reporter.ConsoleReporter;
import com.virtuslab.using_directives.custom.utils.KeyValue;
import com.virtuslab.using_directives.custom.utils.ast.*;
import com.virtuslab.using_directives.reporter.Reporter;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

public class Visitor {
    private final UsingTree root;
    private Reporter reporter;

    public Visitor(UsingTree root, Reporter reporter) {
        this.reporter = reporter;
        this.root = root;
    }

    public Visitor(UsingTree root) {
        this(root, new ConsoleReporter());
    }

    public UsingTree getRoot() {
        return root;
    }

    public Reporter getReporter() {
        return reporter;
    }

    public void setReporter(Reporter reporter) {
        this.reporter = reporter;
    }

    public UsingDirectives visit() {
        Map<Path, Value<?>> flattenView = getFlatView(root);
        int codeOffset;
        if(root instanceof UsingDefs) {
            codeOffset = ((UsingDefs) root).getCodeOffset();
        } else {
            codeOffset = -1;
        }
        return new UsingDirectivesImpl(null, flattenView, root, codeOffset);
    }

    private Map<String, Value<?>> visitSettingsFlat(UsingTree root) {
        if(root instanceof UsingDefs) {
            return ((UsingDefs) root)
                    .getUsingDefs()
                    .stream()
                    .flatMap(ud -> visitSettingsFlat(ud).entrySet().stream())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            this::merge
                    ));
        }
        else if(root instanceof UsingDef) {
            return visitSettingsFlat(((UsingDef) root).getSettingDefs());
        }
        else if(root instanceof SettingDefs) {
            return ((SettingDefs) root)
                    .getSettings()
                    .stream()
                    .flatMap(s -> visitSettingFlat(s).stream())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            this::merge
                    ));

        }
        else if(root instanceof SettingDef) {
            Map<String, Value<?>> map = new HashMap<>();
            List<KeyValue<String, Value<?>>> keyValueList = visitSettingFlat((SettingDef) root);
            keyValueList.forEach(kv -> map.merge(kv.getKey(), kv.getValue(), this::merge));
            return map;
        }
        else {
            reporter.error(root.getPosition(), "Provided AST cannot be processed.");
            return null;
        }
    }

    private List<KeyValue<String, Value<?>>> visitSettingFlat(SettingDef setting) {
        String key = setting.getKey();
        if(setting.getValue() instanceof SettingDefs) {
            List<KeyValue<String, Value<?>>> flatList = ((SettingDefs) setting.getValue()).getSettings().stream()
                    .flatMap(s -> visitSettingFlat(s).stream())
                    .map(kv -> kv.withNewKey(String.format("%s.%s", key, kv.getKey())))
                    .collect(Collectors.toList());
            return flatList;
        }
        else {
            return new ArrayList<>(
                    Collections.singletonList(new KeyValue<>(key, parseValue((UsingValue) setting.getValue())))
            );
        }
    }

//    private Map<String, ValueOrSetting<?>> visitSettings(UsingTree root) {
//        if(root instanceof UsingDefs) {
//            return ((UsingDefs) root)
//                    .getUsingDefs()
//                    .stream()
//                    .flatMap(ud -> visitSettings(ud).entrySet().stream())
//                    .collect(Collectors.toMap(
//                            Map.Entry::getKey,
//                            Map.Entry::getValue,
//                            mergeFunc()
//                    ));
//        }
//        else if(root instanceof UsingDef) {
//            return visitSettings(((UsingDef) root).getSettingDefs());
//        }
//        else if(root instanceof SettingDefs) {
//            return ((SettingDefs) root)
//                    .getSettings()
//                    .stream()
//                    .map(this::visitSetting)
//                    .collect(Collectors.toMap(
//                            Map.Entry::getKey,
//                            Map.Entry::getValue,
//                            mergeFunc()
//                    ));
//
//        }
//        else if(root instanceof SettingDef) {
//            Map<String, ValueOrSetting<?>> map = new HashMap<>();
//            KeyValue<String, ValueOrSetting<?>> keyValue = visitSetting((SettingDef) root);
//            map.put(keyValue.getKey(), keyValue.getValue());
//            return map;
//        }
//        else {
//            reporter.error(root.getPosition(), "Provided AST cannot be processed.");
//            return null;
//        }
//    }
//
//    private KeyValue<String, ValueOrSetting<?>> visitSetting(SettingDef setting) {
//        String key = setting.getKey();
//        ValueOrSetting<?> v;
//        if(setting.getValue() instanceof SettingDefs) {
//            v = new Setting(visitSettings(setting.getValue()));
//        }
//        else {
//            v = parseValue((UsingValue) setting.getValue());
//        }
//        return new KeyValue<>(key, v);
//    }

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

    private BinaryOperator<ValueOrSetting<?>> mergeFunc() {
        return (v1, v2) -> {
            if(v1 instanceof Value<?> && v2 instanceof Value<?>) {
                return merge((Value<?>) v1,(Value<?>)  v2);
            }
            else if(v1 instanceof Value<?> || v2 instanceof Value<?>) {
                return null;
            }
            else {
                Setting s1 = (Setting) v1;
                Setting s2 = (Setting) v2;
                s2.get().forEach(
                        (key, value) -> s1.get().merge(key, value, mergeFunc())
                );
                return s1;
            }
        };
    }

    private Value<?> merge(Value<?> v1, Value<?> v2) {
        if(v1 instanceof ListValue && v2 instanceof ListValue) {
            ArrayList<Value<?>> newList = new ArrayList<>(((ListValue) v1).get());
            newList.addAll(((ListValue) v2).get());
            return new ListValue(newList);
        } else if(v1 instanceof ListValue) {
            ArrayList<Value<?>> newList = new ArrayList<>(((ListValue) v1).get());
            newList.add(v2);
            return new ListValue(newList);
        } else if(v2 instanceof ListValue) {
            ArrayList<Value<?>> newList = new ArrayList<>(((ListValue) v2).get());
            newList.add(v1);
            return new ListValue(newList);
        } else {
            return new ListValue(new ArrayList<>(Arrays.asList(v1, v2)));
        }
    }

    private Map<String, ValueOrSetting<?>> nest(Map<String, ValueOrSetting<?>> raw) {
        return raw.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
    }

    private Map<Path, Value<?>> getFlatView(UsingTree root) {
        Map<String, Value<?>> intermediate = visitSettingsFlat(root);
        return intermediate.entrySet().stream()
                .collect(
                        Collectors.toMap(e ->
                                new Path(Arrays.asList(e.getKey().split("\\."))), Map.Entry::getValue
                        )
                );
    }
}
