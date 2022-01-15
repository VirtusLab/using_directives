package json;

import com.google.gson.*;
import com.virtuslab.using_directives.custom.model.*;
import java.lang.reflect.Type;

public class CustomValueAdapter implements JsonDeserializer<Value<?>>, JsonSerializer<Value<?>> {
  @Override
  public Value<?> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    JsonObject jsonObj = json.getAsJsonObject();
    String type = jsonObj.get("type").getAsString();
    String scope = jsonObj.get("scope") != null ? jsonObj.get("scope").getAsString() : null;
    UsingDirectiveSyntax syntax = UsingDirectiveSyntax.Using;
    switch (type) {
      case "boolean":
        return new BooleanValue(jsonObj.get("value").getAsBoolean(), null, syntax, scope);
      case "numeric":
        return new NumericValue(jsonObj.get("value").getAsString(), null, syntax, scope);
      case "string":
        return new StringValue(jsonObj.get("value").getAsString(), null, syntax, scope);
      default:
        return null;
    }
  }

  @Override
  public JsonElement serialize(Value<?> src, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject jsonObj = new JsonObject();
    if (src.getScope() != null) jsonObj.addProperty("scope", src.getScope());
    if (src instanceof BooleanValue) {
      jsonObj.addProperty("type", "boolean");
      jsonObj.addProperty("value", ((BooleanValue) src).get());
    } else if (src instanceof NumericValue) {
      jsonObj.addProperty("type", "numeric");
      jsonObj.addProperty("value", ((NumericValue) src).get());
    } else if (src instanceof StringValue) {
      jsonObj.addProperty("type", "string");
      jsonObj.addProperty("value", ((StringValue) src).get());
    }
    return jsonObj;
  }
}
