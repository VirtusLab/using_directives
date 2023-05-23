package json;

import com.google.gson.*;
import com.virtuslab.using_directives.custom.utils.Position;
import com.virtuslab.using_directives.custom.utils.ast.*;
import java.lang.reflect.Type;

public class CustomUsingPrimitivesAdapter
    implements JsonDeserializer<UsingPrimitive>, JsonSerializer<UsingPrimitive> {
  @Override
  public UsingPrimitive deserialize(
      JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject jsonObj = json.getAsJsonObject();
    String type = jsonObj.get("type").getAsString();
    Position pos = context.deserialize(jsonObj.getAsJsonObject("position"), Position.class);
    JsonElement scopeJson = jsonObj.get("scope");
    String scope = null;
    if (scopeJson != null) scope = scopeJson.getAsString();
    if (type.equals("boolean")) {
      return new BooleanLiteral(jsonObj.get("value").getAsBoolean(), pos, scope);
    } else if (type.equals("string")) {
      return new StringLiteral(jsonObj.get("value").getAsString(), pos, scope, false);
    } else if (type.equals("stringDoubleQuotes")) {
      return new StringLiteral(jsonObj.get("value").getAsString(), pos, scope, true);
    } else if (type.equals("empty")) {
      return new EmptyLiteral(pos, scope);
    } else {
      return null;
    }
  }

  @Override
  public JsonElement serialize(
      UsingPrimitive src, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject jsonObj = new JsonObject();
    jsonObj.add("position", context.serialize(src.getPosition(), Position.class));
    if (src.getScope() != null) {
      jsonObj.addProperty("scope", src.getScope());
    }
    if (src instanceof BooleanLiteral) {
      jsonObj.addProperty("type", "boolean");
      jsonObj.addProperty("value", ((BooleanLiteral) src).getValue());
    } else if (src instanceof StringLiteral) {
      if (((StringLiteral) src).getIsWrappedDoubleQuotes()) {
        jsonObj.addProperty("type", "stringDoubleQuotes");
      } else {
        jsonObj.addProperty("type", "string");
      }
      jsonObj.addProperty("value", ((StringLiteral) src).getValue());
    } else if (src instanceof EmptyLiteral) {
      jsonObj.addProperty("type", "empty");
    }
    return jsonObj;
  }
}
