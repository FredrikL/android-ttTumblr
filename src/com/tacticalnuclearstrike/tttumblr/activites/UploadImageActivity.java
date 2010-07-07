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
import android.widget.Toast;
import android.util.Log;

import com.tacticalnuclearstrike.tttumblr.R;
import com.tacticalnuclearstrike.tttumblr.TumblrApi;
import com.tacticalnuclearstrike.tttumblr.TumblrService;

public class UploadImageActivity extends PostActivity {
    private static final String TAG = "UploadImageActivity";

	Uri outputFileUri;
	int TAKE_PICTURE = 0;
	int SELECT_IMAGE = 1;
	TumblrApi api;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        api  = new TumblrApi(this);
		setContentView(R.layout.uploadimageview);

        Intent startIntent = getIntent();
        if(startIntent != null 
            && startIntent.getExtras() != null
            && startIntent.getExtras().containsKey(Intent.EXTRA_STREAM)){
            Uri startData = (Uri)startIntent.getExtras().get(Intent.EXTRA_STREAM);
            Log.d(TAG, "got initial data: " + startData.toString());
            outputFileUri = startData;
			setSelectedImageThumbnail(outputFileUri);
        }

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
				File f = new File(outputFileUri.getPath());

				outputFileUri = Uri
						.parse(android.provider.MediaStore.Images.Media
								.insertImage(getContentResolver(), f
										.getAbsolutePath(), null, null));

				f.delete();
				setSelectedImageThumbnail(outputFileUri);

			} catch (FileNotFoundException e) {
				e.printStackTrace();

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
			String path = getRealPathFromURI(image);
			ImageView iv = (ImageView) findViewById(R.id.selectedImage);
			Drawable dr = Drawable.createFromPath(path);
			iv.setImageDrawable(dr);
			iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
			iv.invalidate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getRealPathFromURI(Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(contentUri, proj, null, null, null);
		int column_index = cursor
				.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();

		return cursor.getString(column_index);
	}

	private void uploadImage() {
		if(outputFileUri == null)
		{
			Toast.makeText(this, "No image to upload!", Toast.LENGTH_SHORT).show();
			return;
		}
		
		EditText text = (EditText) findViewById(R.id.tbImageCaption);
		final String caption = text.getText().toString();

        Intent uploadIntent = new Intent(TumblrService.ACTION_POST_PHOTO);
        uploadIntent.putExtra("photo", outputFileUri.toString());
        uploadIntent.putExtra("caption", caption);
        uploadIntent.putExtra("options", mPostOptions);
        startService(uploadIntent);

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
