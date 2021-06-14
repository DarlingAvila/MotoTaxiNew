package com.darling.mototaxi.retrofi;

import com.darling.mototaxi.models.FCMBody;
import com.darling.mototaxi.models.FCMResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMApi {

    //nos permite enviar notifiaciones de dispositivo a dispositivo
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAALZEJVks:APA91bGL7fOH2MgtjF-pYqlsXl3qKtfm1g7UZgaTFHSUSafnrl6rBNmMSDD3s1ZgSuwjK80Wu6UvITGCBlT2zYZNy-tiT98NkWNpvZIIpIDw1_0rjwyTDlqYXXY-AqwV2AmL0XDtu6KY"
    })
    @POST("fcm/send")
    Call<FCMResponse> send(@Body FCMBody body);
}
