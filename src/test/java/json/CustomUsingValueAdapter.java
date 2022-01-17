package json;

import com.google.gson.*;
import com.virtuslab.using_directives.custom.model.UsingDirectiveSyntax;
import com.virtuslab.using_directives.custom.utils.Position;
import com.virtuslab.using_directives.custom.utils.ast.UsingPrimitive;
import com.virtuslab.using_directives.custom.utils.ast.UsingValue;
import com.virtuslab.using_directives.custom.utils.ast.UsingValues;
import java.lang.reflect.Type;
import java.util.Arrays;

public class CustomUsingValueAdapter
    implements JsonDeserializer<UsingValue>, JsonSerializer<UsingValue> {

  @Override
  public UsingValue deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject jsonObj = json.getAsJsonObject();
    String type = jsonObj.get("type").getAsString();
    Position pos = context.deserialize(jsonObj.getAsJsonObject("position"), Position.class);
    UsingDirectiveSyntax syntax = UsingDirectiveSyntax.Using;

    if (type.equals("list")) {
      UsingPrimitive[] values =
          context.deserialize(jsonObj.get("value").getAsJsonArray(), UsingPrimitive[].class);
      return new UsingValues(Arrays.asList(values), pos);
    } else {
      return context.deserialize(jsonObj, UsingPrimitive.class);
    }
  }

  @Override
  public JsonElement serialize(UsingValue src, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject jsonObj = new JsonObject();
    jsonObj.add("position", context.serialize(src.getPosition(), Position.class));
    if (src instanceof UsingValues) {
      jsonObj.addProperty("type", "list");
      JsonElement serializedValues =
          context.serialize(((UsingValues) src).getValues().toArray(), UsingPrimitive[].class);
      jsonObj.add("value", serializedValues);
      return jsonObj;
    } else {
      return context.serialize(src, UsingPrimitive.class);
    }
  }
}
