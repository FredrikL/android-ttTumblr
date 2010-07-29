package com.tacticalnuclearstrike.tttumblr.activites;

import org.apache.http.entity.mime.content.StringBody;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.WindowManager;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.tacticalnuclearstrike.tttumblr.R;
import com.tacticalnuclearstrike.tttumblr.TumblrApi;

/**
 * Base class for activities which post content. provides the menu for setting
 * post options.
 */
public abstract class PostActivity extends Activity {

	private static final String TAG = "PostActivity";

	// menu group for blog selection list.
	protected static final int MENU_GROUP_BLOG = 1;
	protected static final int MENU_GROUP_TWEET = 2;
	protected static final int MENU_GROUP_PRIVATE = 3;
	protected static final int MENU_GROUP_TAGS = 4;

	protected Bundle mPostOptions;
	protected SharedPreferences mBloglist;
	protected SharedPreferences mPrefs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// set defaults for common post options.
		mPostOptions = TumblrApi.getDefaultPostOptions(this);
		mBloglist = getSharedPreferences(TumblrApi.BLOGS_PREFS, 0);
		mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		loadDefaultPostOptions();
		super.onCreate(savedInstanceState);

	}

	protected void returnToMainActivity() {
		setResult(RESULT_OK);
		finish();
	}

	protected void loadDefaultPostOptions() {
		// TODO: fill this in.
	}

	private void showTagsInputDialog() {
		final Dialog dialog = new Dialog(this);
		dialog.getWindow().setFlags( 
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND, 
				WindowManager.LayoutParams.FLAG_BLUR_BEHIND); 
		dialog.setTitle("Tags"); 
		dialog.setContentView(R.layout.inputtextbox);
		
	

		Button okButton = (Button) dialog.findViewById(R.id.btnInputOk);
		Button cancelButton = (Button) dialog.findViewById(R.id.btnInputCancel);
		final EditText text = (EditText) dialog.findViewById(R.id.inputText);
		okButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (!(text.getText().toString().equals(""))) {
					mPostOptions.putString("tags", text.getText().toString());
					Log.d("ttTumblr", text.getText().toString());
				}
				dialog.dismiss();
			}
		});

		cancelButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		if(mPostOptions.containsKey("tags"))
			text.setText(mPostOptions.getString("tags"));
		
		dialog.show();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		MenuItem tags = menu.add(MENU_GROUP_TAGS, Menu.NONE, Menu.NONE, "Tags");
		tags.setIcon(android.R.drawable.ic_menu_add);
		tags.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				showTagsInputDialog();
				return false;
			}
		});

		OnMenuItemClickListener blogchoice_listener = new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem mi) {
				Log.d(TAG, "setting tumblelog to "
						+ mBloglist.getString(mi.getTitle().toString(),
								"unknown!!"));
				mPostOptions.putString("group", mBloglist.getString(mi
						.getTitle().toString(), "unknown"));
				mi.setChecked(true);
				return true;
			}
		};

		SubMenu blogmenu = menu.addSubMenu(Menu.NONE, Menu.NONE, Menu.NONE,
				"Select Tumblelog");
		blogmenu.setIcon(android.R.drawable.ic_menu_agenda);
		// create the sub-menu based on the stored list of preferences.
		for (String k : mBloglist.getAll().keySet()) {
			MenuItem blogitem = blogmenu.add(MENU_GROUP_BLOG, Menu.NONE,
					Menu.NONE, k);
			blogitem.setOnMenuItemClickListener(blogchoice_listener);
			if (mPrefs.getString("default_blog", "NO DEFAULT").equals(
					mBloglist.getString(k, ""))) {
				// this is our default blog. mark it as selected.
				blogitem.setChecked(true);
			}
		}
		blogmenu.setGroupCheckable(MENU_GROUP_BLOG, true, true);

		// add a submenu for twitter
		SubMenu tweetmenu = menu.addSubMenu(Menu.NONE, Menu.NONE, Menu.NONE,
				"Twitter");
		tweetmenu.setIcon(android.R.drawable.ic_menu_share);
		MenuItem tweet = tweetmenu.add(MENU_GROUP_TWEET, Menu.NONE, Menu.NONE,
				"send to twitter");
		tweet.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem mi) {
				mPostOptions.putString("send-to-twitter", "auto");
				mi.setChecked(true);
				return true;
			}
		});

		MenuItem notweet = tweetmenu.add(MENU_GROUP_TWEET, Menu.NONE,
				Menu.NONE, "do not send");
		notweet.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem mi) {
				mPostOptions.putString("send-to-twitter", "no");
				mi.setChecked(true);
				return true;
			}
		});
		// set default item checked.
		if (mPostOptions.getString("send-to-twitter").equals("auto")) {
			tweet.setChecked(true);
		} else if (mPostOptions.getString("send-to-twitter").equals("no")) {
			notweet.setChecked(true);
		}
		tweetmenu.setGroupCheckable(MENU_GROUP_TWEET, true, true);

		// add a submenu for "private" posts.
		SubMenu privmenu = menu.addSubMenu(Menu.NONE, Menu.NONE, Menu.NONE,
				"Post Privately?");
		privmenu.setIcon(android.R.drawable.ic_menu_view);
		MenuItem mi = privmenu.add(MENU_GROUP_PRIVATE, Menu.NONE, Menu.NONE,
				"yes");
		mi.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem mi) {
				mPostOptions.putString("private", "1");
				mi.setChecked(true);
				return true;
			}
		});
		if (mPostOptions.getString("private").equals("1"))
			mi.setChecked(true);

		mi = privmenu.add(MENU_GROUP_PRIVATE, Menu.NONE, Menu.NONE, "no");
		mi.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem mi) {
				mPostOptions.putString("private", "0");
				mi.setChecked(true);
				return true;
			}
		});
		if (mPostOptions.getString("private").equals("0"))
			mi.setChecked(true);
		privmenu.setGroupCheckable(MENU_GROUP_PRIVATE, true, true);

		return true;
	}
}
