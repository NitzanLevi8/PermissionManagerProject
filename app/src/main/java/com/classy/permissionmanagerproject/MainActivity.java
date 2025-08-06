package com.classy.permissionmanagerproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.classy.permissionmanagerproject.adapters.RecentActivityAdapter;
import com.classy.permissionmanagerproject.data.PermissionRepository;
import com.classy.permissionmanagerproject.models.PermissionItem;
import com.classy.permissionmanagerproject.ui.PermissionsActivity;
import com.classy.permissionmanagerproject.ui.WifiScanActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {

    private TextView tvTotalPermissions, tvActivePermissions;
    private RecentActivityAdapter adapter;
    private List<String> recentActivities;
    private ActivityResultLauncher<Intent> permissionActivityLauncher;
    private RecyclerView recyclerRecentActivity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_main);
        tvTotalPermissions = findViewById(R.id.tvTotalPermissions);
        tvActivePermissions = findViewById(R.id.tvActivePermissions);
        recyclerRecentActivity = findViewById(R.id.recyclerRecentActivity);
        recyclerRecentActivity = findViewById(R.id.recyclerRecentActivity);
        recentActivities = new ArrayList<>();
        adapter = new RecentActivityAdapter(recentActivities);
        recyclerRecentActivity.setLayoutManager(new LinearLayoutManager(this));
        recyclerRecentActivity.setAdapter(adapter);
        permissionActivityLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        refreshPermissionsUI();
                    }
                }
        );

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_permissions) {
                Intent intent = new Intent(MainActivity.this, PermissionsActivity.class);
                permissionActivityLauncher.launch(intent);
                return true;
            } else if (itemId == R.id.nav_wifi) {
                Intent intent = new Intent(MainActivity.this, WifiScanActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshPermissionsUI();
        loadRecentLogs();
    }

    private void refreshPermissionsUI() {
        List<PermissionItem> permissions = PermissionRepository.getPermissionsList(this);
        int granted = PermissionRepository.countGrantedPermissions(this);
        tvTotalPermissions.setText(String.valueOf(permissions.size()));
        tvActivePermissions.setText(String.valueOf(granted));
    }

    private void loadRecentLogs() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("activity_logs");

        ref.orderByChild("timestamp").limitToLast(5).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                recentActivities.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    String title = child.child("title").getValue(String.class);
                    String timestamp = child.child("timestamp").getValue(String.class);
                    recentActivities.add("• " + title + "\n" + timestamp);
                }
                Collections.reverse(recentActivities);
                adapter.notifyDataSetChanged();
                if (!recentActivities.isEmpty()) {
                    recyclerRecentActivity.scrollToPosition(0);
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("MainActivity", "Failed to load logs", error.toException());
            }
        });
    }


}
