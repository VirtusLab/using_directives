package dotty.using_directives.custom.utils.ast;

import dotty.using_directives.custom.utils.Position;

import java.util.List;

public class SettingDefs extends SettingDefOrUsingValue {
    private List<SettingDef> settings;

    public SettingDefs(List<SettingDef> settings, Position position) {
        super(position);
        this.settings = settings;
    }

    public SettingDefs() { }

    @Override
    public String toString() {
        return "SettingDefs(" + settings + ")";
    }

    public List<SettingDef> getSettings() {
        return settings;
    }

    public void setSettings(List<SettingDef> settings) {
        this.settings = settings;
    }
}