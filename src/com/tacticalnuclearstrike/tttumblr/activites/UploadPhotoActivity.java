package com.tacticalnuclearstrike.tttumblr.activites;

import java.io.File;
import java.io.FileInputStream;

import com.tacticalnuclearstrike.tttumblr.TumblrApi;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

public class UploadPhotoActivity extends Activity {
	Uri outputFileUri;
	int TAKE_PICTURE=0 ;
	
	 public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
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
		    //if (data == null) {    
		      // TODO Do something with the full image stored
		      // in outputFileUri. Perhaps copying it to the app folder
		    //}
			  try{
				  File file = new File(outputFileUri.getPath());
//				  FileInputStream fis = openFileInput(outputFileUri.getPath());
	//			  byte[] filedata = new byte[fis.available()];
		//		  fis.read(filedata, 0, fis.available());
				  TumblrApi api = new TumblrApi(this);
				  api.PostImage(file);
			  }
			  catch(Exception e)
			  {}
		  }
		}

}
