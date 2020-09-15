package com.example.proyecto;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MedicoActivity extends AppCompatActivity {

    BottomNavigationView menu;
    FloatingActionButton salir;
    NavController nav;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medico);
        menu = findViewById(R.id.menu_medico);
        nav = Navigation.findNavController(this,R.id.nav_medico);
        salir = findViewById(R.id.boton_salir);

        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                salir();
            }
        });
        NavigationUI.setupWithNavController(menu, nav);
    }

    public void salir(){
        new SweetAlertDialog(MedicoActivity.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("")
                .setContentText("Esta seguro de cerrar sesion?")
                .setConfirmText("Si")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        SharedPreferences preferences = getSharedPreferences("datoslogin", Context.MODE_PRIVATE);
                        preferences.edit().clear().commit();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .setCancelButton("No", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }
}