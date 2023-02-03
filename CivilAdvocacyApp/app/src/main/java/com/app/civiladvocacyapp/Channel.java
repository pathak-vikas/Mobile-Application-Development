package com.app.civiladvocacyapp;

import java.io.Serializable;

public class Channel implements Serializable {
    private String googlePlusId;
    private String facebookId;
    private String twitterId;
    private String youtubeId;

    public String getGooglePlusId() {
        return googlePlusId;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public String getTwitterId() {
        return twitterId;
    }

    public String getYoutubeId() {
        return youtubeId;
    }

    public void setGooglePlusId(String googlePlusId) {
        this.googlePlusId = googlePlusId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public void setTwitterId(String twitterId) {
        this.twitterId = twitterId;
    }

    public void setYoutubeId(String youtubeId) {
        this.youtubeId = youtubeId;
    }
}