package dotty.using_directives.custom.utils.ast;

import dotty.using_directives.custom.utils.Position;

public class SettingDef extends UsingTree {
    String key;
    SettingDefOrUsingValue value;

    public SettingDef(String key, SettingDefOrUsingValue value, Position position) {
        super(position);
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return "SettingDef(" + key + ", " + value + ')';
    }
}
