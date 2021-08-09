package dotty.using_directives.custom.reporter;

import dotty.using_directives.custom.utils.Position;

public interface Reporter {
    void error(String msg);
    void warning(String msg);
    void error(Position position, String msg);
    void warning(Position position, String msg);
}
