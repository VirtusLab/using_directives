package com.virtuslab.using_directives.custom;

import com.virtuslab.using_directives.Context;
import com.virtuslab.using_directives.custom.model.*;
import com.virtuslab.using_directives.custom.utils.KeyValue;
import com.virtuslab.using_directives.custom.utils.ast.*;
import java.util.*;
import java.util.stream.Collectors;

public class Visitor {
  private final UsingTree root;
  private final Context context;

  public Visitor(UsingTree root, Context context) {
    this.context = context;
    this.root = root;
  }

  public UsingTree getRoot() {
    return root;
  }

  public Context getContext() {
    return context;
  }

  public UsingDirectives visit() {
    Map<Path, List<Value<?>>> flattenView = getFlatView(root);
    int codeOffset;
    if (root instanceof UsingDefs) {
      codeOffset = ((UsingDefs) root).getCodeOffset();
    } else {
      codeOffset = -1;
    }
    return new UsingDirectivesImpl(null, flattenView, root, codeOffset);
  }

  private Map<String, List<Value<?>>> visitSettingsFlat(UsingTree root) {
    if (root instanceof UsingDefs) {
      return ((UsingDefs) root)
          .getUsingDefs().stream()
              .flatMap(ud -> visitSettingsFlat(ud).entrySet().stream())
              .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, this::merge));
    } else if (root instanceof UsingDef) {
      return visitSettingsFlat(((UsingDef) root).getSettingDefs());
    } else if (root instanceof SettingDefs) {
      return ((SettingDefs) root)
          .getSettings().stream()
              .flatMap(s -> visitSettingFlat(s).stream())
              .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, this::merge));

    } else if (root instanceof SettingDef) {
      Map<String, List<Value<?>>> map = new HashMap<>();
      List<KeyValue<String, List<Value<?>>>> keyValueList = visitSettingFlat((SettingDef) root);
      keyValueList.forEach(kv -> map.merge(kv.getKey(), kv.getValue(), this::merge));
      return map;
    } else {
      context.getReporter().error(root.getPosition(), "Provided AST cannot be processed.");
      return null;
    }
  }

  private List<KeyValue<String, List<Value<?>>>> visitSettingFlat(SettingDef setting) {
    String key = setting.getKey();
    if (setting.getValue() instanceof SettingDefs) {
      return ((SettingDefs) setting.getValue())
          .getSettings().stream()
              .flatMap(s -> visitSettingFlat(s).stream())
              .map(kv -> kv.withNewKey(String.format("%s.%s", key, kv.getKey())))
              .collect(Collectors.toList());
    } else {
      return new ArrayList<>(
          Collections.singletonList(
              new KeyValue<>(key, parseValue((UsingValue) setting.getValue()))));
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

  private List<Value<?>> parseValue(UsingValue value) {
    if (value instanceof UsingPrimitive) {
      List<Value<?>> lst = new ArrayList<>();
      if (value instanceof BooleanLiteral) {
        lst.add(new BooleanValue(((BooleanLiteral) value).getValue()));
      } else if (value instanceof NumericLiteral) {
        lst.add(new NumericValue(((NumericLiteral) value).getValue()));
      } else {
        lst.add(new StringValue(((StringLiteral) value).getValue()));
      }
      return lst;
    } else {
      return parseValues((UsingValues) value);
    }
  }

  private List<Value<?>> parseValues(UsingValues value) {
    return value.values.stream().flatMap(p -> parseValue(p).stream()).collect(Collectors.toList());
  }

  private List<Value<?>> merge(List<Value<?>> v1, List<Value<?>> v2) {
    List<Value<?>> copied = new ArrayList<>(v1);
    copied.addAll(v2);
    return copied;
  }

  private Map<String, ValueOrSetting<?>> nest(Map<String, ValueOrSetting<?>> raw) {
    return raw.entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  private Map<Path, List<Value<?>>> getFlatView(UsingTree root) {
    Map<String, List<Value<?>>> intermediate = visitSettingsFlat(root);
    return intermediate.entrySet().stream()
        .collect(
            Collectors.toMap(
                e -> new Path(Arrays.asList(e.getKey().split("\\."))), Map.Entry::getValue));
  }
}
