package com.megha.tatti.knowyourgovernment;

import java.io.Serializable;


public class MyGovernment implements Serializable{
    private String officeName;
    private int officeIndex;
    private PersonalDetails official;
    Address localAddress;

    public MyGovernment(String officeName, int officeIndex, PersonalDetails official) {
        this.officeName = officeName;
        this.officeIndex = officeIndex;
        this.official = official;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    public int getOfficeIndex() {
        return officeIndex;
    }

    public void setOfficeIndex(int officeIndex) {
        this.officeIndex = officeIndex;
    }

    public PersonalDetails getOfficial() {
        return official;
    }

    public void setOfficial(PersonalDetails official) {
        this.official = official;
    }

    public Address getLocalAddress() {
        return localAddress;
    }

    public void setLocalAddress(Address localAddress) {
        this.localAddress = localAddress;
    }

}
