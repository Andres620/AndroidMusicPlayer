package com.andresparra.musicplayer;

public class Track {
    private String trackName = "";
    private String artistName = "";
    private String trackId = "";
    private String previewUrl= "";
    private String artwork = "";
    private int state;

    public Track(String newTrackName, String newArtistName, String newTrackId,String newPreviewUrl, String newArtwork){
        trackName = newTrackName;
        artistName = newArtistName;
        setTrackId(newTrackId);
        setPreviewUrl(newPreviewUrl);
        setArtwork(newArtwork);
    }


    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }





    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getArtwork() {
        return artwork;
    }

    public void setArtwork(String artwork) {
        this.artwork = artwork;
    }
}
