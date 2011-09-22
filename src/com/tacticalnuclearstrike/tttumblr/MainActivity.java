package com.tacticalnuclearstrike.tttumblr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.google.inject.Inject;
import com.tacticalnuclearstrike.tttumblr.activites.*;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

public class MainActivity extends RoboActivity {

    final int MENU_ACCOUNT = 1;
    final int MENU_ABOUT = 2;
    final int MENU_SETTINGS = 3;

    GoogleAnalyticsTracker tracker;

    @Inject
    TumblrApi api;

    @InjectView(R.id.postOauth)
    Button btnOauth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tracker = GoogleAnalyticsTracker.getInstance();
        tracker.start("UA-9100060-3", 20, this);
        tracker.trackPageView("/OS/" + Build.VERSION.SDK);
        tracker.trackPageView("/rev/" + getApplicationVersion());

        regularStartup();

        if (showDashBoard())
            startDashboardActivity();
        try {

        final TumblrOAuth helper = new TumblrOAuth("ve8Bxcc57SweaoDD4i6gQgkCdqwYDZVPG3dlw37hoyXTawTy8l", "rgfOP9QuWJRgv1gIGv8v1NGdRkkFFMY2kP22WwgRNTPFIAH3wc",
                     "ttt://tacticalnuclearstrike.com");
        btnOauth.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                try {
                    String uri = helper.getRequestToken();
                    startActivity(new Intent("android.intent.action.VIEW", Uri.parse(uri)));
                } catch (Exception e) {
                    Log.e("ttTumblr", e.getMessage(), e);
                }

            }
        });


            String[] token = getVerifier();
            if(token == null) return;
            String[] lol = helper.getAccessToken(token[1]);
            //if (token != null)
            //    String accessToken[] = getAccessToken(token[1]);
        } catch (Exception e) {
        }
    }

    private String[] getVerifier() {
        // extract the token if it exists
        Uri uri = this.getIntent().getData();
        if (uri == null) {
            return null;
        }

        String token = uri.getQueryParameter("oauth_token");
        String verifier = uri.getQueryParameter("oauth_verifier");
        return new String[]{token, verifier};
    }


    private void regularStartup() {
        setContentView(R.layout.main);

        setupButtons();

        CheckIsUserNameAndPasswordCorrect();
    }

    private boolean showDashBoard() {
        return getSharePreferences().getBoolean("DASHBOARD_STARTUP", false);
    }

    private SharedPreferences getSharePreferences() {
        SharedPreferences settings = this.getSharedPreferences("tumblr", 0);
        return settings;
    }

    private void setupButtons() {
        findViewById(R.id.postTextBtn).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        tracker.trackPageView("/PostTextActivity");
                        Intent startPostText = new Intent(MainActivity.this,
                                PostTextActivity.class);
                        startActivity(startPostText);
                    }
                });

        findViewById(R.id.postImageBtn).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        tracker.trackPageView("/UploadImageActivity");
                        Intent intent = new Intent(MainActivity.this,
                                UploadImageActivity.class);
                        startActivity(intent);
                    }
                });

        findViewById(R.id.postVideoBtn).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        tracker.trackPageView("/UploadVideoActivity");
                        Intent intent = new Intent(MainActivity.this,
                                UploadVideoActivity.class);
                        startActivity(intent);
                    }
                });

        findViewById(R.id.postQuoteBtn).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        tracker.trackPageView("/PostQuoteActivity");
                        Intent intent = new Intent(MainActivity.this,
                                PostQuoteActivity.class);
                        startActivity(intent);
                    }
                });

        findViewById(R.id.postLinkBtn).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        tracker.trackPageView("/PostLinkActivity");
                        Intent intent = new Intent(MainActivity.this,
                                PostLinkActivity.class);
                        startActivity(intent);
                    }
                });

        findViewById(R.id.postConversationBtn).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        tracker.trackPageView("/PostConversationActivity");
                        Intent intent = new Intent(MainActivity.this,
                                PostConversationActivity.class);
                        startActivity(intent);
                    }
                });

        findViewById(R.id.dashboardBtn).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        tracker.trackPageView("/DashboardActivity");
                        startDashboardActivity();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tracker.stop();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_account:
                tracker.trackPageView("/AccountActivity");
                startActivityForResult(new Intent(MainActivity.this,
                        AccountActivity.class), 0);
                return true;
            case R.id.menu_about:
                tracker.trackPageView("/AboutDialog");
                createAboutDialog();
                return true;
            case R.id.menu_settings:
                tracker.trackPageView("/Preferences");
                startActivity(new Intent(MainActivity.this, Preferences.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        CheckIsUserNameAndPasswordCorrect();
    }

    private void createAboutDialog() {
        String version = getApplicationVersion();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setMessage(
                        "ttTumblr "
                                + version
                                + "\n\nIf you find any errors please contact me so that I can fix them!")
                .setCancelable(true).setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private String getApplicationVersion() {
        PackageManager pm = getPackageManager();
        String version = "r0";
        try {
            PackageInfo pi = pm.getPackageInfo(
                    "com.tacticalnuclearstrike.tttumblr", 0);
            version = pi.versionName;
        } catch (NameNotFoundException e) {

        }
        return version;
    }

    public void CheckIsUserNameAndPasswordCorrect() {
        TextView infoView = (TextView) findViewById(R.id.labelAuthStatus);

        if (!api.isUserNameAndPasswordStored()) {
            infoView
                    .setText("Please press menu and select Account to enter email and password.");
            infoView.setVisibility(View.VISIBLE);
        } else {
            infoView.setVisibility(View.GONE);
        }
    }

    private void startDashboardActivity() {
        startActivity(new Intent(MainActivity.this,
                Dashboard.class));
    }
}
