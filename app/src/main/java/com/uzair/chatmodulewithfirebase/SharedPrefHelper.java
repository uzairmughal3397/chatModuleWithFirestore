package com.uzair.chatmodulewithfirebase;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPrefHelper {
    private static final int MODE = 0;

    public SharedPrefHelper() {
    }

    private static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences("", 0);

    }


    private static SharedPreferences.Editor getEditor(Context context) {
        return getPreferences(context).edit();
    }

    public static void writeInteger(Context context, String str, int i) {
        getEditor(context).putInt(str, i).commit();
    }

    public static int readInteger(Context context, String str) {
        return getPreferences(context).getInt(str, 0);
    }

    public static void writeString(Context context, String str, String str2) {
        getEditor(context).putString(str, str2).commit();
    }

    public static String readString(Context context, String str) {
        return getPreferences(context).getString(str, null);
    }

    public static boolean checkContain(Context context, String str) {
        return getPreferences(context).contains(str);
    }

    public static void clearAll(Context context) {
        getEditor(context).clear().commit();
    }

    public static void writeBoolean(Context context, String str, boolean z) {
        getPreferences(context).edit().putBoolean(str, z).apply();
    }

    public static boolean readBoolean(Context context, String str) {
        return getPreferences(context).getBoolean(str, false);
    }
}