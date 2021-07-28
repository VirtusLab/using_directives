package dotty.using_directives.custom.utils.ast;

import java.util.List;

public class UsingDef extends UsingTree {
    SettingDefs settings;

    public UsingDef(SettingDefs settings) {
        this.settings = settings;
    }

    @Override
    public String toString() {
        return "UsingDef(" + settings + ")";
    }
}
