package com.liferay.mobile.magazine.screenlets;

import android.support.annotation.NonNull;
import android.view.View;
import com.liferay.mobile.screens.asset.AssetEntry;
import com.liferay.mobile.screens.base.list.BaseListAdapter;
import com.liferay.mobile.screens.base.list.BaseListAdapterListener;

/**
 * @author Javier Gamarra
 */
public class AssetMagazineAdapter extends BaseListAdapter<AssetEntry, AssetMagazineHolder> {

	public AssetMagazineAdapter(int layoutId, int progressLayoutId, BaseListAdapterListener listener) {
		super(layoutId, progressLayoutId, listener);
	}

	@NonNull
	@Override
	public AssetMagazineHolder createViewHolder(View view, BaseListAdapterListener listener) {
		return new AssetMagazineHolder(view, getListener());
	}

	@Override
	protected void fillHolder(AssetEntry entry, AssetMagazineHolder holder) {
		holder.bind(entry);
	}
}
