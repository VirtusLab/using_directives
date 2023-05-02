package json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.virtuslab.using_directives.custom.model.Path;
import com.virtuslab.using_directives.custom.model.Value;
import com.virtuslab.using_directives.custom.utils.ast.*;

public class CustomGsonInstance {
  public static Gson get() {
    return gsonInstance;
  }

  private static final Gson gsonInstance =
      new GsonBuilder()
          .registerTypeAdapter(UsingValue.class, new CustomUsingValueAdapter())
          .registerTypeAdapter(UsingPrimitive.class, new CustomUsingPrimitivesAdapter())
          .registerTypeHierarchyAdapter(Value.class, new CustomValueAdapter())
          .registerTypeAdapter(Path.class, new CustomPathAdapter())
          .addSerializationExclusionStrategy(new CustomExclusionStrategy())
          .addDeserializationExclusionStrategy(new CustomExclusionStrategy())
          .create();
}
