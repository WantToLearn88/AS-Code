package arman.common;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;

import java.util.ArrayList;



public class PermissionManager {

    public static int FILE = 1;
    public static int CAMERA = 2;
    public static int OTHERS = 11;
    public static byte Rejected = -1;
    public static byte Partial = 0;
    public static byte Granted = 1;

    Activity activity;
    int sdk = Build.VERSION.SDK_INT;

    public PermissionManager(Activity activity){
        this.activity = activity;
    }
    public boolean hasFilePermission() {
        if (sdk < 30){
            String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (activity.checkSelfPermission(permission[0]) == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
            activity.requestPermissions(permission, FILE);
            return false;
        }

        if (Environment.isExternalStorageManager()) return true;

        Uri myPackage = Uri.parse("package:" + activity.getPackageName());
        try {
            Intent i = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, myPackage);
            //log("1st try: " + myPackage.toString());
            activity.startActivityForResult(i, FILE);
        }
        catch (ActivityNotFoundException anf) {
            try {
                Intent i = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION, myPackage);
                //log("2nd try: " + myPackage.toString());
                activity.startActivityForResult(i, FILE);
            }
            catch (Exception e) {log("fallback method Failed");}
        }
        catch (Exception e) { log("Intent Failed"); }

        return false;
    }

    public boolean hasCameraPermission() {
        String[] permission = {Manifest.permission.CAMERA};
        if (activity.checkSelfPermission(permission[0]) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        activity.requestPermissions(permission, CAMERA);
        return false;
    }

    public boolean hasOtherPermissions() {
        String[] permissions = null;
        try{
            PackageInfo info = activity.getPackageManager().getPackageInfo(activity.getPackageName(), PackageManager.GET_PERMISSIONS);
            permissions = info.requestedPermissions;
        }
        catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return hasOtherPermissions(permissions);
    }

    public boolean hasOtherPermissions(String[] permissions) {
        ArrayList<String> ungranted = new ArrayList<>();
        if (permissions == null || permissions.length < 1) return true;
        for (String permission : permissions) {
            if (activity.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                ungranted.add(permission);
            }
        }

        if (ungranted.isEmpty()) return true;
        String[] unGranted = convertStringArrayListToStringArray(ungranted);
        activity.requestPermissions(unGranted, OTHERS);
        return false;
    }

    private String[] convertStringArrayListToStringArray(ArrayList<String> stringArrayList){
        String[] stringArray = new String[stringArrayList.size()];
        for (int i = 0; i < stringArrayList.size(); i++){
            stringArray[i] = stringArrayList.get(i);
        }
        return stringArray;
    }

    public byte isGranted(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == FILE || requestCode == CAMERA){
            for (int result : grantResults){
                if (result != PackageManager.PERMISSION_GRANTED) {
                    return Rejected;
                }
            }
            return Granted;
        }

        if (requestCode == OTHERS) {
            int length = grantResults.length;
            int accepted = 0;
            for (int result : grantResults){
                if (result == PackageManager.PERMISSION_GRANTED) {
                    accepted++;
                }
            }
            if (accepted == length) return Granted;
            else if (accepted > 0) return Partial;
        }
        return Rejected;
    }

    void log(String s) {
        Log.e("Notepad", s);
    }
}
