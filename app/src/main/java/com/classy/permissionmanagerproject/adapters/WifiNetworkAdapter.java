package com.classy.permissionmanagerproject.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.classy.permissionmanagerproject.R;
import com.classy.permissionmanagerproject.logging.ActivityLogger;
import com.classy.permissionmanagerproject.models.WifiNetworkItem;

import java.util.List;

public class WifiNetworkAdapter extends RecyclerView.Adapter<WifiNetworkAdapter.WifiViewHolder> {

    private List<WifiNetworkItem> wifiList;
    private final WifiClickListener listener;

    public WifiNetworkAdapter(List<WifiNetworkItem> wifiList, WifiClickListener listener) {
        this.wifiList = wifiList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public WifiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_wifi, parent, false);
        return new WifiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WifiViewHolder holder, int position) {
        WifiNetworkItem item = wifiList.get(position);
        holder.tvSsid.setText(item.getSsid());
        // Set signal icon based on RSSI
        int signalIcon = getWifiSignalIcon(item.getRssi());
        holder.imgSignal.setImageResource(signalIcon);
        // Show/hide lock icon
        holder.imgLock.setVisibility(item.isSecured() ? View.VISIBLE : View.GONE);
        // Handle click on whole card
        holder.itemView.setOnClickListener(v -> {
            // 🔍 לוג לרשת שנלחצה
            ActivityLogger.log("User clicked on WiFi network: " + item.getSsid());
            // הפעולה המקורית שלך
            listener.onWifiItemClicked(item);
        });
    }


    @Override
    public int getItemCount() {
        return wifiList.size();
    }

    public void updateList(List<WifiNetworkItem> newList) {
        this.wifiList = newList;
        notifyDataSetChanged();
    }

    static class WifiViewHolder extends RecyclerView.ViewHolder {
        TextView tvSsid;
        ImageView imgSignal, imgLock;

        public WifiViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSsid = itemView.findViewById(R.id.tv_ssid);
            imgSignal = itemView.findViewById(R.id.img_wifi_signal);
            imgLock = itemView.findViewById(R.id.img_lock);
        }
    }

    public interface WifiClickListener {
        void onWifiItemClicked(WifiNetworkItem item);
    }

    // Signal level to icon mapper
    private int getWifiSignalIcon(int rssi) {
        if (rssi >= -50) {
            return R.drawable.ic_4_wifi;
        } else if (rssi >= -65) {
            return R.drawable.ic_3_wifi;
        } else if (rssi >= -75) {
            return R.drawable.ic_2_wifi;
        } else {
            return R.drawable.ic_1_wifi;
        }
    }
}

