package com.tacticalnuclearstrike.tttumblr;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tacticalnuclearstrike.tttumblr.activites.PostTextActivity;
import com.tacticalnuclearstrike.tttumblr.activites.SettingsActivity;
import com.tacticalnuclearstrike.tttumblr.activites.UploadImageActivity;
import com.tacticalnuclearstrike.tttumblr.activites.UploadVideoActivity;

public class MainActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button btnPostText = (Button)findViewById(R.id.postTextBtn);
        btnPostText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent startPostText = new Intent(MainActivity.this, PostTextActivity.class);
				startActivity(startPostText);
			}
		});
        
        Button btnPostFromCamera = (Button)findViewById(R.id.postImageBtn);
        btnPostFromCamera.setOnClickListener(new View.OnClickListener() {			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, UploadImageActivity.class);
				startActivity(intent);
			}
		});
        
        findViewById(R.id.postVideoBtn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, UploadVideoActivity.class);
				startActivity(intent);
			}
		});
        
        CheckIsUserNameAndPasswordCorrect();
    }
    
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean result = super.onCreateOptionsMenu(menu);
        menu.add(0,1,0, "Settings");
        menu.add(0,2,0, "About");
        return result;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case 1:
            Intent startSettings = new Intent(MainActivity.this, SettingsActivity.class);
            startActivityForResult(startSettings, 0);
            return true;
        case 2:
        	createAboutDialog();
        	return true;
        }
       
        return super.onOptionsItemSelected(item);
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		CheckIsUserNameAndPasswordCorrect();
	}
	
	private void createAboutDialog()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("ttTumblr r1\n\nIf you find any errors please contact me so that I can fix them.\n\nKnown issues: selecting an image from gallery multiple times causes a crash.")
		       .setCancelable(true)
		       .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		                
		           }
		       });
		AlertDialog alert = builder.create();
		alert.show();
	}
    
    public void CheckIsUserNameAndPasswordCorrect()
    {
    	TextView infoView = (TextView)findViewById(R.id.labelAuthStatus);
    	
    	TumblrApi api = new TumblrApi(this);
    	if(!api.isUserNameAndPasswordStored())
    	{
    		infoView.setText("Please enter email and password in settings.");
    	} else {
    		infoView.setText("");
    	}
    }
}