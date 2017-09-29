package com.udayaproject.pat;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by DETECTIVE7 on 29-03-2016.
 */
public class GetDrives {

    AsyncTask<Void, Void, Void> mGetData;
    Context mContext;

    GetDrives(Context context) {
        mContext = context;
    }

    public void getDriveData(final String college) {
        mGetData = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                // Register on our server
                // On server creates a new user

                Log.d("CHECK_COLLEGE", college);
                Drive.getJSON(mContext, college);
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                mGetData = null;
            }
        };
        mGetData.execute(null, null, null);
    }
}
