package com.example.proyecto;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import com.example.proyecto.Entidades.Medico;

public class IntermedioActivity extends AppCompatActivity {
    ProgressBar barra;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intermedio);
        barra = findViewById(R.id.progressBar2);
        barra.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences preferences = getSharedPreferences("datoslogin", Context.MODE_PRIVATE);
                boolean sesion = preferences.getBoolean("session", false);
                String tipo = preferences.getString("tipo","ninguno");
                if (sesion) {
                    if (tipo.equals("administrador")){
                        Intent intent = new Intent(getApplicationContext(), AdministradorActivity.class);
                        startActivity(intent);
                        finish();
                    }else{
                        if(tipo.equals("medico")){
                            Intent intent = new Intent(getApplicationContext(), MedicoActivity.class);
                            startActivity(intent);
                            finish();
                        }else{
                            if(tipo.equals("paciente")){
                                Intent intent = new Intent(getApplicationContext(), PacienteActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }
                } else {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        },2000);
    }

}