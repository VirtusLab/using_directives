package dotty.using_directives.custom.utils.ast;

public class SettingDef extends UsingTree {
    String key;
    SettingDefOrUsingValue value;

    public SettingDef(String key, SettingDefOrUsingValue value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return "SettingDef(" + key + ", " + value + ')';
    }
}
