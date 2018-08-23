package com.movies.utils;


import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Global function for this app
 */
public class Global {

    private static final String STR_SEPARATOR = "__,__";

    /**
     * @param list          of strings
     * @param STR_SEPARATOR The string that separates between all strings in list
     * @return string from list with separates between string to string
     * @throws RuntimeException if list null or empty (size = 0)
     */
    public static String convertListToString(List<String> list, String STR_SEPARATOR) {
        if (list == null || list.size() == 0) {
            throw new RuntimeException("Array cannot be null or empty");
        } else if (list.size() == 1) {
            return list.get(0);
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < list.size() - 1; i++) {
                stringBuilder.append(list.get(i)).append(STR_SEPARATOR);
            }
            stringBuilder.append(list.get(list.size() - 1));
            return stringBuilder.toString();
        }
    }

    /**
     * @param context for ClipboardManager
     * @param text    this text will copy to your keyboard
     */
    public static void copyText(Context context, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(text, text);
        if (clipboard != null) {
            clipboard.setPrimaryClip(clip);
            Toast.makeText(context, "Text copied", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * @param dbReadable The db will check
     * @return true if empty and false if is not empty
     */
    public static boolean isEmptyDB(SQLiteDatabase dbReadable, String tableName) {
        Cursor cursor = dbReadable.rawQuery("SELECT * FROM " + tableName + " LIMIT 1", null);
        int columnCount = cursor.getColumnCount();
        dbReadable.close();
        cursor.close();
        return columnCount == 0;
    }


    /**
     * @see Global#convertListToString(List, String)
     */
    public static String convertListToString(List<String> list) {
        return convertListToString(list, STR_SEPARATOR);
    }


    /**
     * @param str Divides strings by their separator
     * @return List (ArrayList) of strings
     */
    public static List<String> convertStringToList(String str) {
        return new ArrayList<>(Arrays.asList(str.split(STR_SEPARATOR)));
    }

    /**
     * @param activity Where you want clear the status bar and action bar
     */
    public static void setFullScreen(AppCompatActivity activity) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar supportActionBar = activity.getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.hide();
        } else {
            android.app.ActionBar actionBar = activity.getActionBar();
            if (actionBar != null) {
                actionBar.hide();
            }
        }
    }

    /**
     * @return true if is in portrait orientation, false if is in landscape orientation
     */
    public static boolean isPortraitOrientation(Activity activity) {
        return activity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    /**
     * this object good for sizes of screen and other
     */
    private static DisplayMetrics getDisplayMetrics(Activity activity) {
        return activity.getResources().getDisplayMetrics();
    }


    /**
     * @return Height int of screen phone in pixels
     */
    public static int getHeightSizeScreen(Activity activity) {
        return getDisplayMetrics(activity).heightPixels;
    }

    /**
     * @return Width int of screen phone in pixels
     */
    public static int getWidthSizeScreen(Activity activity) {
        return getDisplayMetrics(activity).widthPixels;
    }

    public static void removeAllFragments(FragmentManager fragmentManager) {
        try {
            for (Fragment fragment : fragmentManager.getFragments()) {
                fragmentManager.beginTransaction().remove(fragment).commit();
            }
        } catch (IllegalStateException ignore) {
        }
    }

    /**
     * @return If there is Internet (not necessarily fast and good)
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
