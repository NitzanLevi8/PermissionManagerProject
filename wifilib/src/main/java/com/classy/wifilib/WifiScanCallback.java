package com.classy.wifilib;

import android.net.wifi.ScanResult;
import java.util.List;

public interface WifiScanCallback {
    void onScanSuccess(List<ScanResult> results);
    void onScanFailed(String errorMessage);
}
