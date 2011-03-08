package com.tacticalnuclearstrike.tttumblr;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

public class TumblrApi {
	public static final String TAG = "TumblrApi";
	public static final String BLOGS_PREFS = "blogs";

	public static final String GENERATOR = "ttTumblr"; // user-agent string.

	private SharedPreferences mPrefs;

	private Context context;

	public TumblrApi(Context context) {
		this.context = context;
		mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
	}

	public String getUserName() {
		return getSharePreferences().getString("USERNAME", "");
	}

	public String getPassword() {
		return getSharePreferences().getString("PASSWORD", "");
	}

	public Boolean getIntegrateWithTwitter() {
		return getSharePreferences().getBoolean("TWITTER", false);
	}

	private SharedPreferences getSharePreferences() {
		SharedPreferences settings = context.getSharedPreferences("tumblr", 0);
		return settings;
	}

	public boolean isUserNameAndPasswordStored() {
		return (getUserName().compareTo("") != 0)
				&& (getPassword().compareTo("") != 0);
	}

	public boolean validateUsernameAndPassword(String Username, String Password) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(
				"http://www.tumblr.com/api/authenticate");

		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("email", Username));
			nameValuePairs.add(new BasicNameValuePair("password", Password));

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response = httpclient.execute(httppost);

			if (response.getStatusLine().getStatusCode() != 200) {
				return false;
			}
			// Save our list of available blogs.
			SharedPreferences blogs = context.getSharedPreferences(BLOGS_PREFS,
					0);
			Log.d("ttT", "attempting blog list extraction");
			saveBlogList(response, blogs);
			return true;

		} catch (ClientProtocolException e) {
			Log.d(TAG, "client proto exception", e);
		} catch (IOException e) {
			Log.d(TAG, "io exception", e);
		}
		return false;
	}

	public List<Cookie> authenticateAndReturnCookies() {
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://www.tumblr.com/login");

		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("email", getUserName()));
			nameValuePairs
					.add(new BasicNameValuePair("password", getPassword()));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			httpclient.execute(httppost);

			return httpclient.getCookieStore().getCookies();
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}

		return null;
	}

	private MultipartEntity getEntityWithBaseParamsSet(Boolean Private) {
		MultipartEntity entity = new MultipartEntity();
		try {
			entity.addPart("email", new StringBody(getUserName()));
			entity.addPart("password", new StringBody(getPassword()));
			entity.addPart("generator", new StringBody(GENERATOR));

		} catch (UnsupportedEncodingException e) {
			Log.e("ttTumblr", e.getMessage());
		}
		return entity;
	}

	/*
	 * post options are fields that can be set in the tumblr api: -
	 * send-to-twitter - private - group - tags - format etc.
	 */
	public MultipartEntity getEntityWithOptions(Bundle options) {
		MultipartEntity entity = getEntityWithBaseParamsSet(false);
		if (options == null) {
			return entity;
		}
		;
		try {
			// TODO: detect if the options have already been set?
			// FIXME: use a foreach loop here.
			if (options.containsKey("send-to-twitter")) {
				entity.addPart("send-to-twitter", new StringBody(options
						.getString("send-to-twitter")));
				Log.d(TAG, "send-to-twitter: "
						+ options.getString("send-to-twitter"));
			} else {
				// set the param from the defaults.
				if (mPrefs.getBoolean("twitter", false))
					entity.addPart("send-to-twitter", new StringBody("1"));
			}
			if (options.containsKey("group")) {
				entity.addPart("group", new StringBody(options
						.getString("group")
						+ ".tumblr.com"));
				Log.d(TAG, "group: " + options.getString("group"));
			}
			if (options.containsKey("private")) {
				entity.addPart("private", new StringBody(options
						.getString("private")));
				Log.d(TAG, "private: " + options.getString("private"));
			} else {
				// set the param from the defaults.
				if (mPrefs.getBoolean("private", false))
					entity.addPart("private", new StringBody("1"));
			}
			if (options.containsKey("format")) {
				entity.addPart("format", new StringBody(options.getString(
						"format").toLowerCase()));
				Log.d(TAG, "format: "
						+ options.getString("format").toLowerCase());
			}
			if (options.containsKey("tags")) {
				entity.addPart("tags",
						new StringBody(options.getString("tags")));
				Log.d(TAG, "tags: " + options.getString("tags"));
			}
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, "unsupported encoding: " + e.getMessage());
		}
		return entity;
	}

	/* helper to enclose all the http-related code */
	private HttpResponse postEntity(MultipartEntity entity) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://www.tumblr.com/api/write");
		try {
			httppost.setEntity(entity);
			HttpResponse response = httpclient.execute(httppost);
			return response;
		} catch (ClientProtocolException e) {
			Log.e(TAG, "client proto exception!", e);
		} catch (IOException e) {
			Log.e(TAG, "io exception", e);
		}
		return null;
	}

	public boolean postText(String title, String body, Bundle options) {
		MultipartEntity entity = getEntityWithOptions(options);
		try {
			entity.addPart("type", new StringBody("regular"));
			if (title != null)
				entity.addPart("title", new StringBody(title));
			if (body != null)
				entity.addPart("body", new StringBody(body));
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, e.getMessage());
		}
		HttpResponse response = postEntity(entity);
		try {
			Log.d(TAG, "Server said: "
					+ response.getStatusLine().getStatusCode());
		} catch (NullPointerException npe) {
			Log.e(TAG, "Response was null");
			return false;
		}
		if (response.getStatusLine().getStatusCode() != 201) {
			ShowNotification("ttTumblr", "Text creation failed", "");
			return false;
		}
		return true;
	}

	public void postImage(Uri image, String caption, Bundle options) {
		MultipartEntity entity = getEntityWithOptions(options);
		try {
			entity.addPart("type", new StringBody("photo"));
			if (caption != null)
				entity.addPart("caption", new StringBody(caption));

			File f = new File(image.getPath());
			entity.addPart("data", new FileBody(f));

		} catch (UnsupportedEncodingException e) {
			Log.d(TAG, e.getMessage());
		}
		HttpResponse response = postEntity(entity);
		try {
			Log.d(TAG, "Server said: "
					+ response.getStatusLine().getStatusCode());
		} catch (NullPointerException npe) {
			Log.e(TAG, "Response was null");
			ShowNotification("Image upload failed", "", "");
		}
		if (response.getStatusLine().getStatusCode() == 201)
			ShowNotification("Image Posted", "", "");
		else
			ShowNotification("Image upload failed", "", "");
	}

	public void ShowNotification(String tickerText, String contentTitle,
			String contentText) {
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(ns);

		int icon = R.drawable.tumblr24x24;
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, tickerText, when);
		notification.flags = Notification.FLAG_AUTO_CANCEL;

		Intent notificationIntent = new Intent(context, MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(context, contentTitle, contentText,
				contentIntent);

		mNotificationManager.notify(1, notification);
	}

	public void PostYoutubeUrl(String url, String caption, Bundle options) {
		try {
			MultipartEntity entity = getEntityWithOptions(options);

			entity.addPart("caption", new StringBody(caption));
			entity.addPart("type", new StringBody("video"));
			entity.addPart("embed ", new StringBody(url));

			HttpResponse response = postEntity(entity);
			Log.d(TAG, "Server said:"
					+ response.getStatusLine().getStatusCode());
			if (response.getStatusLine().getStatusCode() == 201)
				ShowNotification("ttTumblr", "Video Posted", "");
			else
				ShowNotification("ttTumblr", "Video post failed", "");
		} catch (IOException e) {
			ShowNotification("ttTumblr", "Video post failed", e.toString());
		}
	}

	public void PostVideo(Uri video, String caption, Bundle options) {
		try {
			MultipartEntity entity = getEntityWithOptions(options);

			entity.addPart("caption", new StringBody(caption));
			entity.addPart("type", new StringBody("video"));
			File f = new File(video.getPath());
			entity.addPart("data", new FileBody(f));

			HttpResponse response = postEntity(entity);
			Log.d(TAG, "Server said:"
					+ response.getStatusLine().getStatusCode());
			if (response.getStatusLine().getStatusCode() == 201)
				ShowNotification("ttTumblr", "Video Posted", "");
			else
				ShowNotification("ttTumblr", "Video upload failed", "");
		} catch (IOException e) {
			ShowNotification("ttTumblr", "Video upload failed", e.toString());
		}
	}

	public boolean postQuote(String quoteText, String sourceText, Bundle options) {
		MultipartEntity entity = getEntityWithOptions(options);
		try {
			entity.addPart("type", new StringBody("quote"));
			entity.addPart("quote", new StringBody(quoteText));
			if (sourceText != null)
				entity.addPart("source", new StringBody(sourceText));
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, e.getMessage());
		}
		HttpResponse response = postEntity(entity);
		if (response.getStatusLine().getStatusCode() != 201) {
			ShowNotification("ttTumblr", "Quote creation failed", "");
		}

		return true;
	}

	public boolean postUrl(String url, String name, String description,
			Bundle options) {
		MultipartEntity entity = getEntityWithOptions(options);
		try {
			entity.addPart("type", new StringBody("link"));
			entity.addPart("url", new StringBody(url));
			if (name != null)
				entity.addPart("name", new StringBody(name));
			if (description != null)
				entity.addPart("description", new StringBody(description));
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, e.getMessage());
		}
		HttpResponse response = postEntity(entity);
		if (response.getStatusLine().getStatusCode() != 201) {
			ShowNotification("Link creation failed", "", "");
		}

		return true;
	}

	public boolean postConversation(String title, String convo, Bundle options) {
		MultipartEntity entity = getEntityWithOptions(options);
		try {
			entity.addPart("type", new StringBody("conversation"));
			if (title != null)
				entity.addPart("title", new StringBody(title));
			if (convo != null)
				entity.addPart("conversation", new StringBody(convo));
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, e.getMessage());
		}
		HttpResponse response = postEntity(entity);
		if (response.getStatusLine().getStatusCode() != 201) {
			ShowNotification("Post creation failed", "", "");
			return false;
		}
		return true;
	}

	private void saveBlogList(HttpResponse r, SharedPreferences bloglist) {
		bloglist.edit().clear().commit();
		try {
			XmlPullParser xpp = XmlPullParserFactory.newInstance()
					.newPullParser();
			xpp.setInput(r.getEntity().getContent(), null);
			int eventType = xpp.getEventType();
			Log.d("ttT", "starting to loop...");
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG
						&& xpp.getName().equals("tumblelog")) {
					Log.d("ttT", "found a tumblelog.");
					String title = xpp.getAttributeValue(null, "title");
					String type = xpp.getAttributeValue(null, "type");
					if (type.equals("public")) {
						String name = xpp.getAttributeValue(null, "name");
						Log.d(TAG, "found public blog named: " + name);
						bloglist.edit().putString(title, name).commit();
						if (xpp.getAttributeValue(null, "is-primary") != null
								&& xpp.getAttributeValue(null, "is-primary")
										.equals("yes")) {
							// set the primary blog as our default.
							SharedPreferences prefs = PreferenceManager
									.getDefaultSharedPreferences(context);
							prefs.edit().putString("default_blog", name)
									.commit();
						}
					}
				}
				eventType = xpp.next();
			}
		} catch (XmlPullParserException e) {
			Log.e(TAG, "blog list parser failure", e);
		} catch (IOException e) {
			Log.e(TAG, "i/o error", e);
		}

	}

	/**
	 * Returns a Bundle with user's preferred default post options. settings are
	 * read from preferences, and can be overridden.
	 */
	public static Bundle getDefaultPostOptions(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		Bundle postoptions = new Bundle();
		if (prefs.getBoolean("twitter", false)) {
			postoptions.putString("send-to-twitter", "auto");
		} else {
			postoptions.putString("send-to-twitter", "no");
		}

		if (prefs.getBoolean("private", false)) {
			postoptions.putString("private", "1");
		} else {
			postoptions.putString("private", "0");
		}

		postoptions.putString("format", prefs.getString("text_format",
				"Markdown"));

		if (prefs.contains("default_blog")
				&& prefs.getString("default_blog", "").compareTo("") == 0)
			postoptions.putString("group", prefs.getString("default_blog", "")
					+ ".tumblr.com");

		return postoptions;
	}
}
