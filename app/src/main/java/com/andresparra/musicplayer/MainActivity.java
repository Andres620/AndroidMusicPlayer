package com.andresparra.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
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
                buildURLTrack(pos, viewHolder);

            }
        });

    }

    public void getInfo(){
        URL url = null;
        final String name = songFind.getText().toString();

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

                    if(root.getResults().size() > 0){
                        showInfo(root.getResults());
                        tracks.clear();
                    }

                    else {
                        System.out.println("Sin datos");
                    }
                }
            }
        }).resume();
    }

    public void showInfo(ArrayList<Result> results) {
        runOnUiThread(() -> {

            //String urlImage = "";

            for (Result result : results) {

                tracks.add(new Track(result.getTrackName(), result.getArtistName(), result.getTrackId(), result.getPreviewUrl(), result.getArtworkUrl100()));

                // urlImage = result.getArtworkUrl30();

                //getImage(urlImage);

            }
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



    public void buildURLTrack(int position, TrackListAdapter.ViewHolder viewHolder)
    {
        Track track = tracks.get(position);

        String fullFilename = this.getCacheDir() + "/" + track.getTrackId() + ".tmp.m4a";

        try {
            url = new URL(track.getPreviewUrl());
        } catch (MalformedURLException e) {
            //e.printStackTrace();
        }

        //new File(fullFilename).delete(); // borrar para descargar nuevamente



        if (new File(fullFilename).exists()) {
            // Ya fue descargado anteriormente y se encuentra en cache
            if (mediaPlayer == null) {
                viewHolder.imgAction.setImageDrawable(getDrawable(R.drawable.stop));
                playTrack(fullFilename);
            }else{
                viewHolder.imgAction.setImageDrawable(getDrawable(R.drawable.play));
                stopTrack();
            }
        } else {
            viewHolder.imgAction.setImageDrawable(getDrawable(R.drawable.download));
            downloadFile(url, fullFilename);
        }
        //return url;
    }

    public void downloadFile(URL audioURL, String fullFilename){
        URLSession.getShared().downloadTask(audioURL, (localAudioUrl, response, error) -> {

            if (error == null) {
                int respCode = ((HTTPURLResponse) response).getStatusCode();
                if (respCode == 200) {
                    File file = new File(localAudioUrl.getFile());
                    if (file.renameTo(new File(fullFilename))) {
                        playTrack(fullFilename);
                    }
                }
            }
        }).resume();
    }

    public void playTrack(String fullFilename) {
        stopTrack();
        mediaPlayer = MediaPlayer.create(this, Uri.parse(fullFilename));
        mediaPlayer.start();
    }

    public void stopTrack() {
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}