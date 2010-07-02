package com.tacticalnuclearstrike.tttumblr;

import android.app.Service;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import android.util.Log;

/** TumblrService - a Service subclass to interact with tumblr's api
 *
 * contains all the relevant bits for making calls to the backend web service.
 *
 * Intents received by this service (prefixed with package name):
 * * POST_TEXT - 
 */
public class TumblrService extends Service {
    private static final String TAG = "TumblrService";
    // notification integers.
    public static final int N_POSTING = 1; // we're currently posting something

    @Override
    public void onCreate() {
        Log.d(TAG, "oncreate!");
    }

    @Override
    public IBinder onBind(Intent i){return null;} // dont use onBind.

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "start intent received: " + intent.getAction());
        //XXX: assuming braindump here.
        startForeground(N_POSTING, getNotification());
        //do the posting.
        doTextPost(intent);
        stopForeground(true);
        return START_REDELIVER_INTENT;
    }

    //XXX: should these posts be cached somewhere so we can retry?
    private void doTextPost(Intent i){
        final String titleText = i.getStringExtra("title");
        final String postText = i.getStringExtra("body");
        final boolean privPost = true;
		final TumblrApi api = new TumblrApi(this);
		new Thread(new Runnable() {
			public void run() {
				api.postText(titleText, postText, privPost);
			}
		}).start();
    }

    @Override
    public void onDestroy() {
        // Tell the user we stopped.
        Toast.makeText(this, "tumblr service stopped!", Toast.LENGTH_SHORT).show();
    }

    private Notification getNotification(){
        Notification n = new Notification(android.R.drawable.stat_notify_sync, "Uploading to Tumblr...", 0);
        Intent i = new Intent("android.intent.action.MAIN");
        i.setClassName("com.tacticalnuclearstrike.tttumblr", "MainActivity");
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);
        n.setLatestEventInfo(this, "posting", "posting text", pi);
        return n;
    }
    
}
