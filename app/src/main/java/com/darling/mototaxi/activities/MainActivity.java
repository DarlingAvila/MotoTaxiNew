package com.darling.mototaxi.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.darling.mototaxi.R;
import com.darling.mototaxi.activities.client.MapClientActivity;
import com.darling.mototaxi.activities.driver.MapDriverActivity;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {


    //botones para darle funcionalidad al login principal
    Button nButtonIAmClient;
    Button nButtonIAmDriver;
    //usaremos shareprefe para saber si es un mototaxista o un cliente
    SharedPreferences mPref;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //incializamos el share y usamos el typeuser
        mPref = getApplicationContext().getSharedPreferences(  "typeUser", MODE_PRIVATE);
        SharedPreferences.Editor editor = mPref.edit();
        //referencias de los botones
        nButtonIAmClient = findViewById(R.id.btnIAmClient);
        nButtonIAmDriver = findViewById(R.id.btnIAmDriver);
        //oncliclistener programacion de la funcionalidad del boton
        nButtonIAmClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("user", "client");
                editor.apply();
                goToSelectAuth();
            }
        });
        //se hace lo mismo con lo del mototaxista para ponerle funcionalidad al boton

        nButtonIAmDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("user", "driver");
                editor.apply();
                goToSelectAuth();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null){
            String user = mPref.getString("user", "");
            if(user.equals("client")){
                //nos envia a los mapas ya se que elija cliente o mototaxista
                Intent intent = new Intent(MainActivity.this, MapClientActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
            else{
                Intent intent = new Intent(MainActivity.this, MapDriverActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

        }
    }

    private void goToSelectAuth() {
        Intent intent = new Intent(MainActivity.this, SelectOptionAuthActivity.class);
        startActivity(intent);
    }


}