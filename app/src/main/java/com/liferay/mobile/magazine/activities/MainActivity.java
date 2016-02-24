package com.liferay.mobile.magazine.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.liferay.mobile.magazine.R;
import com.liferay.mobile.magazine.utils.PicassoUtil;
import com.liferay.mobile.screens.assetlist.AssetEntry;
import com.liferay.mobile.screens.assetlist.AssetListScreenlet;
import com.liferay.mobile.screens.base.list.BaseListListener;
import com.liferay.mobile.screens.base.list.BaseListScreenlet;
import com.liferay.mobile.screens.context.LiferayServerContext;
import com.liferay.mobile.screens.context.SessionContext;
import com.liferay.mobile.screens.ddl.model.Field;
import com.liferay.mobile.screens.util.LiferayLogger;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements BaseListListener<AssetEntry> {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		SessionContext.createBasicSession("javier.gamarra", "***REMOVED***");

		AssetListScreenlet assetListScreenlet = (AssetListScreenlet) findViewById(R.id.magazines);
		assetListScreenlet.setListener(this);
	}

	@Override
	public void onListPageFailed(BaseListScreenlet source, int page, Exception e) {

	}

	@Override
	public void onListPageReceived(BaseListScreenlet source, int page, List<AssetEntry> entries, int rowCount) {

	}

	@Override
	public void onListItemSelected(final AssetEntry assetEntry, View view) {

		final ProgressBar downloadProgress = (ProgressBar) view.findViewById(R.id.download_progress);

		if (triedToDownload(assetEntry)) {
			Intent intent = new Intent(this, MagazineActivity.class);
			intent.putExtra("assetEntry", assetEntry);
			startActivity(intent);
		}
		else {
			final int step = 100 / assetEntry.getChapters().size();
			downloadProgress.setProgress(0);

			final TextView downloadText = (TextView) view.findViewById(R.id.download_text);
			downloadText.setText("Downloading...");

			Observable.from(assetEntry.getChapters())
				.map(new Func1<Field, Object>() {
					@Override
					public Object call(Field field) {
						try {
							if (!isFieldDownloaded(field)) {
								return downloadImage(field);
							}
							return true;
						}
						catch (IOException e) {
							return Observable.error(e);
						}
					}
				})
				.retry(3)
				.cache()
				.subscribeOn(Schedulers.newThread())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Subscriber<Object>() {
					@Override
					public void onCompleted() {
						downloadText.setText("");
					}

					@Override
					public void onError(Throwable e) {
						downloadText.setText("Failed!");
						LiferayLogger.e("Error!" + e.getMessage());
					}

					@Override
					public void onNext(Object o) {
						downloadProgress.setProgress(downloadProgress.getProgress() + step);
					}
				});
		}
	}

	@Override
	public void loadingFromCache(boolean success) {

	}

	@Override
	public void retrievingOnline(boolean triedInCache, Exception e) {

	}

	@Override
	public void storingToCache(Object object) {

	}

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

	private Field downloadImage(final Field field) throws IOException {
		OkHttpClient client = new OkHttpClient();

		Request request = new Request.Builder()
			.url(LiferayServerContext.getServer() + field.getCurrentValue())
			.build();

		Response response = client.newCall(request).execute();

		InputStream is = response.body().byteStream();
		String extension = response.body().contentType().subtype();

		String currentValue = (String) field.getCurrentValue();
		int index = currentValue.indexOf("?img_id");
		int lastIndex = currentValue.lastIndexOf("&");
		String path = currentValue.substring(index + 8, lastIndex) + "." + extension;

		File file = new File(PicassoUtil.getDownloadDirectory(), path);

		createFile(is, file);

		field.setCurrentValue(file.getCanonicalPath());
		return field;
	}

	private void createFile(InputStream is, File file) throws IOException {
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

}
