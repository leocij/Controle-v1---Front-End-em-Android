package com.lemelo.controlev1;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Leoci Melo 26/05/2017.
 */

class UserPostAsync extends AsyncTask<String,Integer,String> {
    private int codeResponse;
    private ProgressDialog dialog;

    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection httpURLConnection;

        try {
            httpURLConnection = (HttpURLConnection) new URL(params[0]).openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setFixedLengthStreamingMode(params[1].getBytes().length);

            httpURLConnection.setConnectTimeout(5000);

            DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
            wr.writeBytes(params[1]);
            wr.flush();
            wr.close();

            setCodeResponse(httpURLConnection.getResponseCode());

            System.out.println("Passei aqui: " + httpURLConnection.getHeaderFields());

            if (getCodeResponse() != 200) {
                return String.valueOf("http " + getCodeResponse());
            } else {
                return String.valueOf("cookie ") + httpURLConnection.getHeaderField("Set-Cookie");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.show();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        dialog.dismiss();
    }

    private int getCodeResponse() {
        return codeResponse;
    }

    private void setCodeResponse(int codeResponse) {
        this.codeResponse = codeResponse;
    }

    void setDialog(ProgressDialog dialog) {
        this.dialog = dialog;
    }
}
