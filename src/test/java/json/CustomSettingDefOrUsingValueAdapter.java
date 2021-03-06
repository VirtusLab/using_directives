package json;

import com.google.gson.*;
import com.virtuslab.using_directives.custom.utils.ast.SettingDefOrUsingValue;
import com.virtuslab.using_directives.custom.utils.ast.SettingDefs;
import com.virtuslab.using_directives.custom.utils.ast.UsingValue;
import java.lang.reflect.Type;

public class CustomSettingDefOrUsingValueAdapter
    implements JsonDeserializer<SettingDefOrUsingValue>, JsonSerializer<SettingDefOrUsingValue> {
  @Override
  public SettingDefOrUsingValue deserialize(
      JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    if (json.getAsJsonObject().has("settings")) {
      return context.deserialize(json.getAsJsonObject(), SettingDefs.class);
    } else {
      return context.deserialize(json.getAsJsonObject(), UsingValue.class);
    }
  }

  @Override
  public JsonElement serialize(
      SettingDefOrUsingValue src, Type typeOfSrc, JsonSerializationContext context) {
    if (src instanceof SettingDefs) {
      return context.serialize(src, SettingDefs.class);
    } else {
      return context.serialize(src, UsingValue.class);
    }
  }
}
