package com.droid.manasshrestha.contentportalhome;

import android.app.Application;
import android.content.Context;

/**
 * Created by ManasShrestha on 9/4/17.
 */

public class ContentPortalApplication extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        context = this;
    }

    public static Context getContext() {
        return context;
    }
}
