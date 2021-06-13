package com.darling.mototaxi.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.darling.mototaxi.R;
import com.darling.mototaxi.activities.client.RegisterActivity;
import com.darling.mototaxi.activities.driver.RegisterDriverActivity;
import com.darling.mototaxi.includs.MyToolbar;

public class SelectOptionAuthActivity extends AppCompatActivity {

    Button mButtonGoToLogin;
    Button mButtonGoToRegister;
    SharedPreferences mPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_option_auth);
        //instanciamos el toolbar
        MyToolbar.show(this,"Seleccione una opcion", true);
        mButtonGoToLogin = findViewById(R.id.btnGoToLogin);
        mButtonGoToRegister = findViewById(R.id.btnGoToRegister);
        mButtonGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLogin();
            }
        });
        mButtonGoToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRegister();
            }
        });
        mPref = getApplicationContext().getSharedPreferences(  "typeUser", MODE_PRIVATE);

    }

    private void goToLogin() {
        Intent intent = new Intent(SelectOptionAuthActivity.this, LoginActivity.class);
        startActivity(intent);
    }
    private void goToRegister() {
        String typeUser = mPref.getString("user", " ");
        if( typeUser.equals("client")){
            Intent intent = new Intent(SelectOptionAuthActivity.this, RegisterActivity.class);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(SelectOptionAuthActivity.this, RegisterDriverActivity.class);
            startActivity(intent);
        }

    }
}