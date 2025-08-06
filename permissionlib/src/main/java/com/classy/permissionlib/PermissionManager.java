package com.classy.permissionlib;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.HashMap;
import java.util.Map;

// responsible for handling runtime permission requests
public class PermissionManager {

    private static final int BASE_REQUEST_CODE = 1001; // Initial code for permission requests
    private static int requestCodeCounter = BASE_REQUEST_CODE; //Occurs every time permission is requested, to identify different requests
    private static final Map<Integer, PermissionResultCallback> callbackMap = new HashMap<>(); // a map between the requestCode and the callback
    private static final Map<Integer, String> permissionMap = new HashMap<>(); // a map between the requestCode and the permission we got

    private Context context;
    public PermissionManager(Context context) {
        this.context = context;
    }
    // checks if a permission is already exists
    public static boolean isPermissionGranted(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }

   // requests a single permission from te user
   public static void requestPermission(Activity activity, String permission, PermissionResultCallback callback) {
       if (ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED) {
           callback.onPermissionResult(permission, true); // if the permission is already exists
       } else {
           int requestCode = requestCodeCounter++; // if the permission is not already exists, creating a new requestCode
           callbackMap.put(requestCode, callback); // storing the requestCode in the callbackMap
           permissionMap.put(requestCode, permission);// storing the requestCode in the permissionMap
           ActivityCompat.requestPermissions(activity, new String[]{permission}, requestCode); // calling the ActivityCompat to request the permission
       }
   }

    public static void handlePermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        PermissionResultCallback callback = callbackMap.get(requestCode); // reads the callback from callbackMap
        String permission = permissionMap.get(requestCode); // reads the permission from permissionMap

        if (callback != null && permission != null) {
            boolean granted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED; // checking if we got a response and if the permission was approved
            callback.onPermissionResult(permission, granted); // returns the result to the callback
        }
        //remove the values to clean the memory
        callbackMap.remove(requestCode);
        permissionMap.remove(requestCode);
        }
    }