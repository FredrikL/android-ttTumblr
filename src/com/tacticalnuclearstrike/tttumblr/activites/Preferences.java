package com.tacticalnuclearstrike.tttumblr.activites;

import java.util.ArrayList;
import java.lang.CharSequence;

import android.content.SharedPreferences;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.os.Build;

import android.util.Log;

import com.tacticalnuclearstrike.tttumblr.R;
import com.tacticalnuclearstrike.tttumblr.TumblrApi;

public class Preferences extends PreferenceActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        ListPreference bloglistpref = (ListPreference)this.findPreference("default_blog");
        SharedPreferences bloglist = getSharedPreferences(TumblrApi.BLOGS_PREFS, 0);

        bloglistpref.setEnabled(true);
        ArrayList<String> entries = new ArrayList();
        ArrayList<String> entryVals = new ArrayList();

        for (String k : bloglist.getAll().keySet()){
            entries.add(k);
            entryVals.add(bloglist.getString(k,""));
        }
        bloglistpref.setEntries(entries.toArray(new CharSequence[1]));
        bloglistpref.setEntryValues(entryVals.toArray(new CharSequence[1]));

    }

}

