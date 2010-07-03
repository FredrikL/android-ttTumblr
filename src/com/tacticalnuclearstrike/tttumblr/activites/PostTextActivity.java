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

public class PostTextActivity extends Activity {
	private final static String[] URL_PREFIXES = {"http://", "https://", "ftp://", "sftp://"};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.posttextview);

		setupOkButton();

		Intent intent = getIntent();
		String action = intent.getAction();
		// SEND intents for URLs (from, for instance, the Browser) are also text,
		// which is somewhat tedious. While it means that you can share a URL to
		// an email program easily, it also means that you can't distinguish
		// between an URL and plain body text. So we make this activity receive both
		// text and URLs and launch the appropriate activity.
		if (Intent.ACTION_SEND.equals(action)){
			String textBody = intent.getExtras().getString(Intent.EXTRA_TEXT);

			boolean isUrl = false;
			for (String urlPrefix: URL_PREFIXES){
				isUrl = textBody.startsWith(urlPrefix) && !textBody.contains("\n") && !textBody.contains("\r");
				if (isUrl){
					break;
				}
			}
			if (isUrl){
				Intent urlLaunchIntent = new Intent(getIntent());
				urlLaunchIntent.setClass(getApplicationContext(), PostLinkActivity.class);
				startActivity(urlLaunchIntent);
				finish();
			}else{
				((EditText)findViewById(R.id.inputPost)).setText(textBody);
			}
		}
	}
	
	private void returnToMainActivity() {
		setResult(RESULT_OK);
		finish();
	}

	private void setupOkButton() {
		Button btnOk = (Button) findViewById(R.id.postTextBtnOk);
		btnOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				okButtonClick();
			}
		});
	}

	private void okButtonClick() {
		EditText title = (EditText) findViewById(R.id.inputText);
		EditText post = (EditText) findViewById(R.id.inputPost);
		
		final String titleText = title.getText().toString();
		final String postText = post.getText().toString();
		final Boolean privPost = false;
		
		if(titleText.compareTo("") == 0 && postText.compareTo("") == 0){
			Toast.makeText(this, "Cannont create post without content!", Toast.LENGTH_SHORT).show();
			return;		
		}
		
		final TumblrApi api = new TumblrApi(this);
		
		Toast.makeText(this, "Creating post", Toast.LENGTH_LONG).show();
		
		new Thread(new Runnable() {
			public void run() {
				api.postText(titleText, postText, privPost);
			}
		}).start();
		
		returnToMainActivity();
	}
}
