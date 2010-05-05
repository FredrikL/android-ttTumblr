package com.tacticalnuclearstrike.tttumblr.activites;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.tacticalnuclearstrike.tttumblr.R;
import com.tacticalnuclearstrike.tttumblr.TumblrApi;

public class PostTextActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.posttextview);

		setupOkButton();
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
		final Boolean privPost = ((CheckBox)findViewById(R.id.textPrivate)).isChecked();
		
		if(postText.compareTo("") == 0){
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
