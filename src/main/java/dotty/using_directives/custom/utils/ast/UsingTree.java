package dotty.using_directives.custom.utils.ast;

import dotty.using_directives.custom.utils.Position;

public abstract class UsingTree extends Positioned {
    public UsingTree(Position position) {
        super(position);
    }

    public UsingTree() { }
}
