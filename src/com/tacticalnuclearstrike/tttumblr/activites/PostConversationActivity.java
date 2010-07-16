package com.tacticalnuclearstrike.tttumblr.activites;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tacticalnuclearstrike.tttumblr.R;

public class PostConversationActivity extends PostActivity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.postconversationview);

		setupOkButton();
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

        Intent postIntent = new Intent("com.tacticalnuclearstrike.tttumblr.POST_CONVERSATION");
        postIntent.putExtra("title", title);
        postIntent.putExtra("conversation", convo);
        postIntent.putExtra("options", mPostOptions);
        startService(postIntent);
		
		returnToMainActivity();
	}
}
