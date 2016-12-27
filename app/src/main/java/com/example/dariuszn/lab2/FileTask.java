package com.example.dariuszn.lab2;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by DariuszN on 01.12.2016.
 */

public class FileTask extends AsyncTask<Integer, Integer, Integer> {

    String url;
    TextView sizeView;
    TextView typeView;
    HttpURLConnection httpURLConnection;

    int size;
    String type;

    public FileTask(String url, TextView sizeView, TextView typeView) {
        this.url = url;
        this.sizeView = sizeView;
        this.typeView = typeView;
    }

    @Override
    protected Integer doInBackground(Integer... integers) {
        getFileInformation();
        Log.e("Info","Jestem w tasku");
        return 42;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);

        sizeView.setText(Integer.toString(size));
        typeView.setText(type);
    }

    private void getFileInformation() {
        try {
            URL url = new URL(this.url);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoOutput(true);
            size = httpURLConnection.getContentLength();
            type = httpURLConnection.getContentType();
            Log.e("SizeFile", Integer.toString(size));
            //Log.e("TypeFile", type.toString());
        }
        catch (Exception e) {
            Log.e("Error Info", "exception", e);
        }
    }

}
