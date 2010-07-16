package com.tacticalnuclearstrike.tttumblr.activites;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tacticalnuclearstrike.tttumblr.R;
import com.tacticalnuclearstrike.tttumblr.TumblrService;

public class PostLinkActivity extends PostActivity {
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
		
        Intent postIntent = new Intent(TumblrService.ACTION_POST_LINK);
        postIntent.putExtra("link", url);
        postIntent.putExtra("name", name);
        postIntent.putExtra("description", description);
        postIntent.putExtra("options", mPostOptions);
        startService(postIntent);
		
		returnToMainActivity();
	}
}
