package com.liferay.mobile.magazine.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.liferay.mobile.magazine.R;

/**
 * @author Javier Gamarra
 */
public class MenuAdapter extends ArrayAdapter<Object[]> {

	public MenuAdapter(Context context, int resource, Object[][] objects) {
		super(context, resource, objects);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Object[] assetEntry = getItem(position);

		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.lateral_menu, parent, false);
		}

		TextView chapter = (TextView) convertView.findViewById(R.id.magazine_chapter);
		chapter.setText((String) assetEntry[0]);

		ImageView imageView = (ImageView) convertView.findViewById(R.id.magazine_thumbnail);

		PicassoUtil
			.getImageWithCache((String) assetEntry[1])
			.placeholder(R.drawable.progress_animation)
			.into(imageView);

		return convertView;
	}
}
