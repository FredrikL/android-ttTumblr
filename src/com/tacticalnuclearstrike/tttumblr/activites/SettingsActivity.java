package com.tacticalnuclearstrike.tttumblr.activites;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;

import com.tacticalnuclearstrike.tttumblr.R;

public class SettingsActivity extends Activity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settingsview);   
        
        loadSettings();
    }

	private void loadSettings() {
		((CheckBox)findViewById(R.id.cbTwitter)).setChecked(integrateWithTwitter());
        ((CheckBox)findViewById(R.id.cbPostExtras)).setChecked(extraPostOptions());
	}
		
	@Override
	public void onPause()
	{
		super.onPause();
		saveSettings();
	}

	private void saveSettings() {
		saveTwitterStatus();
		saveExtraPostOptions();
	}
	
	private void saveTwitterStatus(){
		Boolean checked = ((CheckBox)findViewById(R.id.cbTwitter)).isChecked();
		
		SharedPreferences.Editor editor = getSharedPreferences().edit();
		editor.putBoolean("TWITTER", checked);
		editor.commit();
	}
	
	private void saveExtraPostOptions(){
		Boolean checked = ((CheckBox)findViewById(R.id.cbPostExtras)).isChecked();
		
		SharedPreferences.Editor editor = getSharedPreferences().edit();
		editor.putBoolean("POST_EXTRAS", checked);
		editor.commit();
	}
	
	private Boolean integrateWithTwitter()
	{
		return getSharedPreferences().getBoolean("TWITTER", false);
	}
	
	private Boolean extraPostOptions()
	{
		return getSharedPreferences().getBoolean("POST_EXTRAS", false);
	}
	
	private SharedPreferences getSharedPreferences() {
		SharedPreferences settings = this.getSharedPreferences("tumblr", 0);
		return settings;
	}
}
