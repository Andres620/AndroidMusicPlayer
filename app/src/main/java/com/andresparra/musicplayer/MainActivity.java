package com.andresparra.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;

import java.net.URL;

import cafsoft.foundation.HTTPURLResponse;
import cafsoft.foundation.URLComponents;
import cafsoft.foundation.URLQueryItem;
import cafsoft.foundation.URLSession;

public class MainActivity extends AppCompatActivity {

    private String term;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchTerm("Linkin park");
    }

    public void searchTerm(String term){
        URLComponents comp = new URLComponents();

        comp.setScheme("https");
        comp.setHost("itunes.apple.com");
        comp.setPath("/search");
        comp.setQueryItems(new URLQueryItem[]{
                new URLQueryItem("media", "music"),
                new URLQueryItem("entity", "song"),
                new URLQueryItem("term", term)
        });
        Log.d("url", comp.getURL().toString());

        URL url = comp.getURL();

        URLSession.getShared().dataTask(url, (data, response, error) -> {
            //cuando ejecua este bloque de error es porque recibio una respuesta
            //ya sea el response(puede ser un status 200 - o errores de respuestastatus 400)
            // o un error(error de red - No Internet)
            HTTPURLResponse resp = (HTTPURLResponse) response;
            if (error != null){
                Log.d("resp", "sonido");
                if(resp.getStatusCode()==200){
                    Log.d("resp", data.toText());
                    Gson gson = new Gson();
                    Log.d("Gson", gson.toString());
                }else{
                    Log.d("Message ", "Server error " + resp.getStatusCode());
                }
            }else{
                Log.d("Message", "Network error");
            }
        }).resume(); //con el resume se lanza la tarea en un hilo de ejecución
    }

}