package com.tacticalnuclearstrike.tttumblr;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/** TumblrService - a Service subclass to interact with tumblr's api
 *
 * contains all the relevant bits for making calls to the backend web service.
 *
 * Intents received by this service (prefixed with package name):
 * * POST_TEXT - String title, String body, boolean isPrivate
 * * POST_PHOTO - Uri photo, String caption
 */
public class TumblrService extends IntentService {
    public TumblrService() {    	
		super("Tumblr Upload service");
		Log.d(TAG, "TumblrService()");
	}

	private static final String TAG = "TumblrService";
    // notification integers.
    public static final int N_POSTING = 1; // we're currently posting something

    //Actions:
    public static final String ACTION_POST_TEXT = "com.tacticalnuclearstrike.tttumblr.POST_TEXT";
    public static final String ACTION_POST_PHOTO = "com.tacticalnuclearstrike.tttumblr.POST_PHOTO";
    public static final String ACTION_POST_VIDEO = "com.tacticalnuclearstrike.tttumblr.POST_VIDEO";
    public static final String ACTION_POST_CONVERSATION = "com.tacticalnuclearstrike.tttumblr.POST_CONVERSATION";
    public static final String ACTION_POST_QUOTE = "com.tacticalnuclearstrike.tttumblr.POST_QUOTE";
    public static final String ACTION_POST_LINK = "com.tacticalnuclearstrike.tttumblr.POST_LINK";

    @Override
    public void onCreate() {
    	super.onCreate();
        Log.d(TAG, "oncreate!");
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        try {
            mStartForeground = getClass().getMethod("startForeground",
                    mStartForegroundSignature);
            mStopForeground = getClass().getMethod("stopForeground",
                    mStopForegroundSignature);
        } catch (NoSuchMethodException e) {
            // Running on an older platform.
            mStartForeground = mStopForeground = null;
        }
    }
    
    private static final Class[] mStartForegroundSignature = new Class[] {
        int.class, Notification.class};
    private static final Class[] mStopForegroundSignature = new Class[] {
        boolean.class};

    private NotificationManager mNM;
    private Method mStartForeground;
    private Method mStopForeground;
    private Object[] mStartForegroundArgs = new Object[2];
    private Object[] mStopForegroundArgs = new Object[1];

    /**
     * This is a wrapper around the new startForeground method, using the older
     * APIs if it is not available.
     */
    void startForegroundCompat(int id, Notification notification) {
        // If we have the new startForeground API, then use it.
        if (mStartForeground != null) {
            mStartForegroundArgs[0] = Integer.valueOf(id);
            mStartForegroundArgs[1] = notification;
            try {
                mStartForeground.invoke(this, mStartForegroundArgs);
            } catch (InvocationTargetException e) {
                // Should not happen.
                Log.w("ttTumblr", "Unable to invoke startForeground", e);
            } catch (IllegalAccessException e) {
                // Should not happen.
                Log.w("ttTumblr", "Unable to invoke startForeground", e);
            }
            return;
        }

        // Fall back on the old API.
        setForeground(true);
        mNM.notify(id, notification);
    }

    /**
     * This is a wrapper around the new stopForeground method, using the older
     * APIs if it is not available.
     */
    void stopForegroundCompat(Boolean value) {
        // If we have the new stopForeground API, then use it.
        if (mStopForeground != null) {
            mStopForegroundArgs[0] = Boolean.TRUE;
            try {
                mStopForeground.invoke(this, mStopForegroundArgs);
            } catch (InvocationTargetException e) {
                // Should not happen.
                Log.w("ttTumblr", "Unable to invoke stopForeground", e);
            } catch (IllegalAccessException e) {
                // Should not happen.
                Log.w("ttTumblr", "Unable to invoke stopForeground", e);
            }
            return;
        }

        // Fall back on the old API.  Note to cancel BEFORE changing the
        // foreground state, since we could be killed at that point.
        //mNM.cancel(id);
        setForeground(false);
    }

    @Override
    public IBinder onBind(Intent i){return null;} // dont use onBind.
    
    @Override
    public void onStart(Intent intent, int startId) {
    	super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {    
    	super.onStartCommand(intent, flags, startId);
    	Log.d(TAG,"onStartCommand");
        return START_REDELIVER_INTENT;
    }

	private void handleCommand(Intent intent) {
		Log.d(TAG, "start intent received: " + intent.getAction());
        if (ACTION_POST_TEXT.equals(intent.getAction())){
            doTextPost(intent);
        } else if (ACTION_POST_PHOTO.equals(intent.getAction())) {
            doPhotoPost(intent);
        } else if (ACTION_POST_CONVERSATION.equals(intent.getAction())) {
            doConversationPost(intent);
        } else if (ACTION_POST_QUOTE.equals(intent.getAction())) {
            doQuotePost(intent);
        } else if (ACTION_POST_LINK.equals(intent.getAction())) {
            doUrlPost(intent);
        } else if (ACTION_POST_VIDEO.equals(intent.getAction())){
        	doVideoPost(intent);
        }
        else {
            Log.d(TAG, "UNKNOWN ACTION!");
        }
	}

    //TODO: should these posts be cached somewhere so we can retry?
    private void doTextPost(Intent i){
        final String titleText = i.getStringExtra("title");
        final String postText = i.getStringExtra("body");
        final Bundle options = i.getBundleExtra("options");
		final TumblrApi api = new TumblrApi(this);
        Log.d(TAG, "attempting text post..");
		startForegroundCompat(N_POSTING, getNotification("text"));
	    Log.d(TAG, "calling api.");
		api.postText(titleText, postText, options);
		stopForegroundCompat(true);
    }

    /** doPhotoPost - posts a photo (given extras).
     * Extras: 'photo' - Uri, 'caption' - String.
     */
    private void doPhotoPost(Intent i){
        final Uri photo = Uri.parse(i.getStringExtra("photo"));
        final String text = i.getStringExtra("caption");
        final Bundle options = i.getBundleExtra("options");
		final TumblrApi api = new TumblrApi(this);
		startForegroundCompat(N_POSTING, getNotification("photo"));
		api.postImage(photo, text, options);
		stopForegroundCompat(true);
    }
    
    private void doVideoPost(Intent i){
        Bundle options = i.getBundleExtra("options");
        String text = i.getStringExtra("caption");
        
        Uri video = null;
        if(i.hasExtra("video"))
        	video = Uri.parse(i.getStringExtra("video"));        
        String youtube_url = null;
        if(i.hasExtra("url"))
        	youtube_url = i.getStringExtra("url");
        
		TumblrApi api = new TumblrApi(this);
		startForegroundCompat(N_POSTING, getNotification("video"));
		if(video != null)
			api.PostVideo(video, text, options);
		else if(youtube_url!= null)
			api.PostYoutubeUrl(youtube_url, text, options);
		stopForegroundCompat(true);
    }

    /** doConversationPost - posts a conversation.
     * Extras: 'title' - String, 'conversation' - String.
     */
    private void doConversationPost(Intent i){
        final String title = i.getStringExtra("title");
        final String convo = i.getStringExtra("conversation");
        final Bundle options = i.getBundleExtra("options");
		final TumblrApi api = new TumblrApi(this);
		startForegroundCompat(N_POSTING, getNotification("conversation"));
		api.postConversation(title, convo, options);
		stopForegroundCompat(true);
    }

    /** doQuotePost - posts a quote
     * Extras: 'quote' - String, 'source' - String (optional).
     */
    private void doQuotePost(Intent i){
        final String quote = i.getStringExtra("quote");
        final String source = i.getStringExtra("source");
        final Bundle options = i.getBundleExtra("options");
		final TumblrApi api = new TumblrApi(this);
		startForegroundCompat(N_POSTING, getNotification("quote"));
		api.postQuote(quote, source, options);
		stopForegroundCompat(true);
    }

    /** doUrlPost - posts a link;
     * Extras: 'link' - String, 'name' - String, 'description' - String
     */
    private void doUrlPost(Intent i){
        final String link = i.getStringExtra("link");
        final String name = i.getStringExtra("name");
        final String description = i.getStringExtra("description");
        final Bundle options = i.getBundleExtra("options");
		final TumblrApi api = new TumblrApi(this);
		startForegroundCompat(N_POSTING, getNotification("url"));
		api.postUrl(link, name, description, options);
		stopForegroundCompat(true);
    }

    private Notification getNotification(String type){
        Notification n = new Notification(android.R.drawable.stat_notify_sync, "Uploading to Tumblr...", 0);
        Intent i = new Intent("android.intent.action.MAIN");
        i.setClassName("com.tacticalnuclearstrike.tttumblr", "MainActivity");
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);
        n.setLatestEventInfo(this, "posting", "posting "+type, pi);
        return n;
    }

    @Override
    public void onDestroy() {
    	Log.d(TAG, "tumblr service stopped!");
        stopForegroundCompat(true);
    }

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "onHandleIntent");
		handleCommand(intent);
	}
}
