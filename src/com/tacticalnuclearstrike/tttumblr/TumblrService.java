package com.tacticalnuclearstrike.tttumblr;

import android.app.Service;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import android.util.Log;

/** TumblrService - a Service subclass to interact with tumblr's api
 *
 * contains all the relevant bits for making calls to the backend web service.
 *
 * Intents received by this service (prefixed with package name):
 * * POST_TEXT - String title, String body, boolean isPrivate
 * * POST_PHOTO - Uri photo, String caption
 */
public class TumblrService extends Service {
    private static final String TAG = "TumblrService";
    // notification integers.
    public static final int N_POSTING = 1; // we're currently posting something

    //Actions:
    public static final String ACTION_POST_TEXT = "com.tacticalnuclearstrike.tttumblr.POST_TEXT";
    public static final String ACTION_POST_PHOTO = "com.tacticalnuclearstrike.tttumblr.POST_PHOTO";
    public static final String ACTION_POST_CONVERSATION = "com.tacticalnuclearstrike.tttumblr.POST_CONVERSATION";

    @Override
    public void onCreate() {
        Log.d(TAG, "oncreate!");
    }

    @Override
    public IBinder onBind(Intent i){return null;} // dont use onBind.

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "start intent received: " + intent.getAction());
        if (ACTION_POST_TEXT.equals(intent.getAction())){
            doTextPost(intent);
        } else if (ACTION_POST_PHOTO.equals(intent.getAction())) {
            doPhotoPost(intent);
        } else if (ACTION_POST_CONVERSATION.equals(intent.getAction())) {
            doConversationPost(intent);
        }
        else {
            Log.d(TAG, "UNKNOWN ACTION!");
        }
        return START_REDELIVER_INTENT;
    }

    //TODO: should these posts be cached somewhere so we can retry?
    private void doTextPost(Intent i){
        final String titleText = i.getStringExtra("title");
        final String postText = i.getStringExtra("body");
        final Bundle options = i.getBundleExtra("options");
		final TumblrApi api = new TumblrApi(this);
        Log.d(TAG, "attempting text post..");
		new Thread(new Runnable() {
			public void run() {
                startForeground(N_POSTING, getNotification());
                Log.d(TAG, "calling api.");
				api.postText(titleText, postText, options);
                stopForeground(true);
			}
		}).start();
    }

    /** doPhotoPost - posts a photo (given extras).
     * Extras: 'photo' - Uri, 'caption' - String.
     */
    private void doPhotoPost(Intent i){
        final Uri photo = Uri.parse(i.getStringExtra("photo"));
        final String text = i.getStringExtra("caption");
        final Bundle options = i.getBundleExtra("options");
		final TumblrApi api = new TumblrApi(this);
		new Thread(new Runnable() {
			public void run() {
                startForeground(N_POSTING, getNotification());
				api.postImage(photo, text, options);
                stopForeground(true);
			}
		}).start();
    }

    /** doConversationPost - posts a conversation.
     * Extras: 'title' - String, 'conversation' - String.
     */
    private void doConversationPost(Intent i){
        final String title = i.getStringExtra("title");
        final String convo = i.getStringExtra("conversation");
        final Bundle options = i.getBundleExtra("options");
		final TumblrApi api = new TumblrApi(this);
		new Thread(new Runnable() {
			public void run() {
                startForeground(N_POSTING, getNotification());
				api.postConversation(title, convo, options);
                stopForeground(true);
			}
		}).start();
    }

    private Notification getNotification(){
        Notification n = new Notification(android.R.drawable.stat_notify_sync, "Uploading to Tumblr...", 0);
        Intent i = new Intent("android.intent.action.MAIN");
        i.setClassName("com.tacticalnuclearstrike.tttumblr", "MainActivity");
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);
        n.setLatestEventInfo(this, "posting", "posting text", pi);
        return n;
    }

    @Override
    public void onDestroy() {
        // Tell the user we stopped.
        Toast.makeText(this, "tumblr service stopped!", Toast.LENGTH_SHORT).show();
    }
}
