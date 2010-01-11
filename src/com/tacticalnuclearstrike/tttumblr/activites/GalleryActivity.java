package com.tacticalnuclearstrike.tttumblr.activites;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.GridView;

import com.tacticalnuclearstrike.tttumblr.R;
import com.tacticalnuclearstrike.tttumblr.util.ListImagesAdapter;

public class GalleryActivity extends Activity {
	@Override
	public void onCreate(Bundle instancestate) {
		super.onCreate(instancestate);
		setContentView(R.layout.galleryview);
		
        String[] projection = {MediaStore.Images.Thumbnails._ID};
        Cursor cursor = managedQuery( MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                MediaStore.Images.Thumbnails.IMAGE_ID);
       int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);

		
		GridView gridView = (GridView)findViewById(R.id.gvGallery);
		gridView.setAdapter(new ListImagesAdapter(this, cursor, columnIndex));
	}
}
