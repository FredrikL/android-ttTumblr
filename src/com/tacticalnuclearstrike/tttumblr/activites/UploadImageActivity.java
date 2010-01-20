package com.tacticalnuclearstrike.tttumblr.activites;

import java.io.File;
import java.io.FileNotFoundException;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
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
	int SELECT_IMAGE = 1;

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

		Button btnSelectImage = (Button) findViewById(R.id.btnSelectImage);
		btnSelectImage.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				selectImage();
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
		if (resultCode != Activity.RESULT_OK)
			return;

		if (requestCode == TAKE_PICTURE) {
			try {
				// HACK: create thumbnail from the temp file
				// since adding it to the media store
				// seems to take a short while.
				setSelectedImageThumbnail(outputFileUri);

				// move image to mediastore so that i can be
				// seen in the gallery
				File f = new File(outputFileUri.getPath());
				try {
					outputFileUri = Uri
							.parse(android.provider.MediaStore.Images.Media
									.insertImage(getContentResolver(), f
											.getAbsolutePath(), null, null));

					f.delete();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (requestCode == SELECT_IMAGE) {
			outputFileUri = data.getData();
			setSelectedImageThumbnail(outputFileUri);
		}
	}

	private void setSelectedImageThumbnail(Uri image) {
		try {
			Drawable dr = Drawable.createFromPath(image.getPath());
			ImageView iv = (ImageView) findViewById(R.id.selectedImage);
			iv.setImageDrawable(dr);
			iv.setMaxHeight(100);
			iv.setMaxWidth(100);
			iv.setScaleType(ImageView.ScaleType.FIT_XY);
			iv.invalidate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getRealPathFromURI(Uri contentUri) {

		// can post image
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(contentUri, proj, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();

		return cursor.getString(column_index);
	}

	private void uploadImage() {
		EditText text = (EditText) findViewById(R.id.tbImageCaption);
		String caption = text.getText().toString();

		String path = getRealPathFromURI(outputFileUri);
		File photoToUpload = new File(path);

		TumblrApi api = new TumblrApi(this);
		api.PostImage(photoToUpload, caption);

		setResult(RESULT_OK);
		finish();
	}

	private void selectImage() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"),
				SELECT_IMAGE);
	}
}