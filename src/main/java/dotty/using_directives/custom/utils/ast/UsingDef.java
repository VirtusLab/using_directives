package dotty.using_directives.custom.utils.ast;

import dotty.using_directives.custom.utils.Position;


public class UsingDef extends UsingTree {
    private SettingDefs settingDefs;

    public UsingDef(SettingDefs settings, Position position) {
        super(position);
        this.settingDefs = settings;
    }

    public UsingDef() { }

    public void setSettingDefs(SettingDefs settingDefs) {
        this.settingDefs = settingDefs;
    }

    @Override
    public String toString() {
        return "UsingDef(" + settingDefs + ")";
    }

    public SettingDefs getSettingDefs() {
        return settingDefs;
    }
}
