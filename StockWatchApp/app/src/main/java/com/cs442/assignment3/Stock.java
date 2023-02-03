package com.cs442.assignment3;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Stock implements Serializable,Comparable<Stock> {

    private String symbol;
    private String companyName;
    private Double latestPrice;
    private Double change;
    private Double changePercent;

    public Stock() {
    }

    public Stock(String symbol, String companyName, Double latestPrice, Double change, Double changePercent) {
        this.symbol = symbol;
        this.companyName = companyName;
        this.latestPrice = latestPrice;
        this.change = change;
        this.changePercent = changePercent;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Double getLatestPrice() {
        return latestPrice;
    }

    public void setLatestPrice(Double latestPrice) {
        this.latestPrice = latestPrice;
    }

    public Double getChange() {
        return change;
    }

    public void setChange(Double change) {
        this.change = change;
    }

    public Double getChangePercent() {
        return changePercent;
    }

    public void setChangePercent(Double changePercent) {
        this.changePercent = changePercent;
    }

    @Override
    public String toString() {
        return "Stock{" +
                "symbol='" + symbol + '\'' +
                ", companyName='" + companyName + '\'' +
                ", latestPrice=" + latestPrice +
                ", change=" + change +
                ", changePercent=" + changePercent +
                '}';
    }

    @Override
    public int compareTo(@NonNull Stock o) {
        return getSymbol().compareTo(o.getSymbol());
    }
}
