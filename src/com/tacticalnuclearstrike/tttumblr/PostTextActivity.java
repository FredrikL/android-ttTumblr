package com.tacticalnuclearstrike.tttumblr;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class PostTextActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.posttextview);
        
        Spinner s = (Spinner) findViewById(R.id.spinnerType);
        ArrayAdapter adapter = ArrayAdapter.createFromResource(
                this, R.array.texttypes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);

    }
}
