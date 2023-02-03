package com.cs442.assignment5;

import java.io.Serializable;

public class Article implements Serializable {

    private String author="";
    private String title="";
    private String  description="";
    private String url="";
    private String photo="";
    private String date;

    public Article(String date) {
        this.date = date;
    }

    public Article(String author, String title, String description, String url, String photo, String date) {
        this.author = author;
        this.title = title;
        this.description = description;
        this.url = url;
        this.photo = photo;
        this.date = date;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Article{" +
                "author='" + author + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                ", photo='" + photo + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
