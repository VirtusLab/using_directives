package json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.virtuslab.using_directives.custom.utils.ast.SettingDefOrUsingValue;
import com.virtuslab.using_directives.custom.utils.ast.UsingDef;
import java.lang.reflect.Type;

public class CustomUsingDefAdapter implements JsonSerializer<UsingDef> {
  @Override
  public JsonElement serialize(UsingDef src, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject jsonObj = new JsonObject();
    jsonObj.add(
        "settingDefs", context.serialize(src.getSettingDefs(), SettingDefOrUsingValue.class));
    return jsonObj;
  }
}
