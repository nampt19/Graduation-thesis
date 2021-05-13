package com.nampt.socialnetworkproject.data_local;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreferences {
    private static final String PREF_SHARED_PREFERENCES_UTIL = "MY_SHARED_PREFERENCES_UTIL";
    private Context mContext;

    public MySharedPreferences(Context mContext) {
        this.mContext = mContext;
    }

    public void putBooleanValue(String key, boolean value) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(PREF_SHARED_PREFERENCES_UTIL, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public Boolean getBooleanValue(String key){
        SharedPreferences sharedPreferences=mContext.getSharedPreferences(PREF_SHARED_PREFERENCES_UTIL,Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key,false);
    }
    public void putStringValue(String key,String value){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(PREF_SHARED_PREFERENCES_UTIL,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,value);
        editor.apply();
    }
    public String getStringValue(String key){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(PREF_SHARED_PREFERENCES_UTIL,Context.MODE_PRIVATE);
        return sharedPreferences.getString(key,null);
    }
}
