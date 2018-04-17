package com.jazart.symphony;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

//import java.net.URI;

/**
 * Created by kendrickgholston on 4/17/18.
 */

public class FindURI extends Fragment implements View.OnClickListener {
    protected Uri mURI;
    private static final int URI_REQUEST = 1;
    private boolean mOver;

    public FindURI() {

    }



    public Uri getmURI() {

        return mURI;
    }

    public void setmURI(){
        Intent musicIntent = new Intent();
        musicIntent.setAction(Intent.ACTION_GET_CONTENT);
        musicIntent.setType("audio/mp3");
        startActivityForResult(Intent.createChooser(
                musicIntent, "Open Audio (mp3) file"),URI_REQUEST);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK){
            //Log.d("DEBUG","RESULT OK");
            if (requestCode == 1){
                //Log.d("DEBUG","REQUEST OK");
                mURI= data.getData();
                //Log.d("DEBUG", mURI.toString());
                //Log.d("DEBUG",mURI.toString());
                //MediaMetadataRetriever musicInfoRetr = new MediaMetadataRetriever();
                //musicInfoRetr.setDataSource(uriSound.toString());

            }
        }
    }

    @Override
    public void onClick(View view) {
        setmURI();

    }
}
