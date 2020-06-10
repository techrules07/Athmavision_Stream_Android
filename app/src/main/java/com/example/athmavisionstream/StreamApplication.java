package com.example.athmavisionstream;

import android.app.Application;
import android.content.Context;

import com.example.athmavisionstream.mvvm.AppComponent;
import com.example.athmavisionstream.mvvm.DaggerAppComponent;

public class StreamApplication extends Application {

    AppComponent appComponent;
    Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        appComponent = DaggerAppComponent.builder().build();
    }
    public AppComponent getAppComponent() {
        return appComponent;
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
    }
}
