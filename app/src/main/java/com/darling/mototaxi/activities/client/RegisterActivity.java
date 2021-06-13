package com.darling.mototaxi.activities.client;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.darling.mototaxi.R;
import com.darling.mototaxi.includs.MyToolbar;
import com.darling.mototaxi.models.Client;
import com.darling.mototaxi.providers.AuthProvider;
import com.darling.mototaxi.providers.ClientProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import dmax.dialog.SpotsDialog;


public class RegisterActivity extends AppCompatActivity {

    //para diferenciar si es conducto o no

    AuthProvider mAuthProvider;
    ClientProvider mClientProvider;
    //instanciamos las vistas
    AlertDialog mDialog;

    Button nButtonRegister;
    TextInputEditText mTextInputEmail;
    TextInputEditText mTextInputName;
    TextInputEditText mTextInputPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //llamamos al toolbar
        MyToolbar.show(this,"Registro de usuario", true);
        //instanciamos el mauth

        mAuthProvider = new AuthProvider();
        mClientProvider = new ClientProvider();
        //instanciamos el dialog
        mDialog = new SpotsDialog.Builder().setContext(RegisterActivity.this).setMessage("Espere un momento").build();

        nButtonRegister  = findViewById(R.id.btnRegister);
        mTextInputEmail = findViewById(R.id.textInputEmail);
        mTextInputName = findViewById(R.id.textInputName);
        mTextInputPassword = findViewById(R.id.textInputPassword);

        nButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickRegister();
            }
        });

    }

    private void clickRegister() {
        final String name = mTextInputName.getText().toString();
        final String email = mTextInputEmail.getText().toString();
        final String password = mTextInputPassword.getText().toString();

        if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()){
            if (password.length()>=6){

                mDialog.show();
                register(name, email,password);
            }
            else {
                Toast.makeText(this,"La contraseña debe tener 6 caracteres", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(this,"Ingrese todos los campos", Toast.LENGTH_SHORT).show();

        }
    }

    void register(final String name, final String email, String password){
        mAuthProvider.register(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                mDialog.hide();
                if (task.isSuccessful()){
                    //se obtiene el identificador del usuario
                    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    Client client = new Client(id, name, email);
                    create(client);

                }
                else {
                    Toast.makeText(RegisterActivity.this,"No se pudo registrar el usuario", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    void create(Client client){

        mClientProvider.create(client).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    //Toast.makeText(RegisterActivity.this, "El registro se realizo existosamente", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterActivity.this, MapClientActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(RegisterActivity.this, "No se pudo crear el cliente", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    /*
    private void saveUser(String id, String name, String email) {

        String selectedUser = mPref.getString("user", "");
        //Toast.makeText(this,"El valor que selecciono fue" + selectedUser, Toast.LENGTH_SHORT).show();

        User user = new User();
        user.setEmail(email);
        user.setName(name);

        if(selectedUser.equals("driver")){
            mDatabase.child("user").child("drivers").child(id).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(RegisterActivity.this, "Registro existoso", Toast.LENGTH_SHORT).show();

                    }
                    else {
                        Toast.makeText(RegisterActivity.this, "Fallo registro", Toast.LENGTH_SHORT).show();

                    }
                }
            });

        }
        else if (selectedUser.equals("client")){
            mDatabase.child("user").child("Clients").child(id).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull  Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(RegisterActivity.this, "Registro existoso", Toast.LENGTH_SHORT).show();

                    }
                    else {
                        Toast.makeText(RegisterActivity.this, "Fallo registro", Toast.LENGTH_SHORT).show();

                    }
                }
            });

        }
    }
/*
    void registerUser() {
        final String name = mTextInputName.getText().toString();
        final String email = mTextInputEmail.getText().toString();
        final String password = mTextInputPassword.getText().toString();

        if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {
            if (password.length() >= 6) {
             //   mDialog.show();
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                     //   mDialog.hide();
                        if (task.isSuccessful()) {
                            String id = mAuth.getCurrentUser().getUid();
                            saveUser(id, name, email);
                        }
                        else {
                            Toast.makeText(RegisterActivity.this, "No se pudo registrar el usuario", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            else {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(this, "Ingrese todos los campos", Toast.LENGTH_SHORT).show();
        }
    }


    void registerUser(){
    final String name = mTextInputName.getText().toString();
    final String email = mTextInputEmail.getText().toString();
    final String password = mTextInputPassword.getText().toString();

    if(!name.isEmpty() && !email.isEmpty() && !password.isEmpty()){
        if (password.length() >=6){
            mDialog.show();
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<AuthResult> task) {
                    mDialog.hide();
                    if(task.isSuccessful()){
                        String id = mAuth.getCurrentUser().getUid();
                        saveUser(name, email);
                    }else {

                        Toast.makeText(RegisterActivity.this, "No se puede registar el usuario", Toast.LENGTH_SHORT).show();

                    }
                }
            });

        }
        else {

            Toast.makeText(this, "contraseña debe tener 6 caracteres", Toast.LENGTH_SHORT).show();
        }
    }
    else {

        Toast.makeText(this, "ingrese todos los campos", Toast.LENGTH_SHORT).show();
    }

    }

    private void saveUser(String id, String name, String email) {

        String selectedUser= mPref.getString( "user", " ");
        User user = new User();
        user.setEmail(email);
        user.setName(name);

        if(selectedUser.equals("driver")){

            mDatabase.child("User").child("driver").child(id).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(RegisterActivity.this,"Registro exitoso",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(RegisterActivity.this,"Fallo el registro",Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
        else  if(selectedUser.equals("client")){
            mDatabase.child("User").child("client").child(id).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if(task.isSuccessful()){
                        Toast.makeText(RegisterActivity.this,"Registro exitoso",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(RegisterActivity.this,"Fallo el registro",Toast.LENGTH_SHORT).show();
                    }


                }
            });
        }


    }

*/

}