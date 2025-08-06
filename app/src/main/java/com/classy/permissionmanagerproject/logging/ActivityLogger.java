package com.classy.permissionmanagerproject.logging;
import android.util.Log;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ActivityLogger {

    public static void log(String title) {
        Log.d("ActivityLogger", "Trying to log: " + title);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("activity_logs");
        String timestamp = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(new Date());
        Map<String, Object> log = new HashMap<>();
        log.put("title", title);
        log.put("timestamp", timestamp);
        ref.push().setValue(log)
                .addOnSuccessListener(aVoid -> Log.d("ActivityLogger", "Log saved: " + title))
                .addOnFailureListener(e -> Log.e("ActivityLogger", "Failed to save log", e));
    }
}
