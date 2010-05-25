package com.tacticalnuclearstrike.tttumblr.activites;

import java.util.List;

import org.apache.http.cookie.Cookie;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.tacticalnuclearstrike.tttumblr.TumblrApi;

public class Dashboard extends Activity {
	private WebView webView;
	private ProgressDialog pd;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		CookieSyncManager.createInstance(this);
		pd = ProgressDialog.show(this, "Loading..",
				"Loading dashboard, please wait", true, false);

		webView = new WebView(this);

		new Thread(new Runnable() {
			public void run() {

				setContentView(webView);

				setupCookies();

				webView.getSettings().setJavaScriptEnabled(true);
				webView.setWebViewClient(new InsideWebViewClient());
				webView.loadUrl("http://www.tumblr.com/iphone");
			}
		}).start();
	}

	private void setupCookies() {
		TumblrApi api = new TumblrApi(this);
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
		
		@Override
		public void onPageFinished(WebView view, String url)
		{
			pd.dismiss();
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
