package com.liferay.mobile.magazine.screenlets;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liferay.mobile.screens.assetlist.AssetEntry;
import com.liferay.mobile.screens.base.list.BaseListAdapter;
import com.liferay.mobile.screens.base.list.BaseListAdapterListener;

/**
 * @author Javier Gamarra
 */
public class AssetMagazineAdapter extends BaseListAdapter<AssetEntry, AssetMagazineHolder> {

	public AssetMagazineAdapter(int layoutId, int progressLayoutId, BaseListAdapterListener listener) {
		super(layoutId, progressLayoutId, listener);
	}

	public AssetMagazineHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		View view = viewType == LAYOUT_TYPE_DEFAULT ? inflater.inflate(getLayoutId(), parent, false) : inflater.inflate(getProgressLayoutId(), parent, false);

		return new AssetMagazineHolder(view, getListener());
	}

	@Override
	protected void fillHolder(AssetEntry entry, AssetMagazineHolder holder) {
		holder.bind(entry);
	}

}
