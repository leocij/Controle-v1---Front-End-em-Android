package com.lemelo.controlev1;

import android.os.AsyncTask;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/*
 * Created by leoci on 30/05/2017.
 */

class PutAsyncTask extends AsyncTask<String,String,String> {
    private int codeResponse;

    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection httpURLConnection = null;

        try {
            URL url = new URL(params[0]);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("PUT");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Cookie", "JSESSIONID=" + params[2]);
            DataOutputStream wr = new DataOutputStream(httpURLConnection.getOutputStream());
            wr.writeBytes(params[1]);
            wr.flush();
            wr.close();
            int codeResponse = httpURLConnection.getResponseCode();
            setCodeResponse(codeResponse);
            if (codeResponse < 200 || codeResponse > 206) {
                return "http";
            } else {
                return "sucess";
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
        return null;
    }

    public void setCodeResponse(int codeResponse) {
        this.codeResponse = codeResponse;
    }

    public int getCodeResponse() {
        return codeResponse;
    }
}
