package com.tacticalnuclearstrike.tttumblr.activites;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tacticalnuclearstrike.tttumblr.R;
import com.tacticalnuclearstrike.tttumblr.TumblrApi;

public class PostConversationActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.postconversationview);

		setupOkButton();
	}
	
	private void returnToMainActivity() {
		setResult(RESULT_OK);
		finish();
	}

	private void setupOkButton() {
		Button btnOk = (Button) findViewById(R.id.postConversationBtnOk);
		btnOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				okButtonClick();
			}
		});
	}

	private void okButtonClick() {
		final String title = ((EditText)findViewById(R.id.inputTitle)).getText().toString();
		final String convo = ((EditText)findViewById(R.id.inputConversation)).getText().toString();
		
		if(convo.compareTo("") == 0){
			Toast.makeText(this, "Cannont create post without content!", Toast.LENGTH_SHORT).show();
			return;		
		}
		
		final TumblrApi api = new TumblrApi(this);
		
		Toast.makeText(this, "Creating post", Toast.LENGTH_LONG).show();
		
		new Thread(new Runnable() {
			public void run() {
				api.postConversation(title, convo);
			}
		}).start();
		
		returnToMainActivity();
	}
}
