package com.tacticalnuclearstrike.tttumblr;

import com.tacticalnuclearstrike.tttumblr.TumblrApi.TumblrPostTypes;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class PostTextActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.posttextview);

		setupControls();
	}

	private void setupControls() {
		setupSpinner();
		setupOkButton();
		setupCancelButton();
	}

	private void setupSpinner() {
		Spinner s = (Spinner) findViewById(R.id.spinnerType);
		ArrayAdapter adapter = ArrayAdapter.createFromResource(this,
				R.array.texttypes, android.R.layout.simple_spinner_item);
		adapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		s.setAdapter(adapter);
	}
	
	private void returnToMainActivity() {
		// TODO Auto-generated method stub
		
	}

	private void setupCancelButton() {
		Button btnCancel = (Button) findViewById(R.id.postTextBtnCancel);
		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				returnToMainActivity();
			}	
		});
	}

	private void setupOkButton() {
		Button btnOk = (Button) findViewById(R.id.postTextBtnOk);
		btnOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText text = (EditText) findViewById(R.id.inputText);
				TumblrApi.postText(text.getText().toString(), TumblrPostTypes.Text);
				returnToMainActivity();
			}
		});
	}
}
