package com.andresparra.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.andresparra.musicplayer.services.term.model.Result;
import com.andresparra.musicplayer.services.term.model.Root;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cafsoft.foundation.HTTPURLResponse;
import cafsoft.foundation.URLComponents;
import cafsoft.foundation.URLQueryItem;
import cafsoft.foundation.URLRequest;
import cafsoft.foundation.URLSession;

public class MainActivity extends AppCompatActivity {

    /**
     * Importa datos de la API de iTunes
     */
    final String HOSTNAME = "https://itunes.apple.com/";
    final String SERVICE = "search?media=music&entity=song&term=";


    private ListView lveItems = null;
    private List<Track> tracks = null;

    URL url = null;

    private MediaPlayer mediaPlayer = null;

    Button button = null;
    EditText songFind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        tracks = new ArrayList<>();

        /*
        tracks.add(new Track("Track 1", "Artist 1"));
        tracks.add(new Track("Track 2", "Artist 1"));
        tracks.add(new Track("Track 3", "Artist 1"));
        tracks.add(new Track("Track 1", "Artist 2"));
        tracks.add(new Track("Track 4", "Artist 2"));
        tracks.add(new Track("Track 6", "Artist 2"));
        tracks.add(new Track("Track 11", "Artist 2"));
        tracks.add(new Track("Track 21", "Artist 2"));
        tracks.add(new Track("Track 5", "Artist 10"));
        tracks.add(new Track("Track 7", "Artist 10"));
        tracks.add(new Track("Track 21", "Artist 30"));
        tracks.add(new Track("Track 5", "Artist 40"));
        tracks.add(new Track("Track 7", "Artist 41"));

         */

        initViews();
        initEvents();

    }



    public void initViews(){
        lveItems = findViewById(R.id.lveItems);
        songFind = findViewById(R.id.txt_track);
        button = findViewById(R.id.button);


        TrackListAdapter adapter = new TrackListAdapter(this, tracks);

        lveItems.setAdapter(adapter);

    }

    public void initEvents(){

        button.setOnClickListener(m ->{
            getInfo();});

        lveItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {

                TrackListAdapter.ViewHolder viewHolder = new TrackListAdapter.ViewHolder(view);

                //viewHolder.txtTrackName.setText("ZZZZZZzzzzzzz");




                construirUrlPista(pos, viewHolder);

            }
        });

    }

    public void getInfo(){
        System.out.println("entra el clase");
        URL url = null;
        final String name = songFind.getText().toString(); //Una Aventura
        System.out.println("TTTTTTTTTTTT"+name);

        String urlSearch = HOSTNAME + SERVICE  +  name;
        //urlSearch = urlSearch.replace("http:","https:");

        //System.out.println(urlSearch);
        try {
            url = new URL(urlSearch);
            downloadData(url);
        }catch(MalformedURLException e){
        }

    }

    public void downloadData(URL url){
        URLRequest request = new URLRequest(url);
        URLSession.getShared().dataTask(request, (data, response, error)->{
            if (error == null){
                HTTPURLResponse resp = (HTTPURLResponse) response;
                if(resp.getStatusCode() == 200){
                    String text = data.toText();
                    System.out.println(text);
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson json = gsonBuilder.create();
                    Root root = json.fromJson(text, Root.class);

                    System.out.println("Linea 130: "+root.getResults().size());

                    if(root.getResults().size() > 0){
                        showInfo(root.getResults());
                        tracks.clear();
                    }

                    else {
                        System.out.println("Nada, nadilla");
                    }
                }
            }
        }).resume();
    }

    public void showInfo(ArrayList<Result> results) {
        runOnUiThread(() -> {

            //results.size();

            //String urlImage = "";

            for (Result result : results) {

                tracks.add(new Track(result.getTrackName(), result.getArtistName(), result.getTrackId(), result.getPreviewUrl(), result.getArtworkUrl30()));

                // urlImage = result.getArtworkUrl30();

                //getImage(urlImage);

            }


            //tracks.add(new Track(result.getTrackName(), result.getArtistName()));


            TrackListAdapter adapter = new TrackListAdapter(this, tracks);

            lveItems.setAdapter(adapter);



        });
    }

    public void getImage(String urlImage){
        URL url = null;
        urlImage = urlImage.replace("http:","https:");

        try{
            url = new URL(urlImage);
            //downloadImage(url);
        }catch(MalformedURLException e){}
    }

    /*
    public void downloadImage(URL url){
        //CustomListAdapter.ViewHolder viewHolder = new CustomListAdapter.ViewHolder(lveItems);
        URLRequest request = new URLRequest(url);
        URLSession.getShared().dataTask(request, (data, response, error)->{
            if (error == null){
                HTTPURLResponse resp = (HTTPURLResponse) response;
                if(resp.getStatusCode() == 200){
                    //Bitmap bitmap = BitmapFactory.decodeByteArray(data.toBytes() , 0, data.length());
                    showImage(dataToImage(data), img;
                }
            }
        }).resume();
    }

     */

    public Bitmap dataToImage(cafsoft.foundation.Data data) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(data.toBytes(), 0, data.length());
        return bitmap;
    }

    public void showImage(Bitmap image, ImageView imageView){
        runOnUiThread(() -> imageView.setImageBitmap(image));
    }



    public void construirUrlPista(int posicion, TrackListAdapter.ViewHolder viewHolder)
    {
        Track cancion = tracks.get(posicion);

        String destFilename = this.getCacheDir() + "/" + cancion.getTrackId() + ".tmp.m4a";

        try {
            url = new URL(cancion.getPreviewUrl());
        } catch (MalformedURLException e) {
            //e.printStackTrace();
        }

        //new File(destFilename).delete(); // borrar para descargar nuevamente



        if (!new File(destFilename).exists()) {
            viewHolder.imgAction.setImageDrawable(getDrawable(R.drawable.download));

            downloadFile(url, destFilename);

        } else {
            // Ya fue descargado anteriormente y se encuentra en cache
            viewHolder.imgAction.setImageDrawable(getDrawable(R.drawable.play));
            reproducirPista(destFilename);

        }



        //return url;
    }

    public void downloadFile(URL audioURL, String destFilename){
        URLSession.getShared().downloadTask(audioURL, (localAudioUrl, response, error) -> {

            if (error == null) {
                int respCode = ((HTTPURLResponse) response).getStatusCode();

                if (respCode == 200) {
                    File file = new File(localAudioUrl.getFile());
                    if (file.renameTo(new File(destFilename))) {
                        reproducirPista(destFilename);

                    }
                }
                else{
                    // Error (respCode)
                }
            }else {
                // Connection error
            }
        }).resume();
    }

    public void reproducirPista(String destFilename)
    {

        mediaPlayer = MediaPlayer.create(this, Uri.parse(destFilename));

        if (mediaPlayer.isPlaying())
        {
            mediaPlayer.stop();
        }

        else
        {
            mediaPlayer.start();
        }

        //mediaPlayer.reset();


        //System.out.println(mediaPlayer.getTrackInfo().toString());

    }
}