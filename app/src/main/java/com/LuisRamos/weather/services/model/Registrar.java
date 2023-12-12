package com.LuisRamos.weather.services.model;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.LuisRamos.weather.MainActivity;
import com.LuisRamos.weather.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class Registrar extends AppCompatActivity {
    private FirebaseAuth auth;
    private LocationManager locationManager;
    private LocationListener locationListener;

     EditText txtInputpassword;
     EditText txtInputemail;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        

        auth = FirebaseAuth.getInstance();
        Button button = findViewById(R.id.btnRegis);
        txtInputemail= findViewById(R.id.txtInputemail);
        txtInputpassword =findViewById(R.id.txtInputpassword);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email=txtInputemail.getText().toString();
                String pass = txtInputpassword.getText().toString();


                if (email.isEmpty()|| pass.isEmpty()){
                    Toast.makeText(Registrar.this,"Completa todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }

                auth.createUserWithEmailAndPassword(email,pass)
                        .addOnCompleteListener(Registrar.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    Users users = new Users(email, pass);
                                    Intent intent = new Intent(Registrar.this, MainActivity.class);

                                    Toast.makeText(Registrar.this,"Registro Exitoso", Toast.LENGTH_SHORT).show();


                                }else {
                                    Toast.makeText(Registrar.this, "Error al registrar el usuario", Toast.LENGTH_SHORT).show();
                                    Log.e("RegisterActivity", "Error al registrar el usuario", task.getException());

                                }
                            }
                        });
                Intent intent = new Intent(Registrar.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    public void btnRegistrar (View view){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        StringBuilder text = new StringBuilder();

        if(txtInputemail.getText().toString().isEmpty() || txtInputpassword.getText().toString().isEmpty()){
            text.append(getString(R.string.Fields_cannot_be_empty));
            alert.setMessage(text);
            alert.setPositiveButton("Cerrar",null);

            alert.show();


    }
    }

}