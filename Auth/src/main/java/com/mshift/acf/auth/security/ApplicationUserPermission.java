package com.mshift.acf.auth.security;

public enum ApplicationUserPermission {

    BASE("base");

    private final String permission;

    ApplicationUserPermission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
