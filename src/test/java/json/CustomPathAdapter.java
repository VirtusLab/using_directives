package json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.virtuslab.using_directives.custom.model.Path;
import java.lang.reflect.Type;
import java.util.Arrays;

public class CustomPathAdapter implements JsonDeserializer<Path> {

  @Override
  public Path deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
      throws JsonParseException {
    String path = json.getAsString();
    return new Path(Arrays.asList(path.split("\\.")));
  }
}
