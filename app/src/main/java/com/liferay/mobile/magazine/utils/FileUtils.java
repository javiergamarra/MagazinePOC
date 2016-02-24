package com.liferay.mobile.magazine.utils;

import android.os.Environment;
import android.support.annotation.NonNull;

import com.liferay.mobile.screens.assetlist.AssetEntry;
import com.liferay.mobile.screens.context.LiferayServerContext;
import com.liferay.mobile.screens.ddl.model.Field;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Javier Gamarra
 */
public class FileUtils {

	public static boolean triedToDownload(AssetEntry assetEntry) {
		for (Field field : assetEntry.getChapters()) {
			if (isFieldDownloaded(field)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isAssetDownloaded(AssetEntry assetEntry) {
		for (Field field : assetEntry.getChapters()) {
			if (!isFieldDownloaded(field)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isFieldDownloaded(Field field) {
		return field.getCurrentValue().toString().contains("storage");
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

	public static Field downloadImage(final Field field) throws IOException {
		OkHttpClient client = new OkHttpClient();

		Request request = new Request.Builder()
			.url(LiferayServerContext.getServer() + field.getCurrentValue())
			.build();

		Response response = client.newCall(request).execute();

		InputStream is = response.body().byteStream();
		String extension = response.body().contentType().subtype();

		String currentValue = (String) field.getCurrentValue();
		String path = getPath(currentValue, extension);

		File file = new File(FileUtils.getDownloadDirectory(), path);

		createFile(is, file);

		field.setCurrentValue(file.getCanonicalPath());
		return field;
	}

	@NonNull
	public static String getPath(String currentValue, String extension) {
		int index = currentValue.indexOf("?img_id");
		int lastIndex = currentValue.lastIndexOf("&");
		return currentValue.substring(index + 8, lastIndex) + "." + extension;
	}

	private static void createFile(InputStream is, File file) throws IOException {
		BufferedInputStream input = new BufferedInputStream(is);
		OutputStream output = new FileOutputStream(file);

		byte[] data = new byte[1024];

		int count;
		while ((count = input.read(data)) != -1) {
			output.write(data, 0, count);
		}

		output.flush();
		output.close();
		input.close();
	}
	private static final String PATH = Environment.getExternalStorageDirectory() + "/tmp/magazinePOC/";
}
