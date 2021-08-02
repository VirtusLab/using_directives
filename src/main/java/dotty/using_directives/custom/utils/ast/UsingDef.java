package dotty.using_directives.custom.utils.ast;

import dotty.using_directives.custom.utils.Position;


public class UsingDef extends UsingTree {
    SettingDefs settings;

    public UsingDef(SettingDefs settings, Position position) {
        super(position);
        this.settings = settings;
    }

    @Override
    public String toString() {
        return "UsingDef(" + settings + ")";
    }
}
