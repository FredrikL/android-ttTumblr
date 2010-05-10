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
        
        ((CheckBox)findViewById(R.id.cbTwitter)).setChecked(integrateWithTwitter());
    }
		
	@Override
	public void onPause()
	{
		saveTwitterStatus();
	}
	
	private void saveTwitterStatus(){
		Boolean checked = ((CheckBox)findViewById(R.id.cbTwitter)).isChecked();
		
		SharedPreferences.Editor editor = getSharePreferences().edit();
		editor.putBoolean("TWITTER", checked);
		editor.commit();
	}
	
	private Boolean integrateWithTwitter()
	{
		return getSharePreferences().getBoolean("TWITTER", false);
	}
	
	private SharedPreferences getSharePreferences() {
		SharedPreferences settings = this.getSharedPreferences("tumblr", 0);
		return settings;
	}
}
