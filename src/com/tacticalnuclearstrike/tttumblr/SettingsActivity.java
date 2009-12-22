package com.tacticalnuclearstrike.tttumblr;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SettingsActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settingsview);

		setupControls();
	}

	private void setupControls() {
		setupOkButton();
		setupCancelButton();
	}

	private void setupOkButton() {
		Button btnOk = (Button) findViewById(R.id.settingsBtnOk);
		btnOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				saveSettings();
				returnToMainActivity();
			}
		});
	}

	private void returnToMainActivity() {
		setResult(RESULT_OK);
		finish();
	}

	private void setupCancelButton() {
		Button btnCancel = (Button) findViewById(R.id.settingsBtnCancel);
		btnCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				returnToMainActivity();
			}
		});
	}

	private void setSetting(String name, String value) {
		SharedPreferences settings = getSharedPreferences("tumblr", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(name, value);
		editor.commit();
	}

	private void saveSettings() {
		EditText text = (EditText) findViewById(R.id.inputUsername);
		setSetting("USERNAME", text.getEditableText().toString());

		text = (EditText) findViewById(R.id.inputPassword);
		setSetting("PASSWORD", text.getEditableText().toString());

	}
}
