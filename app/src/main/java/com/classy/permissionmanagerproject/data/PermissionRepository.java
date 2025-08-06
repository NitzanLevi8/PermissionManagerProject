package com.classy.permissionmanagerproject.data;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.content.ContextCompat;
import com.classy.permissionmanagerproject.models.PermissionItem;
import java.util.ArrayList;
import java.util.List;

public class PermissionRepository {

    public static List<PermissionItem> getPermissionsList(Context context) {
        List<PermissionItem> permissions = new ArrayList<>();

        permissions.add(createPermissionItem(context, "Camera", "Access to take pictures", Manifest.permission.CAMERA));
        permissions.add(createPermissionItem(context, "Location", "Access your location", Manifest.permission.ACCESS_FINE_LOCATION));
        permissions.add(createPermissionItem(context, "Microphone", "Record audio", Manifest.permission.RECORD_AUDIO));
        permissions.add(createPermissionItem(context, "Contacts", "Access your contacts", Manifest.permission.READ_CONTACTS));
        permissions.add(createPermissionItem(context, "Phone", "Access phone state", Manifest.permission.READ_PHONE_STATE));
        permissions.add(createPermissionItem(context, "SMS", "Read SMS messages", Manifest.permission.READ_SMS));
        permissions.add(createPermissionItem(context, "Calendar", "Access calendar events", Manifest.permission.READ_CALENDAR));
        permissions.add(createPermissionItem(context, "Call Logs", "Read your call history", Manifest.permission.READ_CALL_LOG));

        return permissions;
    }

    private static PermissionItem createPermissionItem(Context context, String name, String description, String permission) {
        boolean isGranted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
        return new PermissionItem(name, description, permission, isGranted);
    }

    public static int countGrantedPermissions(Context context) {
        int count = 0;
        for (PermissionItem item : getPermissionsList(context)) {
            if (item.isGranted()) {
                count++;
            }
        }
        return count;
    }
}

