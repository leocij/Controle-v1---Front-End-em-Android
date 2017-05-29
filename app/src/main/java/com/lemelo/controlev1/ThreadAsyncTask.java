package com.lemelo.controlev1;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/*
 * Created by leoci on 27/05/2017.
 */

public class ThreadAsyncTask extends AsyncTask<String,String,String> {
    private String method;
    private StringBuffer buffer;
    private int codeResponse;

    @Override
    protected String doInBackground(String... params) {

        HttpURLConnection httpURLConnection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(params[0]);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod(getMethod());
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Cookie", "JSESSIONID=" + params[2]);
            httpURLConnection.setDoInput(true);

            int codeResponse = httpURLConnection.getResponseCode();

            if (codeResponse == 200) {
                if (getMethod().equals("GET")) {

                    InputStream inputStream = httpURLConnection.getInputStream();
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuffer buffer = new StringBuffer();
                    String linha;
                    while ((linha = reader.readLine()) != null) {
                        buffer.append(linha);
                        buffer.append("\n");
                    }
                    setBuffer(buffer);
                    return "buffer";
                }

            } else {
                setCodeResponse(codeResponse);
                return "http";
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }

    public void setBuffer(StringBuffer buffer) {
        this.buffer = buffer;
    }

    public void setCodeResponse(int codeResponse) {
        this.codeResponse = codeResponse;
    }

    public int getCodeResponse() {
        return codeResponse;
    }

    public StringBuffer getBuffer() {
        return buffer;
    }
}
