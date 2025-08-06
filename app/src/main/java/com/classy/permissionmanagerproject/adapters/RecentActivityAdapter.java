package com.classy.permissionmanagerproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.classy.permissionmanagerproject.R;

import java.util.List;

public class RecentActivityAdapter extends RecyclerView.Adapter<RecentActivityAdapter.ViewHolder> {

    private final List<String> activityList;

    public RecentActivityAdapter(List<String> activityList) {
        this.activityList = activityList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recent_activity, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String activity = activityList.get(position);
        holder.textActivity.setText(activity);
    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textActivity;

        public ViewHolder(View itemView) {
            super(itemView);
            textActivity = itemView.findViewById(R.id.textRecentActivity);
        }
    }
}
