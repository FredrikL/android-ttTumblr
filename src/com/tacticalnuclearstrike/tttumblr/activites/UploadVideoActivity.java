package com.tacticalnuclearstrike.tttumblr.activites;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tacticalnuclearstrike.tttumblr.R;
import com.tacticalnuclearstrike.tttumblr.TumblrApi;

public class UploadVideoActivity extends Activity {
	Uri outputFileUri;
	int TAKE_PICTURE = 0;
	int SELECT_VIDEO = 1;
	final TumblrApi api = new TumblrApi(this);

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.uploadvideoview);

		Button btnPostPhoto = (Button) findViewById(R.id.btnPostVideo);
		btnPostPhoto.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				videoImage();
			}
		});

		Button btnSelectImage = (Button) findViewById(R.id.btnSelectVideo);
		btnSelectImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				selectVideo();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK)
			return;

		if (requestCode == SELECT_VIDEO) {
			outputFileUri = data.getData();
			//setSelectedImageThumbnail(outputFileUri);
		}
	}

	private String getRealPathFromURI(Uri contentUri) {
		String[] proj = { MediaStore.Video.Media.DATA };
		Cursor cursor = managedQuery(contentUri, proj, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
		cursor.moveToFirst();

		return cursor.getString(column_index);
	}

	private void videoImage() {
		if(outputFileUri == null)
		{
			Toast.makeText(this, "No video to upload!", Toast.LENGTH_SHORT).show();
			return;
		}
		
		EditText text = (EditText) findViewById(R.id.tbVideoCaption);
		final String caption = text.getText().toString();

		String path = getRealPathFromURI(outputFileUri);
		final File videoToUpload = new File(path);
		
		Toast.makeText(this, "Upload started", Toast.LENGTH_LONG).show();

		new Thread(new Runnable() {
			public void run() {
				api.PostVideo(videoToUpload, caption);
				
			}
		}).start();
		
		setResult(RESULT_OK);
		finish();
	}

	private void selectVideo() {
		Intent intent = new Intent();
		intent.setType("video/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Video"),
				SELECT_VIDEO);
	} 
}
