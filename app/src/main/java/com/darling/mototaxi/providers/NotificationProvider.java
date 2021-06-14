package com.darling.mototaxi.providers;

import com.darling.mototaxi.models.FCMBody;
import com.darling.mototaxi.models.FCMResponse;
import com.darling.mototaxi.retrofi.IFCMApi;
import com.darling.mototaxi.retrofi.RetrofitClient;

import retrofit2.Call;

public class NotificationProvider {

    private String url = "https://fcm.googleapis.com";

    public NotificationProvider() {

    }
    public Call<FCMResponse> sendNotification(FCMBody body){
        return RetrofitClient.getClientObject(url).create(IFCMApi.class).send(body);
    }
}
