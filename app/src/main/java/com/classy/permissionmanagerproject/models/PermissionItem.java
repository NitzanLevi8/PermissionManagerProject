package com.classy.permissionmanagerproject.models;

public class PermissionItem {
    private String name;
    private String description;
    private String permission;
    private boolean isGranted;

    public PermissionItem(String name, String description, String permission, boolean isGranted) {
        this.name = name;
        this.description = description;
        this.permission = permission;
        this.isGranted = isGranted;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPermission() {
        return permission;
    }

    public boolean isGranted() {
        return isGranted;
    }

    public void setGranted(boolean granted) {
        isGranted = granted;
    }
}
