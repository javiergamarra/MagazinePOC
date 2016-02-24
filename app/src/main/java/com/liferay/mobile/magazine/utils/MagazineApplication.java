package com.liferay.mobile.magazine.utils;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;

public class MagazineApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		LeakCanary.install(this);
	}
}