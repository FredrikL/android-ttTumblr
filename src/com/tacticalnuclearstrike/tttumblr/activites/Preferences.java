package com.tacticalnuclearstrike.tttumblr.activites;

import java.util.ArrayList;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;

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

        if(bloglist.getAll().isEmpty()){
            //no entries, dont do anything.
        } else {
            bloglistpref.setEnabled(true);
            ArrayList<String> entries = new ArrayList<String>();
            ArrayList<String> entryVals = new ArrayList<String>();

            for (String k : bloglist.getAll().keySet()){
                entries.add(k);
                entryVals.add(bloglist.getString(k,""));
            }
            bloglistpref.setEntries(entries.toArray(new CharSequence[1]));
            bloglistpref.setEntryValues(entryVals.toArray(new CharSequence[1]));
        }

    }

}

