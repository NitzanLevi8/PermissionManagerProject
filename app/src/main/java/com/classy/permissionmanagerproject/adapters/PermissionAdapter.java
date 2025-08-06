package com.classy.permissionmanagerproject.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.classy.permissionmanagerproject.R;
import com.classy.permissionmanagerproject.logging.ActivityLogger;
import com.classy.permissionmanagerproject.models.PermissionItem;

import java.util.List;

public class PermissionAdapter extends RecyclerView.Adapter<PermissionAdapter.PermissionViewHolder> {

    public interface OnPermissionToggleListener {
        void onPermissionToggle(String permission);
    }

    private List<PermissionItem> permissionList;
    private OnPermissionToggleListener listener;

    public PermissionAdapter(List<PermissionItem> permissionList, OnPermissionToggleListener listener) {
        this.permissionList = permissionList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PermissionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_permission, parent, false);
        return new PermissionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PermissionViewHolder holder, int position) {
        PermissionItem permission = permissionList.get(position);
        Context context = holder.itemView.getContext();
        holder.name.setText(permission.getName());
        holder.description.setText(permission.getDescription());
        boolean isGranted = ContextCompat.checkSelfPermission(
                context, permission.getPermission()
        ) == PackageManager.PERMISSION_GRANTED;
        holder.permissionSwitch.setOnCheckedChangeListener(null);
        holder.permissionSwitch.setChecked(isGranted);
        holder.permissionSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked && !isGranted) {
                ActivityLogger.log("Permission requested: " + permission.getPermission());
                if (listener != null) {
                    listener.onPermissionToggle(permission.getPermission());
                }
            } else if (!isChecked && isGranted) {
                ActivityLogger.log("Permission revoke requested: " + permission.getPermission());
                new androidx.appcompat.app.AlertDialog.Builder(context)
                        .setTitle("Permission settings")
                        .setMessage("To revoke this permission, please go to the app settings.")
                        .setPositiveButton("Go to Settings", (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                            intent.setData(uri);
                            context.startActivity(intent);
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .setOnDismissListener(dialog -> {
                            holder.permissionSwitch.setChecked(true);
                        })
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return permissionList.size();
    }

    static class PermissionViewHolder extends RecyclerView.ViewHolder {
        TextView name, description;
        SwitchCompat permissionSwitch;

        public PermissionViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.permissionName);
            description = itemView.findViewById(R.id.permissionDescription);
            permissionSwitch = itemView.findViewById(R.id.permissionSwitch);
        }
    }
}
