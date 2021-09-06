package com.virtuslab.using_directives.config;

public class Settings {
    private boolean allowRequire = true;

    private boolean startWithAt = true;

    public boolean isAllowRequire() {
        return allowRequire;
    }

    public void setAllowRequire(boolean allowRequire) {
        this.allowRequire = allowRequire;
    }

    public boolean isStartWithAt() {
        return startWithAt;
    }

    public void setStartWithAt(boolean startWithAt) {
        this.startWithAt = startWithAt;
    }
}
