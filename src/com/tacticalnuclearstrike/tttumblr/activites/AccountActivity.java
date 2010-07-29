package com.tacticalnuclearstrike.tttumblr.activites;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tacticalnuclearstrike.tttumblr.R;
import com.tacticalnuclearstrike.tttumblr.TumblrApi;

public class AccountActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.accountview);

		setupControls();

		loadUserNameAndPassword();
	}

	private void loadUserNameAndPassword() {
		TumblrApi api = new TumblrApi(this);
		EditText username = (EditText) findViewById(R.id.inputUsername);
		username.setText(api.getUserName());
		EditText password = (EditText) findViewById(R.id.inputPassword);
		password.setText(api.getPassword());
	}

	private void setupControls() {
		setupOkButton();
		setupCancelButton();
	}

	private void setupOkButton() {
		Button btnOk = (Button) findViewById(R.id.settingsBtnOk);
		btnOk.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				okButtonClick();
			}
		});
	}

	private void okButtonClick() {
		final ProgressDialog pd = ProgressDialog.show(this, "Authenticating",
				"Validating email/password with tumblr", true, false);
		new Thread(new Runnable() {
			public void run() {
				checkAuthentication();
				pd.dismiss();
			}
		}).start();
	}

	private void returnToMainActivity() {
		setResult(RESULT_OK);
		finish();
	}

	private void setupCancelButton() {
		Button btnCancel = (Button) findViewById(R.id.settingsBtnCancel);
		btnCancel.setOnClickListener(new View.OnClickListener() {
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

	private Boolean IsAuthenticationCorrect() {
		EditText text = (EditText) findViewById(R.id.inputUsername);
		String username = text.getEditableText().toString();

		text = (EditText) findViewById(R.id.inputPassword);
		String password = text.getEditableText().toString();

		TumblrApi api = new TumblrApi(this);
		Boolean result = api.validateUsernameAndPassword(username, password);
		return result;
	}

	private void checkAuthentication() {
		if (IsAuthenticationCorrect()) {
			saveSettings();
			returnToMainActivity();
		} else {
			Toast.makeText(this, "email and/or password incorrect",
					Toast.LENGTH_LONG).show();
		}
	}
}
