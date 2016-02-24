package com.liferay.mobile.magazine.utils;

import android.os.Environment;

import com.liferay.mobile.screens.context.LiferayScreensContext;
import com.liferay.mobile.screens.context.LiferayServerContext;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.File;

/**
 * @author Javier Gamarra
 */
public class PicassoUtil {

	public static final String PATH = Environment.getExternalStorageDirectory() + "/tmp/magazinePOC/";

	public static RequestCreator getImageWithoutCache(String url) {
		return getImageWithCache(url).memoryPolicy(MemoryPolicy.NO_CACHE);
	}

	public static RequestCreator getImageWithCache(String url) {
		return Picasso.with(LiferayScreensContext.getContext())
			.load(url.contains("storage") ? "file://" + url : LiferayServerContext.getServer() + url)
			.error(android.R.drawable.ic_delete)
			.fit();
	}

	public static String getDownloadDirectory() {
		File file = new File(PATH);
		if (!file.exists()) {
			boolean success = file.mkdirs();
			if (!success) {
				throw new RuntimeException("missing path");
			}
		}
		return PATH;
	}
}
