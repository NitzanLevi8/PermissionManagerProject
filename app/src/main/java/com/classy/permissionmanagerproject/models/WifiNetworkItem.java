package com.classy.permissionmanagerproject.models;

public class WifiNetworkItem {
    private final String ssid;
    private final int rssi;
    private final boolean isSecured;

    public WifiNetworkItem(String ssid, int rssi, boolean isSecured) {
        this.ssid = ssid;
        this.rssi = rssi;
        this.isSecured = isSecured;
    }

    public String getSsid() {
        return ssid;
    }

    public int getRssi() {
        return rssi;
    }

    public boolean isSecured() {
        return isSecured;
    }
}

