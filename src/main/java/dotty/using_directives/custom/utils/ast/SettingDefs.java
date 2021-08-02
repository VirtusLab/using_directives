package dotty.using_directives.custom.utils.ast;

import dotty.using_directives.custom.utils.Position;

import java.util.List;

public class SettingDefs extends SettingDefOrUsingValue {
    List<SettingDef> settings;

    public SettingDefs(List<SettingDef> settings, Position position) {
        super(position);
        this.settings = settings;
    }

    @Override
    public String toString() {
        return "SettingDefs(" + settings + ")";
    }
}