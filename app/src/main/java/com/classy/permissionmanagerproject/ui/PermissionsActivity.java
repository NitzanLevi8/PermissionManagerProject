package com.classy.permissionmanagerproject.ui;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.classy.permissionlib.PermissionManager;
import com.classy.permissionlib.PermissionResultCallback;
import com.classy.permissionmanagerproject.R;
import com.classy.permissionmanagerproject.adapters.PermissionAdapter;
import com.classy.permissionmanagerproject.data.PermissionRepository;
import com.classy.permissionmanagerproject.logging.ActivityLogger;
import com.classy.permissionmanagerproject.models.PermissionItem;
import java.util.List;
import java.util.stream.Collectors;

public class PermissionsActivity extends AppCompatActivity implements PermissionAdapter.OnPermissionToggleListener {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private RecyclerView permissionsRecyclerView;
    private ImageButton backButton;
    private PermissionAdapter adapter;
    private List<PermissionItem> permissionList;
    private List<String> initialGrantedPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_permissions);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        permissionsRecyclerView = findViewById(R.id.permissionsRecyclerView);
        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> onBackPressed());
        // Load Permissions with current granted state
        permissionList = PermissionRepository.getPermissionsList(this);
        adapter = new PermissionAdapter(permissionList, this);
        permissionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        permissionsRecyclerView.setAdapter(adapter);
        // Save initial granted permissions list
        initialGrantedPermissions = permissionList.stream()
                .filter(PermissionItem::isGranted)
                .map(PermissionItem::getPermission)
                .collect(Collectors.toList());
    }

    @Override
    public void onPermissionToggle(String permission) {
        PermissionManager.requestPermission(
                PermissionsActivity.this,
                permission,
                (permission1, isGranted) -> {
                    Toast.makeText(
                            PermissionsActivity.this,
                            isGranted ? "Permission granted!" : "Permission denied",
                            Toast.LENGTH_SHORT
                    ).show();

                    refreshPermissions();
                    setResult(RESULT_OK);
                }
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.handlePermissionResult(requestCode, permissions, grantResults);
        refreshPermissions();
    }

    @Override
    public void onBackPressed() {
        List<String> currentGrantedPermissions = permissionList.stream()
                .filter(PermissionItem::isGranted)
                .map(PermissionItem::getPermission)
                .collect(Collectors.toList());
        if (!initialGrantedPermissions.equals(currentGrantedPermissions)) {
            setResult(RESULT_OK);
        } else {
            setResult(RESULT_CANCELED);
        }
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshPermissions();
    }

    private void refreshPermissions() {
        for (PermissionItem permission : permissionList) {
            boolean isNowGranted = ContextCompat.checkSelfPermission(
                    this, permission.getPermission()
            ) == PackageManager.PERMISSION_GRANTED;
            if (permission.isGranted() != isNowGranted) {
                permission.setGranted(isNowGranted);
                String status = isNowGranted ? "granted" : "revoked";
                ActivityLogger.log("Permission " + status + ": " + permission.getPermission());
            }
        }
        adapter.notifyDataSetChanged();
    }
}
