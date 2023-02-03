package com.megha.tatti.knowyourgovernment;

import java.io.Serializable;
import java.util.List;


public class PersonalDetails implements Serializable{
    private String name;
    private Address address;
    private String partyName;
    private List<String> phoneNumbers;
    private List<String> emailIds;
    private List<String> urls;
    private String photoUrl;
    private Web_Channel webChannel;

    public PersonalDetails(String name, Address address, String partyName, List<String> phoneNumbers, List<String> emailIds, List<String> urls, String photoUrl, Web_Channel webChannel) {
        this.name = name;
        this.address = address;
        this.partyName = partyName;
        this.phoneNumbers = phoneNumbers;
        this.emailIds = emailIds;
        this.urls = urls;
        this.photoUrl = photoUrl;
        this.webChannel = webChannel;
    }

    public List<String> getEmailIds() {
        return emailIds;
    }

    public void setEmailIds(List<String> emailIds) {
        this.emailIds = emailIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getPartyName() {
        return partyName;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    public List<String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(List<String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Web_Channel getWebChannel() {
        return webChannel;
    }

    public void setWebChannel(Web_Channel webChannel) {
        this.webChannel = webChannel;
    }

}
