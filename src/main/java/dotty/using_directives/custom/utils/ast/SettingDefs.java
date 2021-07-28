package dotty.using_directives.custom.utils.ast;

import java.util.List;

public class SettingDefs extends SettingDefOrUsingValue {
    List<SettingDef> settings;

    public SettingDefs(List<SettingDef> settings) {
        this.settings = settings;
    }

    @Override
    public String toString() {
        return "SettingDefs(" + settings + ")";
    }
}