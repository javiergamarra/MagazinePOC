package com.liferay.mobile.magazine.screenlets;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import com.liferay.mobile.magazine.R;
import com.liferay.mobile.magazine.utils.FileUtils;
import com.liferay.mobile.screens.asset.AssetEntry;
import com.liferay.mobile.screens.base.list.BaseListScreenletView;
import com.liferay.mobile.screens.ddl.model.Field;
import com.liferay.mobile.screens.webcontent.WebContent;
import java.io.File;
import java.util.List;

import static com.liferay.mobile.magazine.utils.FileUtils.getPath;
import static com.liferay.mobile.magazine.utils.FileUtils.isFieldDownloaded;

/**
 * @author Javier Gamarra
 */
public class AssetMagazineListView
	extends BaseListScreenletView<AssetEntry, AssetMagazineHolder, AssetMagazineAdapter> {

	private static final int SPAN_COUNT = 2;

	public AssetMagazineListView(Context context) {
		super(context);
	}

	public AssetMagazineListView(Context context, AttributeSet attributes) {
		super(context, attributes);
	}

	public AssetMagazineListView(Context context, AttributeSet attributes, int defaultStyle) {
		super(context, attributes, defaultStyle);
	}

	@Override
	public void showFinishOperation(int startRow, int endRow, List<AssetEntry> serverEntries, int totalRowCount) {

		for (AssetEntry assetEntry : serverEntries) {

			WebContent magazine = (WebContent) assetEntry;

			//FIXME
			//List<Field> fields = magazine.getDDMStructure().getFields();

			checkIfFileExists(magazine);
		}

		super.showFinishOperation(startRow, endRow, serverEntries, totalRowCount);
	}

	@Override
	protected AssetMagazineAdapter createListAdapter(int itemLayoutId, int itemProgressLayoutId) {
		return new AssetMagazineAdapter(itemLayoutId, itemProgressLayoutId, this);
	}

	protected int getItemLayoutId() {
		return R.layout.main_item;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		int itemLayoutId = getItemLayoutId();
		int itemProgressLayoutId = getItemProgressLayoutId();

		recyclerView = (RecyclerView) findViewById(com.liferay.mobile.screens.R.id.liferay_recycler_list);
		progressBar = (ProgressBar) findViewById(com.liferay.mobile.screens.R.id.liferay_progress);

		AssetMagazineAdapter adapter = createListAdapter(itemLayoutId, itemProgressLayoutId);
		recyclerView.setAdapter(adapter);
		recyclerView.setHasFixedSize(true);
		recyclerView.setLayoutManager(new GridLayoutManager(getContext(), SPAN_COUNT));

		RecyclerView.ItemDecoration dividerItemDecoration = getDividerDecoration();
		if (dividerItemDecoration != null) {
			recyclerView.addItemDecoration(getDividerDecoration());
		}
	}

	private void checkIfFileExists(AssetEntry assetEntry) {
		for (Field field : ((WebContent) assetEntry).getDDMStructure().getFields()) {
			if (isFieldDownloaded(field)) {
				continue;
			}

			String path = getPath((String) field.getCurrentValue(), "");

			File tmpDir = new File(FileUtils.getDownloadDirectory());
			for (File file : tmpDir.listFiles()) {

				if (file.isFile()) {
					String[] filename = file.getName().split("(?=[^\\.]+$)");
					if (filename[0].contains(path)) {
						field.setCurrentValue(file.getAbsolutePath());
					}
				}
			}
		}
	}
}
