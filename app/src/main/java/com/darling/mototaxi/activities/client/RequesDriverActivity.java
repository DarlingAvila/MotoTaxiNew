package com.darling.mototaxi.activities.client;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.darling.mototaxi.R;
import com.darling.mototaxi.models.FCMBody;
import com.darling.mototaxi.models.FCMResponse;
import com.darling.mototaxi.providers.GeoFireProvider;
import com.darling.mototaxi.providers.NotificationProvider;
import com.darling.mototaxi.providers.TokenProvider;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequesDriverActivity extends AppCompatActivity {


    private LottieAnimationView mAnimation;
    private TextView mTextViewLookingFor;
    private Button mButtonCancelRequest;
    private GeoFireProvider mGeofireProvider;

    private double mExtraOriginLat;
    private double mExtraOriginLng;

    private LatLng mOriginLatLng;
    private LatLng mDestinationLatLng;

    private double mRadius = 0.1;
    private boolean mDriverFound = false;
    private String mIdDriverFound = "";
    private LatLng mDriverFoundLatLng;

    private NotificationProvider mNotificationProvider;
    private TokenProvider mTokenProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reques_driver);

        mAnimation = findViewById(R.id.animation);
        mTextViewLookingFor = findViewById(R.id.textViewLookingFor);
        mButtonCancelRequest = findViewById(R.id.btnCancelRequest);

        mAnimation.playAnimation();
        mExtraOriginLat = getIntent().getDoubleExtra("origin_lat", 0);
        mExtraOriginLng = getIntent().getDoubleExtra("origin_lng", 0);
        mOriginLatLng = new LatLng(mExtraOriginLat, mExtraOriginLng);
        mGeofireProvider = new GeoFireProvider();

        mNotificationProvider = new NotificationProvider();
        mTokenProvider = new TokenProvider();

        getClosestDriver();

    }
    private  void  getClosestDriver(){
        mGeofireProvider.getActiveDriver(mOriginLatLng, mRadius).addGeoQueryDataEventListener(new GeoQueryDataEventListener() {
            @Override
            public void onDataEntered(DataSnapshot dataSnapshot, GeoLocation location) {

                if (!mDriverFound) {
                    mDriverFound = true;
                    mIdDriverFound = String.valueOf(dataSnapshot);
                    mDriverFoundLatLng = new LatLng(location.latitude, location.longitude);
                    mTextViewLookingFor.setText("MOTOTAXI ENCONTRADO\nESPERANDO RESPUESTA");
                    sendNotification();
                    Log.d("Mototaxi", "ID: " + mIdDriverFound);
                }
            }

            @Override
            public void onDataExited(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onDataMoved(DataSnapshot dataSnapshot, GeoLocation location) {

            }

            @Override
            public void onDataChanged(DataSnapshot dataSnapshot, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

                //INGRESA AL TERMINO DE LA BUSQUEDA EN UN RADIO DE 0.1KM

                if (!mDriverFound) {
                    mRadius = mRadius + 0.1f;
                    // NO ENCONTRO NINGUN CONDUCTOR
                    if (mRadius > 5) {
                        mTextViewLookingFor.setText("NO SE ENCONTRO NINGUN MOTOTAXI");
                        Toast.makeText(RequesDriverActivity.this, "NO SE ENCONTRO UN CONDUCTOR", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else {
                        getClosestDriver();
                    }
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }

    private void sendNotification(){
    mTokenProvider.getToken(mIdDriverFound).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        //si surge problemas cammbiar de snapshot a datasnapshot
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            String token = dataSnapshot.child("token").getValue().toString();
            Map<String, String> map = new HashMap<>();
            map.put("title", "Solicitud de servicio");
            map.put("body","Un cliente esta solicitando un servicio");
            FCMBody fcmBody = new FCMBody(token, "high", map);
            mNotificationProvider.sendNotification(fcmBody).enqueue(new Callback<FCMResponse>() {
                @Override
                public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                    if (response.body() != null){
                        if (response.body().getSuccess()==1){
                            Toast.makeText(RequesDriverActivity.this, "La notificacion se ha enviado correctamente", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(RequesDriverActivity.this, "No se pudo enviar la notificacion", Toast.LENGTH_SHORT).show();

                        }

                    }
                }

                @Override
                public void onFailure(Call<FCMResponse> call, Throwable t) {

                       Log.d("Error", "Error "+ t.getMessage());
                }
            });
        }

        @Override
        public void onCancelled(@NonNull  DatabaseError error) {

        }
    });
    }

}