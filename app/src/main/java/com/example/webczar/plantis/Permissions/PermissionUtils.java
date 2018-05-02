package com.example.webczar.plantis.Permissions;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import java.security.Permission;
import java.util.ArrayList;

/**
 * Created by webczar on 2/4/2018.
 */
public class PermissionUtils {
    public static boolean requestPermission(Activity activity, int requestCode ,String... permissions){
        boolean granted = true;
        ArrayList<String> permissionNeeded = new ArrayList<>();

        for (String s: permissions){
            int permissionCheck = ActivityCompat.checkSelfPermission(activity, s);
            boolean permissionGranted = (permissionCheck == PackageManager.PERMISSION_GRANTED);
            granted &= permissionGranted;
            if (!permissionGranted){
                permissionNeeded.add(s);
            }
        }
        if (granted){
            return true;
        }else {
            ActivityCompat.requestPermissions(activity,
                    permissionNeeded.toArray(new String[permissionNeeded.size()]),
                    requestCode);
            return false;
        }
        }

        public static boolean permissionGranted(int requestCode , int permissionCode, int[] grantResult){
        if (requestCode==permissionCode){
            if (grantResult.length>=0 && grantResult[0]==PackageManager.PERMISSION_GRANTED){
                return true;
            }else {
                return false;
            }
        }return false;
        }
}

