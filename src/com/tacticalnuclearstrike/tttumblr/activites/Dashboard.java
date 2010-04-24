package com.tacticalnuclearstrike.tttumblr.activites;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class Dashboard extends Activity {
	  @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	        WebView web = new WebView(this);
	        
	        setContentView(web);
	        
	        web.loadUrl("http://blog.tacticalnuclearstrike.com");
	     
	    }
}
