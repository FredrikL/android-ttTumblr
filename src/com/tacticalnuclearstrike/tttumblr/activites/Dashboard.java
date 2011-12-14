package com.tacticalnuclearstrike.tttumblr.activites;

import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.google.inject.Inject;
import com.tacticalnuclearstrike.tttumblr.TumblrApi;
import org.apache.http.cookie.Cookie;
import roboguice.activity.RoboActivity;

import java.util.List;

public class Dashboard extends RoboActivity {
	private WebView webView;

    @Inject TumblrApi api;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		CookieSyncManager.createInstance(this);

		webView = new WebView(this);

		setContentView(webView);

		setupCookies();

		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebViewClient(new InsideWebViewClient());
		webView.getSettings().setBuiltInZoomControls(true);
		webView.loadUrl("http://www.tumblr.com/iphone");
	}

	private void setupCookies() {
		List<Cookie> cookies = api.authenticateAndReturnCookies();

		if (cookies != null) {
			for (Cookie cookie : cookies) {
				String cookieString = cookie.getName() + "="
						+ cookie.getValue() + "; domain=" + cookie.getDomain();
				CookieManager.getInstance().setCookie(cookie.getDomain(),
						cookieString);
			}
			CookieSyncManager.getInstance().sync();
		}
	}

	private class InsideWebViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if (url.toLowerCase().contains("tumblr.com")) {
				view.loadUrl(url);
				return true;
			}
			return false;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
			webView.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
