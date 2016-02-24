package com.liferay.mobile.magazine.screenlets;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.liferay.mobile.magazine.R;
import com.liferay.mobile.magazine.activities.MainActivity;
import com.liferay.mobile.magazine.utils.PicassoUtil;
import com.liferay.mobile.screens.assetlist.AssetEntry;
import com.liferay.mobile.screens.base.list.BaseListAdapter;
import com.liferay.mobile.screens.base.list.BaseListAdapterListener;
import com.liferay.mobile.screens.ddl.model.Field;

import java.io.File;

public class AssetMagazineHolder extends BaseListAdapter.ViewHolder implements View.OnClickListener {

	public AssetMagazineHolder(View view, BaseListAdapterListener listener) {
		super(view, listener);

		imageView = (ImageView) view.findViewById(R.id.magazine_picture);
		magazineTitle = (TextView) view.findViewById(R.id.magazine_title);
		magazinePrize = (TextView) view.findViewById(R.id.magazine_prize);
		magazineProgress = (ProgressBar) view.findViewById(R.id.download_progress);

		_listener = listener;
		view.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		_listener.onItemClick(getLayoutPosition(), v);
	}

	public void bind(AssetEntry assetEntry) {
		magazineTitle.setText((String) assetEntry.getFieldByName("magazineTitle").getCurrentValue());
		magazinePrize.setText((String) assetEntry.getFieldByName("price").getCurrentValue());

		checkIfFileExists(assetEntry);

		magazineProgress.setProgress(MainActivity.isAssetDownloaded(assetEntry) ? 100 : 0);

		Field magazineThumbnail = assetEntry.getFieldByName("magazineThumbnail");
		if (magazineThumbnail != null) {
			String url = (String) magazineThumbnail.getCurrentValue();

			PicassoUtil.getImageWithCache(url).into(imageView);
		}

	}

	private void checkIfFileExists(AssetEntry assetEntry) {
		for (Field field : assetEntry.getChapters()) {
			String currentValue = (String) field.getCurrentValue();

			if (currentValue.contains("storage")) {
				continue;
			}
			int index = currentValue.indexOf("?img_id");
			int lastIndex = currentValue.lastIndexOf("&");
			String path = currentValue.substring(index + 8, lastIndex);

			File f = new File(PicassoUtil.getDownloadDirectory());
			for (File file : f.listFiles()) {

				if (file.isFile()) {
					String[] filename = file.getName().split("\\.(?=[^\\.]+$)");
					if (filename[0].equalsIgnoreCase(path)) {
						field.setCurrentValue(file.getAbsolutePath());
					}
				}
			}
		}
	}

	private ImageView imageView;
	private TextView magazineTitle;
	private TextView magazinePrize;
	private ProgressBar magazineProgress;
	private BaseListAdapterListener _listener;
}