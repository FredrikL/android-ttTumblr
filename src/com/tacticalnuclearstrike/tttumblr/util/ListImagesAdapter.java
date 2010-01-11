package com.tacticalnuclearstrike.tttumblr.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ListImagesAdapter extends BaseAdapter {
	private Context context;
	private Cursor cursor;
	private int columnIndex;

	public ListImagesAdapter(Context c, Cursor cursor, int index) {
		context = c;
		this.cursor = cursor;
		columnIndex = index;
	}

	@Override
	public int getCount() {
		return 0;
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;

		if (convertView == null) {
			imageView = new ImageView(context);
			cursor.moveToPosition(position);
			int imageID = cursor.getInt(columnIndex);
			imageView.setImageURI(Uri.withAppendedPath(
					MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, ""
							+ imageID));
			imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
			imageView.setPadding(8, 8, 8, 8);
			imageView.setLayoutParams(new GridView.LayoutParams(100, 100));

		} else {
			imageView = (ImageView) convertView;
		}

		// load images from disk

		return imageView;
	}
}
