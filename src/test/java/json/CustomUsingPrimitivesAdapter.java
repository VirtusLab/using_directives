package json;

import com.google.gson.*;
import com.virtuslab.using_directives.custom.utils.Position;
import com.virtuslab.using_directives.custom.utils.ast.BooleanLiteral;
import com.virtuslab.using_directives.custom.utils.ast.NumericLiteral;
import com.virtuslab.using_directives.custom.utils.ast.StringLiteral;
import com.virtuslab.using_directives.custom.utils.ast.UsingPrimitive;
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
    if (type.equals("boolean")) {
      return new BooleanLiteral(jsonObj.get("value").getAsBoolean(), pos);
    } else if (type.equals("numeric")) {
      return new NumericLiteral(jsonObj.get("value").getAsString(), pos);
    } else if (type.equals("string")) {
      return new StringLiteral(jsonObj.get("value").getAsString(), pos);
    } else {
      return null;
    }
  }

  @Override
  public JsonElement serialize(
      UsingPrimitive src, Type typeOfSrc, JsonSerializationContext context) {
    JsonObject jsonObj = new JsonObject();
    jsonObj.add("position", context.serialize(src.getPosition(), Position.class));
    if (src instanceof BooleanLiteral) {
      jsonObj.addProperty("type", "boolean");
      jsonObj.addProperty("value", ((BooleanLiteral) src).getValue());
    } else if (src instanceof NumericLiteral) {
      jsonObj.addProperty("type", "numeric");
      jsonObj.addProperty("value", ((NumericLiteral) src).getValue());
    } else if (src instanceof StringLiteral) {
      jsonObj.addProperty("type", "string");
      jsonObj.addProperty("value", ((StringLiteral) src).getValue());
    }
    return jsonObj;
  }
}
