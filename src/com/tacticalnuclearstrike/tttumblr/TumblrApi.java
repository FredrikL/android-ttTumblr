package com.tacticalnuclearstrike.tttumblr;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.FileNotFoundException;
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
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.webkit.MimeTypeMap;

import org.nerdcircus.android.tumblr.MediaUriBody;

public class TumblrApi {
    public static final String TAG = "TumblrApi";
    public static final String BLOGS_PREFS = "blogs";

    public static final String GENERATOR = "ttTumblr"; //user-agent string.

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
            //Save our list of available blogs.
            SharedPreferences blogs = context.getSharedPreferences(BLOGS_PREFS, 0);
            saveBlogList(response, blogs);
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
			entity.addPart("generator", new StringBody(GENERATOR));

		} catch (UnsupportedEncodingException e) {
			Log.e("ttTumblr", e.getMessage());
		}
		return entity;
	}

    /* post options are fields that can be set in the tumblr api:
     * - send-to-twitter 
     * - private
     * - group
     * - tags
     * - format
     * etc. 
     */
	public MultipartEntity getEntityWithOptions(Bundle options) {
        MultipartEntity entity = getEntityWithBaseParamsSet(false);
        if (options == null){ return entity; };
        try {
            //TODO: detect if the options have already been set?
            //FIXME: use a foreach loop here.
            if( options.containsKey("send-to-twitter")){
                entity.addPart("send-to-twitter", new StringBody(options.getString("send-to-twitter")));
                Log.d(TAG, "send-to-twitter: " + options.getString("send-to-twitter"));
            }
            else {
                //set the param from the defaults.
                if (mPrefs.getBoolean("twitter",false))
                    entity.addPart("send-to-twitter", new StringBody("1"));
            }
            if( options.containsKey("group")){
                entity.addPart("group", new StringBody(options.getString("group")));
                Log.d(TAG, "group: " + options.getString("group"));
            }
            if( options.containsKey("private")){
                entity.addPart("private", new StringBody(options.getString("private")));
                Log.d(TAG, "private: " + options.getString("private"));
            }
            else {
                //set the param from the defaults.
                if (mPrefs.getBoolean("private",false))
                    entity.addPart("private", new StringBody("1"));
            }
		} catch (UnsupportedEncodingException e) {
			Log.e(TAG, "unsupported encoding: " + e.getMessage());
		}
        return entity;
    }

    /* helper to enclose all the http-related code */
    private HttpResponse postEntity(MultipartEntity entity){
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
            if(title != null)
                entity.addPart("title", new StringBody(title));
            if(body != null)
                entity.addPart("body", new StringBody(body));
        }
        catch(UnsupportedEncodingException e){
            Log.e(TAG, e.getMessage());
        }
        HttpResponse response = postEntity(entity);
        if (response.getStatusLine().getStatusCode() != 201) {
            ShowNotification("ttTumblr", "Text creation failed", "");
            return false;
        }
        return true;
    }

	public boolean postText(String Title, String Body, Boolean Private) {
        //Backward-compatability
        Bundle o = new Bundle();
        if(Private)
            o.putString("private", "1");
        return postText(Title, Body, o);
	}


	public void postImage(Uri image, String caption, Bundle options) {
        MultipartEntity entity = getEntityWithOptions(options);
        try {
            entity.addPart("type", new StringBody("photo"));
            if(caption != null)
                entity.addPart("caption", new StringBody(caption));
            // Do the MediaUriBody dance.
            // Getting the type of the file
            ContentResolver cR = context.getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            String type = mime.getExtensionFromMimeType(cR.getType(image));
            //TODO: fix filename to be something unique.
            InputStream stream = cR.openInputStream(image);
            entity.addPart("data", new MediaUriBody(context, image, stream, "file."+type));

        } catch (FileNotFoundException e) {
            //from MediaUriBody constructor.
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            Log.d(TAG, e.getMessage());
        }
        HttpResponse response = postEntity(entity);
        if (response.getStatusLine().getStatusCode() == 201)
            ShowNotification("ttTumblr", "Image Posted", "");
        else
            ShowNotification("ttTumblr", "Image upload failed", "");
    }

    //backward compatability
	public void PostImage(Uri image, String caption) {
        postImage(image, caption, new Bundle());
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

    private void saveBlogList(HttpResponse r, SharedPreferences bloglist){
        try{
            XmlPullParser xpp = XmlPullParserFactory.newInstance().newPullParser();
            xpp.setInput(r.getEntity().getContent(), null);
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if(eventType == XmlPullParser.START_TAG && xpp.getName().equals("tumblelog")){
                    String title = xpp.getAttributeValue(null, "title");
                    String type = xpp.getAttributeValue(null, "type");
                    if(type.equals("public")){
                        String name = xpp.getAttributeValue(null, "name");  
                        Log.d(TAG, "found public blog named: " + name);
                        bloglist.edit().putString(title, name).commit();
                    }
                }
                xpp.next();
            }
        } catch (XmlPullParserException e){
            Log.e(TAG, "blog list parser failure", e);
        } catch (IOException e){
            Log.e(TAG, "i/o error", e);
        }

    }
}
