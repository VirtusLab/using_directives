package com.virtuslab.using_directives.config;

public class Settings {
    private boolean allowRequire = true;

    private boolean allowStartWithoutAt = true;

    public boolean isAllowRequire() {
        return allowRequire;
    }

    public void setAllowRequire(boolean allowRequire) {
        this.allowRequire = allowRequire;
    }

    public boolean isAllowStartWithoutAt() {
        return allowStartWithoutAt;
    }

    public void setAllowStartWithoutAt(boolean allowStartWithoutAt) {
        this.allowStartWithoutAt = allowStartWithoutAt;
    }

    public Settings() { }

    public Settings(boolean allowRequire, boolean allowStartWithoutAt) {
        this.allowRequire = allowRequire;
        this.allowStartWithoutAt = allowStartWithoutAt;
    }
}
