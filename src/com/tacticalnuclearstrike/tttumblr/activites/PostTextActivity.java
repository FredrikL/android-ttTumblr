package com.tacticalnuclearstrike.tttumblr.activites;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tacticalnuclearstrike.tttumblr.R;
import com.tacticalnuclearstrike.tttumblr.TumblrApi;

public class PostTextActivity extends Activity {

    //menu group for blog selection list.
    private static final int BLOG_GROUP = 1;

    private Bundle mPostOptions = new Bundle();

	@Override
	public void onCreate(Bundle savedInstanceState) {
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
        postIntent.putExtra("isPrivate", privPost);
        startService(postIntent);
		
		returnToMainActivity();
	}

    private void loadDefaultPostOptions(){
        //TODO: fill this in.
    }

    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        SubMenu blogmenu = menu.addSubMenu(Menu.NONE, Menu.NONE, Menu.NONE, "Select Tumblelog");
        //create the sub-menu based on the stored list of preferences.
        SharedPreferences bloglist = getSharedPreferences(TumblrApi.BLOGS_PREFS, 0);
        for (String k : bloglist.getAll().keySet()){
            blogmenu.add(BLOG_GROUP, Menu.NONE, Menu.NONE, k);
        }
        blogmenu.add(BLOG_GROUP, Menu.NONE, Menu.NONE, "blog 1");
        blogmenu.add(BLOG_GROUP, Menu.NONE, Menu.NONE, "blog 2");
        blogmenu.setGroupCheckable(BLOG_GROUP, true, true); 

        return true;
    }
}
