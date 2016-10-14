package com.liferay.mobile.magazine.utils;

import com.liferay.mobile.screens.context.LiferayScreensContext;
import com.liferay.mobile.screens.context.LiferayServerContext;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import static com.squareup.picasso.MemoryPolicy.NO_CACHE;
import static com.squareup.picasso.MemoryPolicy.NO_STORE;

/**
 * @author Javier Gamarra
 */
public class PicassoUtil {

	public static RequestCreator getImageWithoutCache(String url) {
		return getImageWithCache(url).memoryPolicy(NO_CACHE, NO_STORE);
	}

	public static RequestCreator getImageWithCache(String url) {
		return Picasso.with(LiferayScreensContext.getContext())
			.load(url.contains("storage") ? "file://" + url : LiferayServerContext.getServer() + url)
			.error(android.R.drawable.ic_delete)
			.fit();
	}
}
