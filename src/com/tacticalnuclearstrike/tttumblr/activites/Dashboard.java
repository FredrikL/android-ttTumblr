package com.tacticalnuclearstrike.tttumblr.activites;

import java.util.List;

import org.apache.http.Header;
import org.apache.http.cookie.Cookie;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;

import com.tacticalnuclearstrike.tttumblr.TumblrApi;

public class Dashboard extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CookieSyncManager.createInstance(this);
		
		WebView web = new WebView(this);
		setContentView(web);

		TumblrApi api = new TumblrApi(this);
		List<Cookie> cookies = api.authenticateAndReturnCookies();

		
		if (cookies != null) {		
			for (Cookie cookie : cookies) {
				String cookieString = cookie.getName() + "=" + cookie.getValue() + "; domain=" + cookie.getDomain();
				CookieManager.getInstance().setCookie(cookie.getDomain(), cookieString);
			}
			CookieSyncManager.getInstance().sync();
		}
		
		web.loadUrl("http://www.tumblr.com/iphone"); // wish it was named mobile :)
	}
}
