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

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class TumblrApi {
	private Context context;

	public TumblrApi(Context context) {
		this.context = context;
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
			return true;
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
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
			if (Private)
				entity.addPart("private", new StringBody("1"));
			entity.addPart("generator", new StringBody("ttTumblr"));

			if (getIntegrateWithTwitter()) {
				entity.addPart("send-to-twitter", new StringBody("auto"));
			} else {
				entity.addPart("send-to-twitter", new StringBody("no"));
			}

		} catch (UnsupportedEncodingException e) {
			Log.e("ttTumblr", e.getMessage());
		}
		return entity;
	}

	public boolean postText(String Title, String Body, Boolean Private) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://www.tumblr.com/api/write");
		try {
			MultipartEntity entity = getEntityWithBaseParamsSet(Private);
			entity.addPart("type", new StringBody("regular"));
			if (Body.compareTo("") != 0)
				entity.addPart("body", new StringBody(Body));
			if (Title.compareTo("") != 0)
				entity.addPart("title", new StringBody(Title));

			httppost.setEntity(entity);

			HttpResponse response = httpclient.execute(httppost);

			if (response.getStatusLine().getStatusCode() != 201) {
				ShowNotification("ttTumblr", "Text creation failed", "");
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}

		return true;
	}

	public void PostImage(File image, String caption) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://www.tumblr.com/api/write");

		try {
			MultipartEntity entity = getEntityWithBaseParamsSet(false);

			entity.addPart("caption", new StringBody(caption));
			entity.addPart("type", new StringBody("photo"));
			entity.addPart("data", new FileBody(image));

			httppost.setEntity(entity);

			HttpResponse response = httpclient.execute(httppost);

			if (response.getStatusLine().getStatusCode() == 201)
				ShowNotification("ttTumblr", "Image Posted", "");
			else
				ShowNotification("ttTumblr", "Image upload failed", "");
		} catch (ClientProtocolException e) {
			ShowNotification("ttTumblr", "Image upload failed", e.toString());
		} catch (IOException e) {
			ShowNotification("ttTumblr", "Image upload failed", e.toString());
		}
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

	public void PostVideo(File videoToUpload, String caption) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://www.tumblr.com/api/write");

		try {
			MultipartEntity entity = getEntityWithBaseParamsSet(false);

			entity.addPart("caption", new StringBody(caption));
			entity.addPart("type", new StringBody("video"));
			entity.addPart("data", new FileBody(videoToUpload));

			httppost.setEntity(entity);

			HttpResponse response = httpclient.execute(httppost);

			if (response.getStatusLine().getStatusCode() == 201)
				ShowNotification("ttTumblr", "Video Posted", "");
			else
				ShowNotification("ttTumblr", "Video upload failed", "");
		} catch (ClientProtocolException e) {
			ShowNotification("ttTumblr", "Video upload failed", e.toString());
		} catch (IOException e) {
			ShowNotification("ttTumblr", "Video upload failed", e.toString());
		}
	}

	public boolean postQuote(String quoteText, String sourceText) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://www.tumblr.com/api/write");

		try {
			MultipartEntity entity = getEntityWithBaseParamsSet(false);

			if (quoteText.compareTo("") != 0)
				entity.addPart("quote", new StringBody(quoteText));
			if (sourceText.compareTo("") != 0)
				entity.addPart("source", new StringBody(sourceText));
			entity.addPart("type", new StringBody("quote"));

			httppost.setEntity(entity);

			HttpResponse response = httpclient.execute(httppost);

			if (response.getStatusLine().getStatusCode() != 201) {
				ShowNotification("ttTumblr", "Quote creation failed", "");
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}

		return true;
	}

	public boolean postUrl(String url, String name, String description) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://www.tumblr.com/api/write");

		try {
			MultipartEntity entity = getEntityWithBaseParamsSet(false);

			if (url.compareTo("") != 0)
				entity.addPart("url", new StringBody(url));
			if (name.compareTo("") != 0)
				entity.addPart("name", new StringBody(name));
			if (description.compareTo("") != 0)
				entity.addPart("description", new StringBody(description));
			entity.addPart("type", new StringBody("link"));

			httppost.setEntity(entity);

			HttpResponse response = httpclient.execute(httppost);

			if (response.getStatusLine().getStatusCode() != 201) {
				ShowNotification("ttTumblr", "Link creation failed", "");
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}

		return true;
	}

	public boolean postConversation(String title, String convo) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://www.tumblr.com/api/write");

		try {
			MultipartEntity entity = getEntityWithBaseParamsSet(false);

			if (title.compareTo("") != 0)
				entity.addPart("title", new StringBody(title));
			if (convo.compareTo("") != 0)
				entity.addPart("conversation", new StringBody(convo));
			entity.addPart("type", new StringBody("conversation"));

			httppost.setEntity(entity);

			HttpResponse response = httpclient.execute(httppost);

			if (response.getStatusLine().getStatusCode() != 201) {
				ShowNotification("ttTumblr", "Conversation creation failed", "");
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}

		return true;
	}
}
