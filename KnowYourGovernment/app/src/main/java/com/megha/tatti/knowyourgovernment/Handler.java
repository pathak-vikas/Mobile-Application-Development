package com.megha.tatti.knowyourgovernment;

import android.content.Context;
import android.util.JsonReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Handler {
    private Map<Integer,PersonalDetails> officialMap;
    private List<MyGovernment> officialList;
    private Context context;

    public Handler(Context context) {
        this.context = context;
    }

    public List<MyGovernment> readData(InputStream in) throws IOException {
        JsonReader jsonReader1 = new JsonReader(new InputStreamReader(in, "UTF-8"));
        JsonReader jsonReader2 = new JsonReader(new InputStreamReader(in, "UTF-8"));
        try {
            readOfficialObject(jsonReader2);
        } finally {
            jsonReader2.close();
        }
        in.reset();
        try {
            officialList = getOfficialList(jsonReader1);
        } finally {
            jsonReader1.close();
        }
        return officialList;
    }

    private List<MyGovernment> getOfficialList(JsonReader jsonReader) throws IOException {
        officialList = new ArrayList<>();
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            if (jsonReader.nextName().equals("offices")) {
                readOfficialList(jsonReader);
            }else{
                jsonReader.skipValue();
            }
        }
        jsonReader.endObject();
        return officialList;
    }

    private void readOfficialList(JsonReader jsonReader) throws IOException {
        jsonReader.beginArray();
        while (jsonReader.hasNext()) {
                officialObjectList(jsonReader);
        }
        jsonReader.endArray();
    }

    private void officialObjectList(JsonReader jsonReader) throws IOException {
        String officeName="";
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            if (name.equals("name")) {
                officeName = jsonReader.nextString();
            }else if (name.equals("officialIndices")) {
                readOfficialIndex(jsonReader,officeName);
            }else{
                jsonReader.skipValue();
            }
        }

        jsonReader.endObject();
    }

    private void readOfficialIndex(JsonReader jsonReader, String name) throws IOException {
        jsonReader.beginArray();
        while (jsonReader.hasNext()) {
            int index = jsonReader.nextInt();
            PersonalDetails official = officialMap.get(index);
            officialList.add(new MyGovernment(name,index,official));
        }
        jsonReader.endArray();
    }

    private void readOfficialObject(JsonReader jsonReader) throws IOException {
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            if (name.equals("officials")) {
                readOfficialsArray(jsonReader);
            }else{
                jsonReader.skipValue();
            }
        }
        jsonReader.endObject();
    }

    private void readOfficialsArray(JsonReader jsonReader) throws IOException {
        int count = 0;
        officialMap = new HashMap<>();
        jsonReader.beginArray();
        while (jsonReader.hasNext()) {
            PersonalDetails official = readObject(jsonReader);
            officialMap.put(count++,official);
        }
        jsonReader.endArray();
    }

    private PersonalDetails readObject(JsonReader jsonReader) throws IOException {
         String officialName=null;
         Address address=null;
         String partyName=null;
         List<String> phoneNumberList=null;
         List<String> emailList= null;
         List<String> urlList=null;
         String photoUrl=null;
         Web_Channel webChannel =null;
         PersonalDetails details;
        jsonReader.beginObject();
        while (jsonReader.hasNext()) {
            String name = jsonReader.nextName();
            if (name.equals("name")) {
                officialName = jsonReader.nextString();
            }else if (name.equals("address")) {
              address =  readOfficialAddressArray(jsonReader);
            }else if (name.equals("party")) {
                partyName=jsonReader.nextString();
            }else if (name.equals("phones")) {
               phoneNumberList =  PhoneNoList(jsonReader);
            }else if (name.equals("emails")) {
              emailList=  readEmailList(jsonReader);
            }else if (name.equals("urls")) {
              urlList =  readURLList(jsonReader);
            }else if (name.equals("photoUrl")) {
               photoUrl = jsonReader.nextString();
            }else if (name.equals("channels")) {
                webChannel = new Web_Channel();
                ChannelArray(jsonReader, webChannel);
            }else{
                jsonReader.skipValue();
            }
        }
        jsonReader.endObject();
        details = new PersonalDetails(officialName,address,partyName,phoneNumberList,emailList,urlList,photoUrl, webChannel);
        return details;
    }


    private Address readOfficialAddressArray(JsonReader reader) throws IOException {
        Address address=null;
        reader.beginArray();
        while (reader.hasNext()) {
           address= readObjData(reader);
        }
        reader.endArray();
        return address;
    }

    private Address readObjData(JsonReader reader) throws IOException {
        String line1=null;
        String line2=null;
        String line3=null;
        String city=null;
        String state=null;
        String zip=null;
        Address address;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("line1")) {
                line1 = reader.nextString();
            }else if (name.equals("line2")) {
                line2 = reader.nextString();
            }else if (name.equals("line3")) {
                line3 = reader.nextString();
            }else if (name.equals("city")) {
                city = reader.nextString();
            }else if (name.equals("state")) {
                state = reader.nextString();
            }else if (name.equals("zip")) {
                zip = reader.nextString();
            }else{
                reader.skipValue();
            }
        }
        reader.endObject();
        address=new Address(line1,line2,line3,city,state,zip);
        return address;
    }

    private List<String> PhoneNoList(JsonReader reader) throws IOException {
        List<String> phNumList= new ArrayList<>();
        reader.beginArray();
        while (reader.hasNext()) {
            String phoneNum = reader.nextString();
            phNumList.add(phoneNum);
        }
        reader.endArray();
        return phNumList;
    }
    private List<String> readEmailList(JsonReader reader) throws IOException {
        List<String> emailList= new ArrayList<>();
        reader.beginArray();
        while (reader.hasNext()) {
            String email = reader.nextString();
            emailList.add(email);
        }
        reader.endArray();
        return emailList;
    }
    private List<String> readURLList(JsonReader reader) throws IOException {
        List<String> urlList= new ArrayList<>();
        reader.beginArray();
        while (reader.hasNext()) {
            String url = reader.nextString();
            urlList.add(url);
        }
        reader.endArray();
        return urlList;
    }

    private void ChannelArray(JsonReader reader, Web_Channel webChannel)throws IOException {
        reader.beginArray();
        while (reader.hasNext()) {
            readChannelObjectData(reader, webChannel);
        }
        reader.endArray();
    }

    private void readChannelObjectData(JsonReader reader, Web_Channel webChannel)throws IOException {
        String channelName="";
        String channelID="";
        reader.beginObject();
        while (reader.hasNext()){
            String name = reader.nextName();
            if (name.equals("type")) {
                channelName = reader.nextString();
            }else if (name.equals("id")) {
                channelID = reader.nextString();
            }else{
                reader.skipValue();
            }
        }
        if(channelName.equalsIgnoreCase("GooglePlus")){
            webChannel.setGoogleID(channelID);
        }else if(channelName.equalsIgnoreCase("Facebook")){
            webChannel.setFacebookID(channelID);
        }else if(channelName.equalsIgnoreCase("Twitter")){
            webChannel.setTwitterID(channelID);
        }else if(channelName.equalsIgnoreCase("YouTube")){
            webChannel.setYoutubeID(channelID);
        }
        reader.endObject();
    }


    public Address readAddress(InputStream inputStream) throws IOException {
        Address address;
        JsonReader jsonReader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
        try {
          address = readNormalizedInput(jsonReader);
        } finally {
            jsonReader.close();
        }
        return address;
    }

    private Address readNormalizedInput(JsonReader reader)throws IOException {
        Address address=null;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("normalizedInput")) {
              address =  readInputAddress(reader);
            }else{
                reader.skipValue();
            }
        }
        reader.endObject();
        return address;
    }

    private Address readInputAddress(JsonReader reader) throws IOException {
        String line1=null;
        String city=null;
        String state=null;
        String zip=null;
        Address address;
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("line1")) {
                line1 = reader.nextString();
            }else if (name.equals("city")) {
                city = reader.nextString();
            }else if (name.equals("state")) {
                state = reader.nextString();
            }else if (name.equals("zip")) {
                zip = reader.nextString();
            }else{
                reader.skipValue();
            }
        }
        reader.endObject();
        address=new Address(line1,null,null,city,state,zip);
        return address;
    }

}
