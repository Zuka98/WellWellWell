package com.NHS.UCLTeam9.WellWellWell;


import android.util.Log;

import com.google.gson.Gson;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PostRequest implements Runnable {
    //    private AnonymousData dataToSend;
    private String postcode;
    private int score;
    private int errorRate;


    public PostRequest(String postcode, int score, int errorRate) {
        this.postcode = postcode;
        this.score = score;
        this.errorRate = errorRate;
    }

    @Override
    public void run() {
        try {
//            String url = "http://10.0.2.2:3000/androidquery";
          String url = "https://wellbeingdata.azurewebsites.net/androidquery";

            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            //add reuqest header
            con.setRequestMethod("POST");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            con.setRequestProperty("Content-type" , "application/json" );


//           AnonymousData dataToSend = new Datafeeder().getDatatoShare();
            AnonymousData dataToSend = new AnonymousData(postcode,score,errorRate);

            Gson gson = new Gson();
            String filetoSend = gson.toJson(dataToSend);
            Log.d("JSON:", filetoSend);

            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(filetoSend);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            con.disconnect();

        }
        catch (Exception e) {

            Log.d("1","Server Connection Error: " + e.getMessage());
        }
    }


}

