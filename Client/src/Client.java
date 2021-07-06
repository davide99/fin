import com.google.gson.Gson;
import okhttp3.*;
import org.recognition.fingerprint.Fingerprint;
import org.recognition.fingerprint.Links;
import org.recognition.io.MicReader;
import org.recognition.io.WavReader;
import org.recognition.model.Song;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Client {
    private Client(float[] data) {
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
                    .url("http://localhost:8080/search")
                    .post(formBody)
                    .build();
            Response response = client.newCall(request).execute();

            Song song = gson.fromJson(response.body().string(),Song.class);
            System.out.println(song);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static void main(String[] args) throws InterruptedException {
        /*MicReader mic = new MicReader(5);
        mic.start();
        mic.join();
        new Client(mic.getData());*/

        WavReader wav = new WavReader("/home/davide/Scrivania/out.wav");
        new Client(wav.getData());
    }
}