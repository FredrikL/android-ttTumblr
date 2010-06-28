package com.tacticalnuclearstrike.tttumblr.activites;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tacticalnuclearstrike.tttumblr.R;
import com.tacticalnuclearstrike.tttumblr.TumblrApi;

public class PostLinkActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.postlinkview);

		setupOkButton();

		Intent intent = getIntent();
		String action = intent.getAction();
		if (Intent.ACTION_SEND.equals(action)){
			((EditText)findViewById(R.id.inputUrl)).setText(intent.getExtras().getString(Intent.EXTRA_TEXT));
		}
	}
	
	private void returnToMainActivity() {
		setResult(RESULT_OK);
		finish();
	}

	private void setupOkButton() {
		Button btnOk = (Button) findViewById(R.id.postQuoteBtnOk);
		btnOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				okButtonClick();
			}
		});
	}

	private void okButtonClick() {
		
		final String name = ((EditText)findViewById(R.id.inputName)).getText().toString();
		final String description = ((EditText)findViewById(R.id.inputDescription)).getText().toString();
		final String url = ((EditText)findViewById(R.id.inputUrl)).getText().toString();
		
		if(url.compareTo("") == 0){
			Toast.makeText(this, "Cannont create post without content!", Toast.LENGTH_SHORT).show();
			return;		
		}
		
		final TumblrApi api = new TumblrApi(this);
		
		Toast.makeText(this, "Creating post", Toast.LENGTH_LONG).show();
		
		new Thread(new Runnable() {
			public void run() {
				api.postUrl(url, name, description);
			}
		}).start();
		
		returnToMainActivity();
	}
}
