package com.megha.tatti.knowyourgovernment;

import android.net.NetworkInfo;

public interface  Callback<T> {

    void update(T result);
    NetworkInfo getNetworkInfo();
    void finish();
}
