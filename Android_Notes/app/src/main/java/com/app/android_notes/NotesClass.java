package com.app.android_notes;

import android.util.JsonWriter;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;

public class NotesClass implements Serializable {
    public String strtitle;
    public String savetimelast;
    public String strcontent;



    public NotesClass(){}


    public NotesClass(String strtitle, String savetimelast, String strcontent) {
        this.strtitle = strtitle;
        this.savetimelast = savetimelast;
        this.strcontent = strcontent;
    }

    public String getStrtitle() {
        return strtitle;
    }

    public void setStrtitle(String strtitle) {
        this.strtitle = strtitle;
    }

    public String getSavetimelast() {
        return savetimelast;
    }

    public void setSavetimelast(String savetimelast) {
        this.savetimelast = savetimelast;
    }

    public String getStrcontent() {
        return strcontent;
    }

    public void setStrcontent(String strcontent) {
        this.strcontent = strcontent;
    }

    @NonNull
    public String toString() {

        try {
            StringWriter sw = new StringWriter();
            JsonWriter jsonWriter = new JsonWriter(sw);
            jsonWriter.setIndent("  ");
            jsonWriter.beginObject();
            jsonWriter.name("strtitle").value(getStrtitle());
            jsonWriter.name("savetimelast").value(getSavetimelast());
            jsonWriter.name("strcontent").value(getStrcontent());
            jsonWriter.endObject();
            jsonWriter.close();
            return sw.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }


}
