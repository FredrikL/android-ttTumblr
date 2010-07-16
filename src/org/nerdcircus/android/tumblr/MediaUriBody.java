package org.nerdcircus.android.tumblr;

import org.apache.http.entity.mime.content.InputStreamBody;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.net.Uri;
import android.database.Cursor;
import android.provider.MediaStore.MediaColumns;

public class MediaUriBody extends InputStreamBody {

    private Uri mUri;
    private Cursor mCursor;

    private long mSize;
    private String mFilename;
    private String mType;
    private InputStream mStream;

    /* MIME body part for items in the MediaStore content provider.
     */
    public MediaUriBody(Context ctx, Uri media_uri, InputStream is, String filename){
        super(is, filename);

        mUri = media_uri;
        mFilename = filename;
        mStream = is;
        //TODO: open the uri, get the data we need, and do stuff with it.
        Cursor c = ctx.getContentResolver().query(media_uri, 
            new String[] { MediaColumns.DATA, MediaColumns.DISPLAY_NAME, MediaColumns.MIME_TYPE, MediaColumns.SIZE },
            null,
            null,
            null
        );
        c.moveToFirst();
        mSize = c.getLong(c.getColumnIndex(MediaColumns.SIZE));
        mType = c.getString(c.getColumnIndex(MediaColumns.MIME_TYPE));
        //mFilename = c.getString(c.getColumnIndex(MediaColumns.DISPLAY_NAME));
        //mStream = ctx.getContentResolver().openInputStream(media_uri);

        c.close();
    }

    public long getContentLength() {
        return mSize;
    }

    public String getFilename() {
        return mFilename;
    }

    public InputStream getInputStream() {
        return mStream;
    }

}
