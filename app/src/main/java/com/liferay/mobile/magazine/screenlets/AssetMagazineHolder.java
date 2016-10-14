package com.liferay.mobile.magazine.screenlets;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.liferay.mobile.magazine.R;
import com.liferay.mobile.magazine.activities.AssetUtil;
import com.liferay.mobile.magazine.utils.PicassoUtil;
import com.liferay.mobile.screens.assetlist.AssetEntry;
import com.liferay.mobile.screens.base.list.BaseListAdapter;
import com.liferay.mobile.screens.base.list.BaseListAdapterListener;
import com.liferay.mobile.screens.ddl.model.Field;

import static com.liferay.mobile.magazine.utils.FileUtils.isAssetDownloaded;

public class AssetMagazineHolder extends BaseListAdapter.ViewHolder implements View.OnClickListener {

	private final ImageView imageView;
	private final TextView magazineTitle;
	private final TextView magazinePrize;
	private final ProgressBar magazineProgress;
	private final BaseListAdapterListener _listener;
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
		magazineTitle.setText((String) AssetUtil.getFieldByName(assetEntry, "magazineTitle").getCurrentValue());
		magazinePrize.setText((String) AssetUtil.getFieldByName(assetEntry, "price").getCurrentValue());

		magazineProgress.setProgress(isAssetDownloaded(assetEntry) ? 100 : 0);

		Field magazineThumbnail = AssetUtil.getFieldByName(assetEntry, "magazineThumbnail");
		if (magazineThumbnail != null) {
			String url = (String) magazineThumbnail.getCurrentValue();
			PicassoUtil.getImageWithCache(url).placeholder(R.drawable.progress_animation).into(imageView);
		}
	}
}