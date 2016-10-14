package com.liferay.mobile.magazine.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.liferay.mobile.magazine.R;
import com.liferay.mobile.screens.asset.AssetEntry;
import com.liferay.mobile.screens.asset.list.AssetListScreenlet;
import com.liferay.mobile.screens.auth.login.LoginListener;
import com.liferay.mobile.screens.auth.login.interactor.LoginBasicInteractor;
import com.liferay.mobile.screens.base.list.BaseListListener;
import com.liferay.mobile.screens.context.SessionContext;
import com.liferay.mobile.screens.context.User;
import com.liferay.mobile.screens.context.storage.CredentialsStorageBuilder;
import com.liferay.mobile.screens.ddl.model.Field;
import com.liferay.mobile.screens.util.LiferayLogger;
import com.liferay.mobile.screens.webcontent.WebContent;
import java.io.IOException;
import java.util.List;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.liferay.mobile.magazine.utils.FileUtils.downloadImage;
import static com.liferay.mobile.magazine.utils.FileUtils.isFieldDownloaded;
import static com.liferay.mobile.magazine.utils.FileUtils.triedToDownload;
import static com.liferay.mobile.screens.auth.BasicAuthMethod.SCREEN_NAME;

public class MainActivity extends AppCompatActivity implements BaseListListener<AssetEntry>, LoginListener {

	private LoginBasicInteractor _loginInteractor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		triedToLogin();
	}

	@Override
	public void onListPageFailed(int startRow, Exception e) {

	}

	@Override
	public void onListPageReceived(int startRow, int endRow, List<AssetEntry> entries, int rowCount) {

	}

	@Override
	public void onListItemSelected(final AssetEntry assetEntry, View view) {

		final ProgressBar downloadProgress = (ProgressBar) view.findViewById(R.id.download_progress);

		if (triedToDownload(assetEntry)) {
			Intent intent = new Intent(this, MagazineActivity.class);
			intent.putExtra("assetEntry", assetEntry);
			startActivity(intent);
		} else {
			List<Field> chapters = ((WebContent) assetEntry).getDDMStructure().getFields();
			final int step = 100 / chapters.size();
			downloadProgress.setProgress(0);

			final TextView downloadText = (TextView) view.findViewById(R.id.download_text);
			downloadText.setText(R.string.downloading);

			Observable.from(chapters)
				.map(new Func1<Field, Object>() {
					@Override
					public Object call(Field field) {
						try {
							if (!isFieldDownloaded(field)) {
								return downloadImage(field);
							}
							return true;
						} catch (IOException e) {
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
						downloadText.setText(R.string.failed);
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
	public void onLoginSuccess(User user) {

		SessionContext.storeCredentials(CredentialsStorageBuilder.StorageType.SHARED_PREFERENCES);

		AssetListScreenlet assetListScreenlet = (AssetListScreenlet) findViewById(R.id.magazines);
		assetListScreenlet.setListener(this);
		assetListScreenlet.loadPage(0);
	}

	@Override
	public void onLoginFailure(Exception e) {

	}

	@Override
	protected void onStop() {
		super.onStop();
		if (_loginInteractor != null) {
			_loginInteractor.onScreenletDetached(this);
		}
	}

	private void triedToLogin() {

		SessionContext.loadStoredCredentials(CredentialsStorageBuilder.StorageType.SHARED_PREFERENCES);
		if (!SessionContext.isLoggedIn()) {
			SessionContext.createBasicSession("javier.gamarra", "1");
			_loginInteractor = new LoginBasicInteractor();
			_loginInteractor.start("javier.gamarra", "1", SCREEN_NAME);
			_loginInteractor.onScreenletAttached(this);
		} else {
			onLoginSuccess(SessionContext.getCurrentUser());
		}
	}

	@Override
	public void error(Exception e, String userAction) {

	}
}
