package com.andresparra.musicplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

public class TrackListAdapter  extends BaseAdapter {
    private Context context = null;
    private List<Track> tracks = null;

    public TrackListAdapter(Context newContext, List<Track> newTracks){
        context = newContext;
        tracks = newTracks;
    }


    @Override
    public int getCount() {
        return tracks.size();
    }

    @Override
    public Track getItem(int i) {
        return tracks.get(i);

    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        final int ROW_RESOURCE = R.layout.row_item;
        ViewHolder viewHolder = null;

        if (convertView == null){
            LayoutInflater layout = LayoutInflater.from(context);
            convertView = layout.inflate(ROW_RESOURCE, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Track currentTrack = getItem(pos);

        Track track = tracks.get(pos);

        String artworkUrl = currentTrack.getArtwork();
        viewHolder.imgPhoto.setImageBitmap(StringToBitMap(currentTrack.getArtwork()));
        viewHolder.txtTrackName.setText(track.getTrackName());
        viewHolder.txtArtistName.setText(track.getArtistName());
        String destFilename = context.getCacheDir() + "/" + currentTrack.getTrackId() + ".m4a";

        if (!new File(destFilename).exists()) {
            viewHolder.imgAction.setImageDrawable(context.getDrawable(R.drawable.download));
            //currentSong.setState(1);
        } else {
            viewHolder.imgAction.setImageDrawable(context.getDrawable(R.drawable.play));
            //currentSong.setState(2);
        }


        return convertView;
    }

    public Bitmap StringToBitMap(String encodedString){
        try{
            byte [] encodeByte = Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }
        catch(Exception e){
            e.getMessage();
            return null;
        }
    }

    public static class ViewHolder{
        ImageView imgPhoto = null;
        TextView txtTrackName = null;
        TextView txtArtistName = null;
        public ImageView imgAction;

        public ViewHolder(View view){
            imgPhoto = view.findViewById(R.id.imgPhoto);
            txtTrackName = view.findViewById(R.id.txtTrackName);
            txtArtistName = view.findViewById(R.id.txtArtistName);
            imgAction = view.findViewById(R.id.imgAction);
        }
    }


}
