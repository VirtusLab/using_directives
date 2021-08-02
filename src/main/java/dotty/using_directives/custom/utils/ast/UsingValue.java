package dotty.using_directives.custom.utils.ast;

import dotty.using_directives.custom.utils.Position;

public abstract class UsingValue extends SettingDefOrUsingValue {
    public UsingValue(Position position) {
        super(position);
    }
}
