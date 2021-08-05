package json;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import dotty.using_directives.custom.utils.Position;

public class CustomExclusionStrategy implements ExclusionStrategy {
    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        return false;
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        if(clazz.equals(Position.class)) return true;
        else return false;
    }
}
