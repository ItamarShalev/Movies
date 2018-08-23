package com.movies.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


/**
 * Handle in permission, with 4 step
 * <p>
 * 1 - create instance with activity, requestCode
 * 2 - init runnable what you want to do if have permission or not, and permission args(String...)
 * 3 - just call to requestPermission();
 * 4 - call to onRequestPermissionsResult where your activity, and give boolean true if you want all permission must to be true or false if just permission one true is ok
 */
public class PermissionHandler {

    private Activity activity;
    private int requestCode;
    private Runnable actionWhenHave;
    private Runnable actionWhenNoHave;
    private String[] permissions;
    private int status = 0;


    public PermissionHandler(Activity activity, int requestCode) {
        this.activity = activity;
        this.requestCode = requestCode;
    }

    private static boolean needRequestRuntimePermissions() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }


    public void init(Runnable actionWhenHave, Runnable actionWhenNoHave, String... permissions) {
        this.actionWhenHave = actionWhenHave;
        this.actionWhenNoHave = actionWhenNoHave;
        this.permissions = permissions;
        if (permissions.length == 0) {
            throw new RuntimeException("Must contains minimum one permission");
        }
        status = 1;
    }

    public void requestPermission() {
        if (status == 0) {
            throw new RuntimeException("init not call()");
        }
        if (!needRequestRuntimePermissions()) {
            actionWhenHave.run();
        } else {
            boolean result = true;
            for (String permission : permissions) {
                int resultNumber = ContextCompat.checkSelfPermission(activity, permission);
                if (resultNumber != PackageManager.PERMISSION_GRANTED) {
                    result = false;
                    break;
                }
            }
            if (result) {
                actionWhenHave.run();
            } else {
                status = 2;
                ActivityCompat.requestPermissions(activity, permissions, requestCode);
            }
        }

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults, boolean allMustToBeTrue) {
        if (this.requestCode != requestCode || !isSamePermission(permissions)) {
            return;
        }
        if (status == 0) {
            throw new RuntimeException("init not call()");
        }
        if (status != 2) {
            throw new RuntimeException("requestPermission not call()");
        }
        boolean result;
        if (allMustToBeTrue) {
            result = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    result = false;
                    break;
                }
            }
        } else {
            result = false;
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    result = true;
                    break;
                }
            }
        }
        if (result) {
            actionWhenHave.run();
        } else {
            actionWhenNoHave.run();
        }
    }

    private boolean isSamePermission(String[] permissions) {
        int length = this.permissions.length;
        Set<String> stringSet = new HashSet<>(Arrays.asList(this.permissions));
        stringSet.addAll(Arrays.asList(permissions));
        return length == stringSet.size();
    }

}
