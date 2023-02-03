package com.megha.tatti.knowyourgovernment;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class Downloader extends AsyncTask<String, Void, Downloader.Result> {

    private Callback<String> mCallback;

    Downloader(Callback<String> callback) {
        setCallback(callback);
    }

    void setCallback(Callback<String> callback) {
        mCallback = callback;
    }


    static class Result {
        public String mResultValue;
        public Exception mException;
        public Result(String resultValue) {
            mResultValue = resultValue;
        }
        public Result(Exception exception) {
            mException = exception;
        }
    }


    @Override
    protected void onPreExecute() {
        if (mCallback != null) {
            NetworkInfo networkInfo = mCallback.getNetworkInfo();
            if (networkInfo == null || !networkInfo.isConnected() ||
                    (networkInfo.getType() != ConnectivityManager.TYPE_WIFI
                            && networkInfo.getType() != ConnectivityManager.TYPE_MOBILE)) {
                mCallback.update(null);
                cancel(true);
            }
        }
    }


    @Override
    protected Downloader.Result doInBackground(String... urls) {
        Result result = null;
        if (!isCancelled() && urls != null && urls.length > 0) {
            String urlString = urls[0];
            try {
                URL url = new URL(urlString);
                String resultString = downloadUrl(url);
                if (resultString != null) {
                    result = new Result(resultString);
                } else {
                    throw new IOException("No response received.");
                }
            } catch(Exception e) {
                result = new Result(e);
            }
        }
        return result;
    }


    @Override
    protected void onPostExecute(Result result) {
        if (result != null && mCallback != null) {
            if (result.mException != null) {
                mCallback.update(result.mException.getMessage());
            } else if (result.mResultValue != null) {
                mCallback.update(result.mResultValue);
            }
            //mCallback.finish();
        }
    }


    @Override
    protected void onCancelled(Result result) {
    }


    private String downloadUrl(URL url) throws IOException {
        InputStream stream = null;
        HttpURLConnection connection = null;
        String result = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();
            int responseCode = connection.getResponseCode();

            if (responseCode != HttpsURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }

                stream = connection.getInputStream();
                if (stream != null) {
                    result = readStream(stream, Integer.MAX_VALUE - 5);
                }

        }
        catch(Exception e){
        e.printStackTrace();

        }
        finally {

            if (stream != null) {
                stream.close();
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return result;
    }

    private synchronized String readStream(InputStream stream, int maxLength) throws IOException {
        String result = null;
        InputStreamReader reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[2147483];
        int numChars = 0;
        int readSize = 0;
        while (numChars < maxLength && readSize != -1) {
            numChars += readSize;
            int pct = (100 * numChars) / maxLength;
            readSize = reader.read(buffer, numChars, buffer.length - numChars);
        }
        if (numChars != -1) {
            numChars = Math.min(numChars, maxLength);
            result = new String(buffer, 0, numChars);
        }
        return result;
    }
}