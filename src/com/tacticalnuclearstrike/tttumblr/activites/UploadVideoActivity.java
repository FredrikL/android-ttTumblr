package com.tacticalnuclearstrike.tttumblr.activites;

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
import com.tacticalnuclearstrike.tttumblr.TumblrService;

public class UploadVideoActivity extends PostActivity {
	Uri outputFileUri;
	int TAKE_PICTURE = 0;
	int SELECT_VIDEO = 1;
	
	private static final String TAG = "UploadVideoActivity";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.uploadvideoview);

		Button btnPostPhoto = (Button) findViewById(R.id.btnPostVideo);
		btnPostPhoto.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				videoImage();
			}
		});

		Button btnSelectImage = (Button) findViewById(R.id.btnSelectVideo);
		btnSelectImage.setOnClickListener(new View.OnClickListener() {
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
		String caption = text.getText().toString();

		Intent uploadIntent = new Intent(TumblrService.ACTION_POST_VIDEO);
		uploadIntent.putExtra("video", getRealPathFromURI(outputFileUri));
		uploadIntent.putExtra("caption", caption);
		uploadIntent.putExtra("options", mPostOptions);
		startService(uploadIntent);
		
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
