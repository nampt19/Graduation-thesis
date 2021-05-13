package com.nampt.socialnetworkproject.util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

public class WindowUtil {
    private static WindowUtil instance;

    private Context mContext;

    public static WindowUtil getInstance(Context mContext) {
        if (instance == null) {
            instance = new WindowUtil(mContext);
        }
        return instance;
    }

    private WindowUtil(Context mContext) {
        this.mContext = mContext;
    }

    public int getWindowHeight() {
        // Calculate window height for fullscreen use
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) mContext).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        float density = mContext.getResources().getDisplayMetrics().density;
//        float dpHeight = displayMetrics.heightPixels / density;
//        float dpWidth = displayMetrics.widthPixels / density;
        return displayMetrics.heightPixels;
    }

    public float convertDpToPixel(float dp) {
        return dp * ((float) mContext.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public float convertPixelsToDp(float px) {
        return px / ((float) mContext.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) mContext.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager == null) return;
        if (inputMethodManager.isAcceptingText()) {
            if (((Activity) mContext).getCurrentFocus() == null) return;
            else
                inputMethodManager.hideSoftInputFromWindow(
                        ((Activity) mContext).getCurrentFocus().getWindowToken(), 0);

        }
    }
}
