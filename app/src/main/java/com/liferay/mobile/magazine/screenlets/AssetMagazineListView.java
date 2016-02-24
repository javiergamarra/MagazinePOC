package com.liferay.mobile.magazine.screenlets;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.liferay.mobile.magazine.R;
import com.liferay.mobile.magazine.utils.FileUtils;
import com.liferay.mobile.screens.assetlist.AssetEntry;
import com.liferay.mobile.screens.base.list.BaseListScreenletView;
import com.liferay.mobile.screens.ddl.XSDParser;
import com.liferay.mobile.screens.ddl.model.Field;
import com.liferay.mobile.screens.util.LiferayLogger;
import com.liferay.mobile.screens.viewsets.defaultviews.ddl.list.DividerItemDecoration;

import org.xml.sax.SAXException;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.liferay.mobile.magazine.utils.FileUtils.getPath;
import static com.liferay.mobile.magazine.utils.FileUtils.isFieldDownloaded;

/**
 * @author Javier Gamarra
 */
public class AssetMagazineListView
	extends BaseListScreenletView<AssetEntry, AssetMagazineHolder, AssetMagazineAdapter> {

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
	public void showFinishOperation(int page, List<AssetEntry> serverEntries, int rowCount) {
		try {
			for (AssetEntry magazine : serverEntries) {
				Map<String, Object> object = (Map<String, Object>) magazine.getValues().get("object");

				XSDParser xsdParser = new XSDParser();
				List<Field> formFields = xsdParser.parse((String) ((Map) object.get("structure")).get("xsd"), Locale.ENGLISH);
				List<Field> fields = xsdParser.createForm(formFields, (String) object.get("content"));
				magazine.setFields(fields);
				checkIfFileExists(magazine);
			}
		}
		catch (SAXException e) {
			LiferayLogger.e("Error parsing form", e);
		}

		super.showFinishOperation(page, serverEntries, rowCount);
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

		_recyclerView = (RecyclerView) findViewById(com.liferay.mobile.screens.R.id.liferay_recycler_list);
		_progressBar = (ProgressBar) findViewById(com.liferay.mobile.screens.R.id.liferay_progress);

		AssetMagazineAdapter adapter = createListAdapter(itemLayoutId, itemProgressLayoutId);
		_recyclerView.setAdapter(adapter);
		_recyclerView.setHasFixedSize(true);
		_recyclerView.setLayoutManager(new GridLayoutManager(getContext(), SPAN_COUNT));

		DividerItemDecoration dividerItemDecoration = getDividerDecoration();
		if (dividerItemDecoration != null) {
			_recyclerView.addItemDecoration(
				getDividerDecoration());
		}
	}

	private void checkIfFileExists(AssetEntry assetEntry) {
		for (Field field : assetEntry.getChapters()) {
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
	private static final int SPAN_COUNT = 2;
}
