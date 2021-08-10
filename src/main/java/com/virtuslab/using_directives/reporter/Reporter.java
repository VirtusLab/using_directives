package com.virtuslab.using_directives.reporter;

import com.virtuslab.using_directives.custom.utils.Position;

public interface Reporter {
    void error(String msg);
    void warning(String msg);
    void error(Position position, String msg);
    void warning(Position position, String msg);
    boolean hasErrors();
    void reset();
}
