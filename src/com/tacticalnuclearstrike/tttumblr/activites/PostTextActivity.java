package com.tacticalnuclearstrike.tttumblr.activites;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;

import com.tacticalnuclearstrike.tttumblr.R;
import com.tacticalnuclearstrike.tttumblr.TumblrApi;

public class PostTextActivity extends Activity {

    public static final String TAG = "PostTextActivity";

    //menu group for blog selection list.
    private static final int MENU_GROUP_BLOG = 1;
    private static final int MENU_GROUP_TWEET = 2;
    private static final int MENU_GROUP_PRIVATE = 3;

    private Bundle mPostOptions;
    private SharedPreferences mBloglist;
    private SharedPreferences mPrefs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
        //set defaults for common post options.
        mPostOptions = TumblrApi.getDefaultPostOptions(this);
        mBloglist = getSharedPreferences(TumblrApi.BLOGS_PREFS, 0);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.posttextview);

		setupOkButton();

        loadDefaultPostOptions();
	}
	
	private void returnToMainActivity() {
		setResult(RESULT_OK);
		finish();
	}

	private void setupOkButton() {
		Button btnOk = (Button) findViewById(R.id.postTextBtnOk);
		btnOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				okButtonClick();
			}
		});
	}

	private void okButtonClick() {
		EditText title = (EditText) findViewById(R.id.inputText);
		EditText post = (EditText) findViewById(R.id.inputPost);
		
		final String titleText = title.getText().toString();
		final String postText = post.getText().toString();
		final Boolean privPost = false;
		
		if(titleText.compareTo("") == 0 && postText.compareTo("") == 0){
			Toast.makeText(this, "Cannont create post without content!", Toast.LENGTH_SHORT).show();
			return;		
		}
		
        Intent postIntent = new Intent("com.tacticalnuclearstrike.tttumblr.POST_TEXT");
        postIntent.putExtra("title", titleText);
        postIntent.putExtra("body", postText);
        postIntent.putExtra("options", mPostOptions);
        startService(postIntent);
		
		returnToMainActivity();
	}

    private void loadDefaultPostOptions(){
        //TODO: fill this in.
    }

    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        
        OnMenuItemClickListener blogchoice_listener = new OnMenuItemClickListener(){
            public boolean onMenuItemClick(MenuItem mi){
                Log.d(TAG, "setting tumblelog to " + mBloglist.getString(mi.getTitle().toString(), "unknown!!"));
                mPostOptions.putString("group", mBloglist.getString(mi.getTitle().toString(), "unknown"));
                return true;
            }
        };
                
        SubMenu blogmenu = menu.addSubMenu(Menu.NONE, Menu.NONE, Menu.NONE, "Select Tumblelog");
        //create the sub-menu based on the stored list of preferences.
        for (String k : mBloglist.getAll().keySet()){
            MenuItem blogitem = blogmenu.add(MENU_GROUP_BLOG, Menu.NONE, Menu.NONE, k);
            blogitem.setOnMenuItemClickListener(blogchoice_listener);
            if(mPrefs.getString("default_blog", "NO DEFAULT").equals(mBloglist.getString(k,""))){
                //this is our default blog. mark it as selected.
                blogitem.setChecked(true);
            }
        }
        blogmenu.setGroupCheckable(MENU_GROUP_BLOG, true, true); 

        //add a submenu for twitter
        SubMenu tweetmenu = menu.addSubMenu(Menu.NONE, Menu.NONE, Menu.NONE, "Twitter");
        MenuItem tweet = tweetmenu.add(MENU_GROUP_TWEET, Menu.NONE, Menu.NONE, "send to twitter");
        tweet.setOnMenuItemClickListener(new OnMenuItemClickListener(){
            public boolean onMenuItemClick(MenuItem mi){
                mPostOptions.putString("send-to-twitter", "auto");
                return true;
            }
        });
        if( mPostOptions.getString("send-to-twitter").equals("auto"))
            tweet.setChecked(true);

        MenuItem notweet = tweetmenu.add(MENU_GROUP_TWEET, Menu.NONE, Menu.NONE, "do not send");
        notweet.setOnMenuItemClickListener(new OnMenuItemClickListener(){
            public boolean onMenuItemClick(MenuItem mi){
                mPostOptions.putString("send-to-twitter", "no");
                return true;
            }
        });
        if( mPostOptions.getString("send-to-twitter").equals("no"))
            notweet.setChecked(true);
        tweetmenu.setGroupCheckable(MENU_GROUP_TWEET, true, true); 

        //add a submenu for "private" posts.
        SubMenu privmenu = menu.addSubMenu(Menu.NONE, Menu.NONE, Menu.NONE, "Post Privately?");
        MenuItem mi = privmenu.add(MENU_GROUP_PRIVATE, Menu.NONE, Menu.NONE, "yes");
        mi.setOnMenuItemClickListener(new OnMenuItemClickListener(){
            public boolean onMenuItemClick(MenuItem mi){
                mPostOptions.putString("private", "1");
                return true;
            }
        });
        if( mPostOptions.getString("private").equals("1"))
            mi.setChecked(true);

        mi = privmenu.add(MENU_GROUP_PRIVATE, Menu.NONE, Menu.NONE, "no");
        mi.setOnMenuItemClickListener(new OnMenuItemClickListener(){
            public boolean onMenuItemClick(MenuItem mi){
                mPostOptions.putString("private", "0");
                return true;
            }
        });
        if( mPostOptions.getString("private").equals("0"))
            mi.setChecked(true);
        privmenu.setGroupCheckable(MENU_GROUP_PRIVATE, true, true); 

        return true;
    }
}
