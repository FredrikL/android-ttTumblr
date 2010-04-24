package com.tacticalnuclearstrike.tttumblr.activites;

import org.apache.http.Header;
import org.apache.http.HttpResponse;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.WebView;

import com.tacticalnuclearstrike.tttumblr.TumblrApi;

public class Dashboard extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		WebView web = new WebView(this);
		setContentView(web);

		TumblrApi api = new TumblrApi(this);
		HttpResponse responseWithCookies = api.authenticateAndReturnResponse();

		if (responseWithCookies != null) {
			Header[] header = responseWithCookies.getHeaders("Set-Cookie");
			String val = header[0].getValue();
			for (Header head : header) {
				CookieManager.getInstance().setCookie("http://www.tumblr.com",
						head.getValue());
			}
		}
		
		web.loadUrl("http://tumblr.com/iphone"); // wish it was named mobile :)
	}
}
