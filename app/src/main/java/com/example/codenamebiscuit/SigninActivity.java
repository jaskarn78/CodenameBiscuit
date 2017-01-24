package com.example.codenamebiscuit;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by Tommy on 1/23/17.
 */

public class SigninActivity extends AsyncTask<String, Void, String> {
    private int get_post_flag;  // 0 = GET, 1 = POST

    public SigninActivity (int flag) { get_post_flag = flag;}

    @Override
    protected String doInBackground(String... strings) {
        String link = strings[0];
        String identifier = strings[1];
        String userId = strings[2];

        if (!userId.equals("")) {
            if (get_post_flag == 0) {

            } else {
                try {
                    String data = URLEncoder.encode(identifier, "UTF-8")
                            + "=" + URLEncoder.encode(userId, "UTF-8");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write( data );
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new
                            InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while((line = reader.readLine()) != null) {
                        Log.v("PrintLine", line);
                        sb.append(line);
                    }

                    JSONArray jsonArray = new JSONArray(sb.toString());
                    JSONObject jsonObject = new JSONObject(jsonArray.getString(1));

                    return jsonObject.getString("user_name");
                    //JSONArray jArray = new jObj.getJSONArray("");

                } catch (MalformedURLException e) {
                    Log.e("MalformedURL", e.toString());
                } catch (UnsupportedEncodingException e) {
                    Log.e("UnsupportedEncoding", e.toString());
                } catch (IOException e) {
                    Log.e("IOException", e.toString());
                } catch (JSONException e) {
                    Log.e("JSONException", e.toString());
                }
            }
        } else {
            Log.e("FacebookEmailRequest", "Could not pull user email from Facebook");
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null)
            Log.v("SUCCESS", result);
    }
}
