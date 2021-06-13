package com.darling.mototaxi.activities.driver;

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
import com.darling.mototaxi.models.Driver;
import com.darling.mototaxi.providers.AuthProvider;
import com.darling.mototaxi.providers.DriverProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import dmax.dialog.SpotsDialog;

public class RegisterDriverActivity extends AppCompatActivity {

    AuthProvider mAuthProvider;
    DriverProvider mDriverProvider;
    //instanciamos las vistas
    AlertDialog mDialog;

    Button nButtonRegister;
    TextInputEditText mTextInputEmail;
    TextInputEditText mTextInputName;
    TextInputEditText mTextInputVehicleBrand;
    TextInputEditText mTextInputVehiclePlate;
    TextInputEditText mTextInputPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_driver);
        //llamamos al toolbar
        MyToolbar.show(this,"Registro de Mototaxistas", true);
        //instanciamos el mauth

        mAuthProvider = new AuthProvider();
        mDriverProvider = new DriverProvider();
        //instanciamos el dialog
        mDialog = new SpotsDialog.Builder().setContext(RegisterDriverActivity.this).setMessage("Espere un momento").build();
        nButtonRegister  = findViewById(R.id.btnRegister);
        mTextInputEmail = findViewById(R.id.textInputEmail);
        mTextInputName = findViewById(R.id.textInputName);
        mTextInputVehicleBrand = findViewById(R.id.textInputVehicleBrand);
        mTextInputVehiclePlate = findViewById(R.id.textInputVehiclePlate);
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
        final String vehicleBrand = mTextInputVehicleBrand.getText().toString();
        final String vehiclePlate= mTextInputVehiclePlate.getText().toString();
        final String password = mTextInputPassword.getText().toString();

        if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty() && !vehicleBrand.isEmpty() && !vehiclePlate.isEmpty()){
            if (password.length()>=6){

                mDialog.show();
                register(name, email,password, vehicleBrand, vehiclePlate);
            }
            else {
                Toast.makeText(this,"La contraseña debe tener 6 caracteres", Toast.LENGTH_SHORT).show();
            }
        }
        else {
            Toast.makeText(this,"Ingrese todos los campos", Toast.LENGTH_SHORT).show();

        }
    }

    private void register(final String name, String email, String password, String vehicleBrand, String vehiclePlate) {

        mAuthProvider.register(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                mDialog.hide();
                if (task.isSuccessful()){
                    //se obtiene el identificador del usuario
                    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    Driver driver = new Driver(id, name, email, vehicleBrand, vehiclePlate);
                    create(driver);

                }
                else {
                    Toast.makeText(RegisterDriverActivity.this,"No se pudo registrar el usuario", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
    void create(Driver driver){

        mDriverProvider.create(driver).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                    //Toast.makeText(RegisterDriverActivity.this, "El registro se realizo existosamente", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterDriverActivity.this, MapDriverActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(RegisterDriverActivity.this, "No se pudo crear el cliente", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }
}