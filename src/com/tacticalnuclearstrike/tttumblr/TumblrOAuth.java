package com.tacticalnuclearstrike.tttumblr;

import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

import java.io.UnsupportedEncodingException;

/**
 * User: Fredrik / 2011-09-21
 */
public class TumblrOAuth {

    private OAuthConsumer mConsumer;
    private OAuthProvider mProvider;

    private String mCallbackUrl;

    public TumblrOAuth(String consumerKey, String consumerSecret,
                       String callbackUrl)
            throws UnsupportedEncodingException {
        mConsumer = new CommonsHttpOAuthConsumer(consumerKey, consumerSecret);
        mProvider = new CommonsHttpOAuthProvider("http://www.tumblr.com/oauth/request_token",
                "http://www.tumblr.com/oauth/access_token",
                "http://www.tumblr.com/oauth/authorize");

        mProvider.setOAuth10a(true);
        mCallbackUrl = (callbackUrl == null ? OAuth.OUT_OF_BAND : callbackUrl);
    }

    public String[] getAccessToken(String verifier)
            throws OAuthMessageSignerException, OAuthNotAuthorizedException,
            OAuthExpectationFailedException, OAuthCommunicationException {
        mProvider.retrieveAccessToken(mConsumer, verifier);
        return new String[]{
                mConsumer.getToken(), mConsumer.getTokenSecret()
        };
    }

    public String getRequestToken()
            throws OAuthMessageSignerException, OAuthNotAuthorizedException,
            OAuthExpectationFailedException, OAuthCommunicationException {
        String authUrl = mProvider.retrieveRequestToken(mConsumer, mCallbackUrl);
        return authUrl;
    }
}
