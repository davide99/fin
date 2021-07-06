package com.fingerprint.davide.fingerprint;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import org.recognition.fingerprint.Fingerprint;
import org.recognition.fingerprint.Links;
import org.recognition.io.MicReader;
import org.recognition.model.Song;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        try {
            MicReader mic = new MicReader(5);
            mic.start();
            mic.join();
            analize(mic.getData());
        } catch (Exception ignored) {
        }
    }

    private void analize(float[] data) {
        try {
            Gson gson = new Gson();
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(1, TimeUnit.MINUTES)
                    .readTimeout(1, TimeUnit.MINUTES)
                    .writeTimeout(1, TimeUnit.MINUTES)
                    .build();

            List<Links.Link> list = new Links(new Fingerprint(data).getPeakList());

            FormBody.Builder formBuilder = new FormBody.Builder()
                    .add("links", gson.toJson(list, list.getClass()));

            RequestBody formBody = formBuilder.build();

            Request request = new Request.Builder()
                    .url("http://" + ((EditText) findViewById(R.id.host)).getText() + ":8080/search")
                    .post(formBody)
                    .build();
            Response response = client.newCall(request).execute();

            Song song = gson.fromJson(response.body().string(), Song.class);
            System.out.println(song);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
