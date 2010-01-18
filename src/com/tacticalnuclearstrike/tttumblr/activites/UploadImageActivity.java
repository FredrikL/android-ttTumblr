package com.tacticalnuclearstrike.tttumblr.activites;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.tacticalnuclearstrike.tttumblr.R;
import com.tacticalnuclearstrike.tttumblr.TumblrApi;

public class UploadImageActivity extends Activity {
	Uri outputFileUri;
	int TAKE_PICTURE = 0;
	File photoToUpload;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.uploadimageview);

		Button btnTakePicture = (Button) findViewById(R.id.btnTakePicture);
		btnTakePicture.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				takePhoto();
			}
		});

		Button btnPostPhoto = (Button) findViewById(R.id.btnPostImage);
		btnPostPhoto.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				uploadImage();
			}
		});
	}

	private void takePhoto() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File file = new File(Environment.getExternalStorageDirectory(),
				"test.jpg");
		outputFileUri = Uri.fromFile(file);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
		startActivityForResult(intent, TAKE_PICTURE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if ((requestCode == TAKE_PICTURE) && (resultCode == Activity.RESULT_OK)) {
			try {
				photoToUpload = new File(outputFileUri.getPath());

				Drawable dr = Drawable.createFromPath(outputFileUri.getPath());
				ImageView iv = (ImageView) findViewById(R.id.selectedImage);
				iv.setImageDrawable(dr);
				iv.setMaxHeight(100);
				iv.setMaxWidth(100);
				iv.setScaleType(ImageView.ScaleType.FIT_XY);
				iv.invalidate();
			} catch (Exception e) {
			}
		}
	}

	private void uploadImage() {
		EditText text = (EditText)findViewById(R.id.tbImageCaption);
		String caption = text.getText().toString();
		
		TumblrApi api = new TumblrApi(this);
		api.PostImage(photoToUpload, caption);
		
		setResult(RESULT_OK);
		finish();
	}
}
