package com.tacticalnuclearstrike.tttumblr;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
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

public class TumblrApi {
	// http://www.androidsnippets.org/snippets/1/
	// nonblocking
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

	private SharedPreferences getSharePreferences() {
		SharedPreferences settings = context.getSharedPreferences("tumblr", 0);
		return settings;
	}

	public boolean isUserNameAndPasswordStored()
	{
		return (getUserName().compareTo("") != 0) && (getPassword().compareTo("") != 0); 
	}
	
	public boolean validateUsernameAndPassword(String Username, String Password)
	{
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://www.tumblr.com/api/authenticate");

		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("email", Username));
			nameValuePairs
					.add(new BasicNameValuePair("password", Password));

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response = httpclient.execute(httppost);
			
			if(response.getStatusLine().getStatusCode() != 200)
			{
				return false;
			}
			return true;
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}

		
		return false;
	}
	
	public boolean postText(String Title, String Body) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://www.tumblr.com/api/write");

		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("email", getUserName()));
			nameValuePairs
					.add(new BasicNameValuePair("password", getPassword()));
			nameValuePairs.add(new BasicNameValuePair("type", "regular"));
			if (Body.compareTo("") != 0)
				nameValuePairs.add(new BasicNameValuePair("body", Body));
			if (Title.compareTo("") != 0)
				nameValuePairs.add(new BasicNameValuePair("title", Title));
			nameValuePairs.add(new BasicNameValuePair("generator", "ttTumblr"));

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response = httpclient.execute(httppost);
			
			if(response.getStatusLine().getStatusCode() != 201)
			{
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
			MultipartEntity entity = new MultipartEntity();

			entity.addPart("email", new StringBody(getUserName()));
			entity.addPart("password", new StringBody(getPassword()));
			entity.addPart("caption", new StringBody(caption));
			entity.addPart("type", new StringBody("photo"));
			entity.addPart("generator", new StringBody("ttTumblr"));
			entity.addPart("data", new FileBody(image));

			httppost.setEntity(entity);

			HttpResponse response = httpclient.execute(httppost);
			
			if(response.getStatusLine().getStatusCode() == 201)
				ShowNotification("ttTumblr", "Image Posted", "");
			else
				ShowNotification("ttTumblr", "Image upload failed", "");
		} catch (ClientProtocolException e) {
			ShowNotification("ttTumblr", "Image upload failed", e.toString());
		} catch (IOException e) {
			ShowNotification("ttTumblr", "Image upload failed", e.toString());
		}
	}
	
    public void ShowNotification(String tickerText, String contentTitle, String contentText)
	{
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(ns);
		
		int icon = R.drawable.tumblr24x24;
		long when = System.currentTimeMillis();

		Notification notification = new Notification(icon, tickerText, when);
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		
		Intent notificationIntent = new Intent(context, MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		

		mNotificationManager.notify(1, notification);
	}

	public void PostVideo(File videoToUpload, String caption) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://www.tumblr.com/api/write");

		try {
			MultipartEntity entity = new MultipartEntity();

			entity.addPart("email", new StringBody(getUserName()));
			entity.addPart("password", new StringBody(getPassword()));
			entity.addPart("caption", new StringBody(caption));
			entity.addPart("type", new StringBody("video"));
			entity.addPart("generator", new StringBody("ttTumblr"));
			entity.addPart("data", new FileBody(videoToUpload));

			httppost.setEntity(entity);

			HttpResponse response = httpclient.execute(httppost);
			
			if(response.getStatusLine().getStatusCode() == 201)
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
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("email", getUserName()));
			nameValuePairs
					.add(new BasicNameValuePair("password", getPassword()));
			nameValuePairs.add(new BasicNameValuePair("type", "quote"));
			if (quoteText.compareTo("") != 0)
				nameValuePairs.add(new BasicNameValuePair("quote", quoteText));
			if (sourceText.compareTo("") != 0)
				nameValuePairs.add(new BasicNameValuePair("source", sourceText));
			nameValuePairs.add(new BasicNameValuePair("generator", "ttTumblr"));

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response = httpclient.execute(httppost);
			
			if(response.getStatusLine().getStatusCode() != 201)
			{
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
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("email", getUserName()));
			nameValuePairs
					.add(new BasicNameValuePair("password", getPassword()));
			nameValuePairs.add(new BasicNameValuePair("type", "link"));
			if (url.compareTo("") != 0)
				nameValuePairs.add(new BasicNameValuePair("url", url));
			if (name.compareTo("") != 0)
				nameValuePairs.add(new BasicNameValuePair("name", name));
			if (description.compareTo("") != 0)
				nameValuePairs.add(new BasicNameValuePair("description", description));
			nameValuePairs.add(new BasicNameValuePair("generator", "ttTumblr"));

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response = httpclient.execute(httppost);
			
			if(response.getStatusLine().getStatusCode() != 201)
			{
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
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("email", getUserName()));
			nameValuePairs
					.add(new BasicNameValuePair("password", getPassword()));
			nameValuePairs.add(new BasicNameValuePair("type", "conversation"));
			if (title.compareTo("") != 0)
				nameValuePairs.add(new BasicNameValuePair("title", title));
			if (convo.compareTo("") != 0)
				nameValuePairs.add(new BasicNameValuePair("conversation", convo));
			nameValuePairs.add(new BasicNameValuePair("generator", "ttTumblr"));

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response = httpclient.execute(httppost);
			
			if(response.getStatusLine().getStatusCode() != 201)
			{
				ShowNotification("ttTumblr", "Conversation creation failed", "");
			}
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}

		return true;
	}
}
