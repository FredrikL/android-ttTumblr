package com.tacticalnuclearstrike.tttumblr.activites;

import java.util.List;

import org.apache.http.cookie.Cookie;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

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
		
		web.getSettings().setJavaScriptEnabled(true);
		web.setWebViewClient(new InsideWebViewClient());
		web.loadUrl("http://www.tumblr.com/iphone"); // wish it was named mobile :)
	}
	
	// from http://www.androidsnippets.org/snippets/26/
	private class InsideWebViewClient extends WebViewClient {  
        @Override  
        public boolean shouldOverrideUrlLoading(WebView view, String url) {  
            view.loadUrl(url);  
            return true;  
        }  
    }  
}
