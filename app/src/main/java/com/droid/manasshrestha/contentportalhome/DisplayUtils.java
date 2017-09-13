package com.droid.manasshrestha.contentportalhome;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

public class DisplayUtils {

    public static float convertDpToPixel(float dp){
        Resources resources = ContentPortalApplication.getContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

}
