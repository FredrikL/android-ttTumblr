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

import android.content.Context;
import android.content.SharedPreferences;

public class TumblrApi {
	// http://www.androidsnippets.org/snippets/1/
	// nonblocking
	private Context context;

	public TumblrApi(Context context) {
		this.context = context;
	}

	private String getUserName() {
		return getSharePreferences().getString("USERNAME", "");
	}

	private String getPassword() {
		return getSharePreferences().getString("PASSWORD", "");
	}

	private SharedPreferences getSharePreferences() {
		SharedPreferences settings = context.getSharedPreferences("tumblr", 0);
		return settings;
	}

	public boolean postRegular(String Title, String Body) {
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
		} catch (ClientProtocolException e) {
		} catch (IOException e) {
		}

		return true;
	}

	public void PostImage(File image) {
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost("http://www.tumblr.com/api/write");

		try {
			MultipartEntity entity = new MultipartEntity();

			entity.addPart("email", new StringBody(getUserName()));
			entity.addPart("password", new StringBody(getPassword()));
			entity.addPart("type", new StringBody("photo"));
			entity.addPart("generator", new StringBody("ttTumblr"));
			entity.addPart("data", new FileBody(image));

			httppost.setEntity(entity);

			HttpResponse response = httpclient.execute(httppost);
		} catch (ClientProtocolException e) {
			String s = e.toString();
		} catch (IOException e) {
			String s = e.toString();
		}

	}
}
