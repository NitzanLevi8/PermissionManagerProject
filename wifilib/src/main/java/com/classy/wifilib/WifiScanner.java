package com.classy.wifilib;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.classy.permissionlib.PermissionManager;
import com.classy.permissionlib.PermissionResultCallback;

import java.util.List;

public class WifiScanner {

    private static final String TAG = "WifiScanner";

    private final Context context;
    private final WifiManager wifiManager;

    public WifiScanner(Context context) {
        this.context = context.getApplicationContext(); // getting app context
        this.wifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE); // initialize wifiManager
    }

    public void startScanWithPermissions(Activity activity, WifiScanCallback callback) { // gets activity for permissions requests, gets callback to return the scan result
        String[] requiredPermissions = { // requested permissions to scan WIFI
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_WIFI_STATE
        };

        checkAndRequestPermissions(activity, requiredPermissions, new PermissionResultCallback() {
            @Override
            public void onPermissionResult(String permission, boolean granted) {
                if (allPermissionsGranted(activity, requiredPermissions)) { // if all permissions were approved >> start wifi scan
                    if (!wifiManager.isWifiEnabled()) {
                        callback.onScanFailed("WiFi is disabled. Please enable WiFi.");
                        return;
                    }
                    startWifiScan(callback);
                } else {
                    callback.onScanFailed("Missing required permissions"); // returns error via the callback
                }
            }
        });
    }

    public void startWifiScan(WifiScanCallback callback) {
        IntentFilter filter = new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        Handler handler = new Handler(Looper.getMainLooper());

        final class TimeoutWrapper {
            Runnable runnable;
        }
        final TimeoutWrapper timeoutWrapper = new TimeoutWrapper();

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    context.unregisterReceiver(this);
                } catch (IllegalArgumentException ignored) {
                }
                handler.removeCallbacks(timeoutWrapper.runnable);

                boolean success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (success) {
                    try {
                        List<ScanResult> results = wifiManager.getScanResults();
                        callback.onScanSuccess(results);
                    } catch (SecurityException e) {
                        callback.onScanFailed("SecurityException: Missing permission to access scan results.");
                    }
                } else {
                    callback.onScanFailed("Scan failed or canceled.");
                }
            }
        };

        timeoutWrapper.runnable = () -> {
            try {
                context.unregisterReceiver(receiver);
            } catch (IllegalArgumentException ignored) {
            }
            callback.onScanFailed("Scan timeout: No response received.");
        };

        context.registerReceiver(receiver, filter);
        handler.postDelayed(timeoutWrapper.runnable, 10_000);

        try {
            boolean success = wifiManager.startScan();
            if (!success) {
                handler.removeCallbacks(timeoutWrapper.runnable);
                context.unregisterReceiver(receiver);
                callback.onScanFailed("WiFi scan failed to start.");
            }
        } catch (SecurityException e) {
            handler.removeCallbacks(timeoutWrapper.runnable);
            context.unregisterReceiver(receiver);
            callback.onScanFailed("SecurityException: Missing permission to start WiFi scan.");
        }
    }


//if permission is not yet granted >> performs a request, else returns immediate response via the callback
    private void checkAndRequestPermissions(Activity activity, String[] permissions, PermissionResultCallback callback) {
        for (String permission : permissions) {
            if (!PermissionManager.isPermissionGranted(activity, permission)) {
                PermissionManager.requestPermission(activity, permission, callback);
            } else {
                callback.onPermissionResult(permission, true);
            }
        }
    }

    // check if all requested permissions are exists
    private boolean allPermissionsGranted(Context context, String[] permissions) {
        for (String permission : permissions) {
            if (!PermissionManager.isPermissionGranted(context, permission)) {
                return false;
            }
        }
        return true;
    }
}
