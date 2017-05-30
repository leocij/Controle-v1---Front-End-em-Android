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
 * Created by leoci on 30/05/2017.
 */

class GetAsyncTask extends AsyncTask<String,String,String> {
    private int codeResponse;
    private StringBuffer buffer;

    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection httpURLConnection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(params[0]);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Cookie", "JSESSIONID=" + params[2]);
            int codeResponse = httpURLConnection.getResponseCode();
            setCodeResponse(codeResponse);
            if (codeResponse != 200) {
                return "http";
            } else {
                InputStream inputStream = httpURLConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String linha;
                StringBuffer buffer = new StringBuffer();
                while ((linha = reader.readLine()) != null) {
                    buffer.append(linha);
                    buffer.append("\n");
                }
                setBuffer(buffer);
                return "buffer";
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            if(httpURLConnection != null){
                httpURLConnection.disconnect();
            }
            if(reader != null){
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
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

    public void setBuffer(StringBuffer buffer) {
        this.buffer = buffer;
    }

    public int getCodeResponse() {
        return codeResponse;
    }

    public StringBuffer getBuffer() {
        return buffer;
    }
}
