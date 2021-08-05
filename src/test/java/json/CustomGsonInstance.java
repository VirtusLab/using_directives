package json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dotty.using_directives.custom.utils.ast.SettingDefOrUsingValue;
import dotty.using_directives.custom.utils.ast.UsingPrimitive;
import dotty.using_directives.custom.utils.ast.UsingValue;

public class CustomGsonInstance {
    public static Gson get() {
        return gsonInstance;
    }

    private static final Gson gsonInstance = new GsonBuilder()
            .registerTypeAdapter(SettingDefOrUsingValue.class, new CustomSettingDefOrUsingValueAdapter())
            .registerTypeAdapter(UsingValue.class, new CustomUsingValueAdapter())
            .registerTypeAdapter(UsingPrimitive.class, new CustomUsingPrimitivesAdapter())
            .addSerializationExclusionStrategy(new CustomExclusionStrategy())
            .addDeserializationExclusionStrategy(new CustomExclusionStrategy())
            .create();
}
