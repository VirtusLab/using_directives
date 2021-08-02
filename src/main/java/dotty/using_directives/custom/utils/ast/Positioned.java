package dotty.using_directives.custom.utils.ast;

import dotty.using_directives.custom.utils.Position;

public abstract class Positioned {
    private final Position position;

    public Positioned(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }
}
