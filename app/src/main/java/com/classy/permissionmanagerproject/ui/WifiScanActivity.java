package com.classy.permissionmanagerproject.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.classy.permissionmanagerproject.R;
import com.classy.permissionmanagerproject.adapters.WifiNetworkAdapter;
import com.classy.permissionmanagerproject.logging.ActivityLogger;
import com.classy.permissionmanagerproject.models.WifiNetworkItem;
import com.classy.wifilib.WifiScanCallback;
import com.classy.wifilib.WifiScanner;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class WifiScanActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_LOCATION = 100;

    private RecyclerView recyclerView;
    private WifiNetworkAdapter adapter;
    private FloatingActionButton fabRefresh;
    private WifiScanner wifiScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_scan_activity);
        // Toolbar with back button
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        // RecyclerView setup
        recyclerView = findViewById(R.id.recycler_wifi_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WifiNetworkAdapter(new ArrayList<>(), this::onWifiClicked);
        recyclerView.setAdapter(adapter);
        fabRefresh = findViewById(R.id.fab_refresh);
        fabRefresh.setOnClickListener(v -> checkPermissionsAndScan());
        wifiScanner = new WifiScanner(this);
        checkPermissionsAndScan();
    }

    private void onWifiClicked(WifiNetworkItem item) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (item.isSecured()) {
                showPasswordDialog(item);
            } else {
                connectToWifi(item.getSsid(), null);
            }
        } else {
            Intent panelIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
            startActivity(panelIntent);
        }
    }

    private void showPasswordDialog(WifiNetworkItem item) {
        if (!item.isSecured()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                connectToWifi(item.getSsid(), null);  // שולח null כסיסמה
            } else {
                Toast.makeText(this, "Connecting to WiFi requires Android 10 or higher", Toast.LENGTH_LONG).show();
            }
            return;
        }
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setHint("Enter WiFi Password");
        new AlertDialog.Builder(this)
                .setTitle("Connect to " + item.getSsid())
                .setView(input)
                .setPositiveButton("Connect", (dialog, which) -> {
                    String password = input.getText().toString();
                    if (TextUtils.isEmpty(password)) {
                        Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        connectToWifi(item.getSsid(), password);
                    } else {
                        Toast.makeText(this, "Connecting to WiFi requires Android 10 or higher", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void connectToWifi(String ssid, @Nullable String password) {
        WifiNetworkSpecifier.Builder builder = new WifiNetworkSpecifier.Builder()
                .setSsid(ssid);
        if (password != null) {
            builder.setWpa2Passphrase(password);
        }
        WifiNetworkSpecifier specifier = builder.build();
        NetworkRequest request = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .setNetworkSpecifier(specifier)
                .build();
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        connectivityManager.requestNetwork(request, new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                super.onAvailable(network);
                connectivityManager.bindProcessToNetwork(network);
                runOnUiThread(() -> Toast.makeText(WifiScanActivity.this,
                        "Connected to " + ssid, Toast.LENGTH_SHORT).show());
            }
            @Override
            public void onUnavailable() {
                runOnUiThread(() -> Toast.makeText(WifiScanActivity.this,
                        "Failed to connect to " + ssid, Toast.LENGTH_SHORT).show());
            }
        });
    }

    private void checkPermissionsAndScan() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LOCATION);
        } else {
            startWifiScan();
        }
    }
    private void startWifiScan() {
        ActivityLogger.log("Started WiFi scan");
        wifiScanner.startWifiScan(new WifiScanCallback() {
            @Override
            public void onScanSuccess(List<ScanResult> results) {
                List<WifiNetworkItem> networks = new ArrayList<>();
                for (ScanResult result : results) {
                    boolean isSecured = result.capabilities.contains("WPA") ||
                            result.capabilities.contains("WEP") ||
                            result.capabilities.contains("EAP");
                    networks.add(new WifiNetworkItem(result.SSID, result.level, isSecured));
                }
                adapter.updateList(networks);
                ActivityLogger.log("Received " + results.size() + " Wi-Fi networks");
            }
            @Override
            public void onScanFailed(String errorMessage) {
                ActivityLogger.log("WiFi scan failed: " + errorMessage);
                Toast.makeText(WifiScanActivity.this, "Scan failed: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startWifiScan();
        } else {
            Toast.makeText(this, "Location permission required to scan WiFi", Toast.LENGTH_LONG).show();
        }
    }
}
