package com.darling.mototaxi.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.darling.mototaxi.R;
import com.darling.mototaxi.activities.client.MapClientActivity;
import com.darling.mototaxi.activities.driver.MapDriverActivity;
import com.darling.mototaxi.includs.MyToolbar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import dmax.dialog.SpotsDialog;

public class LoginActivity extends AppCompatActivity {


    TextInputEditText mTextInputEmail;
    TextInputEditText mTextInputPassword;
    Button mButtonLogin;
    SharedPreferences mPref;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    AlertDialog mDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        MyToolbar.show(this,"login de usuario", true);
        //instanciamos
       mTextInputEmail = findViewById(R.id.textInputEmail);
       mTextInputPassword = findViewById(R.id.textInputPassword);
       mButtonLogin = findViewById(R.id.btnLogin);
        //instanciamos
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mPref = getApplicationContext().getSharedPreferences(  "typeUser", MODE_PRIVATE);
        mDialog = new SpotsDialog.Builder().setContext(LoginActivity.this).setMessage("Espere un momento").build();

        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {

        String email = mTextInputEmail.getText().toString();
        String password = mTextInputPassword.getText().toString();

        if (!email.isEmpty() && !password.isEmpty()) {
            if (password.length() >= 6) {
                mDialog.show();
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String user = mPref.getString("user","");
                            if(user.equals("client")){
                                //nos envia a los mapas ya se que elija cliente o mototaxista
                                Intent intent = new Intent(LoginActivity.this, MapClientActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                            else{
                                Intent intent = new Intent(LoginActivity.this, MapDriverActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                           }
                        else {
                            Toast.makeText(LoginActivity.this, "La contraseña o el password son incorrectos", Toast.LENGTH_SHORT).show();
                        }
                        mDialog.dismiss();
                    }
                });
            }
            else {
                Toast.makeText(this, "La contraseña debe tener mas de 6 caracteres", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(this, "La contraseña y el email son obligatorios", Toast.LENGTH_SHORT).show();
        }
    }



    /*

    //creamos los textos de login
    TextInputEditText mTextInputEmail;
    TextInputEditText mTextInputPassword;
    Button mButtonLogin;

    FirebaseAuth mAuth;
    //base de datos de tiempo real de firebase
    DatabaseReference mDatabase;

    //alerta de carga
    AlertDialog mDialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        MyToolbar.show(this,"Login de usuario",true);

        //instanciamos
        mTextInputEmail = findViewById(R.id.textInputEmail);
        mTextInputPassword = findViewById(R.id.textInputPassword);
        mButtonLogin = findViewById(R.id.btnLogin);
        //instanciamos la el mAuth de firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //instanciamos el dialog
        mDialog = new SpotsDialog.Builder().setContext(LoginActivity.this).setMessage("Espere un momento").build();


        //esteblemos el metodo oncliclistener

        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //haremos que ejecute una funcion lllamada login
                login();

            }
        });
    }

    private void login() {
        String email = mTextInputEmail.getText().toString();
        String password = mTextInputPassword.getText().toString();

        if (!email.isEmpty() && !password.isEmpty()) {
            if (password.length() >= 6) {
                mDialog.show();
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "El login se realizo exitosamente", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(LoginActivity.this, "La contraseña o el password son incorrectos", Toast.LENGTH_SHORT).show();
                        }
                        mDialog.dismiss();
                    }
                });
            }
            else {
                Toast.makeText(this, "La contraseña debe tener mas de 6 caracteres", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(this, "La contraseña y el email son obligatorios", Toast.LENGTH_SHORT).show();
        }
    }

    /*public  void login(){
        //debemos hacer que nos mande los datos que se ingresan en el login
        //variables y lo convertimos a una cadena de texto con el tostring
        String email= mTextInputEmail.getText().toString();
        String password= mTextInputPassword.getText().toString();

        //validamos si el usuario ingreso un correo

        if(!email.isEmpty() && !password.isEmpty()){
            if (!email.isEmpty() && !password.isEmpty()){
                if(password.length()>= 6){
                    //mostramos el dialog alert
                    mDialog.show();

                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                String user= mPref.getString("user", "");
                                if(user.equals("client")){

                                    Intent intent = new Intent(LoginActivity.this, MapClientActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                } else{

                                    Intent intent = new Intent(LoginActivity.this, MapDriverActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                }
                                // Toast.makeText(LoginActivity2.this, "El login se realizo exitosamente", Toast.LENGTH_SHORT).show();

                            }
                            else{
                                Toast.makeText(LoginActivity.this, "La contraseña o el password son incorrectos", Toast.LENGTH_SHORT).show();

                            }
                            //dejamos de mostrar el alert dialogo
                            mDialog.dismiss();
                        }
                    });
                }
                else{
                    Toast.makeText(this, "La contraseña debe tener mas de 6 caracteres", Toast.LENGTH_SHORT).show();

                }
            }
            else{
                Toast.makeText(this, "La contraseña e email son obligatorios", Toast.LENGTH_SHORT).show();
            }
        }

    }*/
}