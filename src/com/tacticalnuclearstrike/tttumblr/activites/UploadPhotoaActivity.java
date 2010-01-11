package com.tacticalnuclearstrike.tttumblr.activites;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

public class UploadPhotoaActivity extends Activity {
	Uri outputFileUri;
	int TAKE_PICTURE=0;
	
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	      //  setContentView(R.layout.main);
	        getFullImage();
	      
	    }
	
	private void getFullImage() {
		  Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		  File file = new File(Environment.getExternalStorageDirectory(), "test.jpg");
		  outputFileUri = Uri.fromFile(file);
		  intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
		  startActivityForResult(intent, TAKE_PICTURE);
		}

		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		  if ((requestCode == TAKE_PICTURE) && (resultCode == Activity.RESULT_OK)) {
		    // Check if the result includes a thumbnail Bitmap
		    if (data == null) {    
		      // TODO Do something with the full image stored
		      // in outputFileUri. Perhaps copying it to the app folder
		    }
		  }
		}

}
