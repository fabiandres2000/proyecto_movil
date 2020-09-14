package com.example.proyecto;


import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.textclassifier.TextLinks;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.proyecto.Entidades.Paciente;
import com.example.proyecto.Entidades.Usuario;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import cn.pedant.SweetAlert.SweetAlertDialog;
import android.graphics.Color;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements  Response.Listener<JSONObject>,Response.ErrorListener{
    EditText usuario,password;
    Button enviar;
    SweetAlertDialog dialogo;
    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;
    String user, pass ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usuario = findViewById(R.id.edtUsuario);
        password = findViewById(R.id.edtPassword);
        enviar = findViewById(R.id.btnLogin);

        recuperar_datos();

        enviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user = usuario.getText().toString();
                pass = password.getText().toString();
                if (!user.isEmpty() && !pass.isEmpty()){
                    validar_usuario();
                }else {
                    Toast.makeText(MainActivity.this, "No se permiten datos vacios", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private  void validar_usuario(){
        dialogo = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        dialogo.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        dialogo.setTitleText("Espere ...");
        dialogo.setCancelable(true);
        dialogo.show();

        String url="https://dep2020.000webhostapp.com/login.php?usuario="+usuario.getText().toString()+"" +
                "&password="+password.getText().toString();

        url = url.replace(" ","%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url,null,this,this);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }


    public void guardardatos (String tipo){
        SharedPreferences preferences = getSharedPreferences("datoslogin", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("usuario", user);
        editor.putString("password", pass);
        editor.putBoolean("session", true);
        editor.putString("tipo", tipo);
        editor.commit();
    }

    public void recuperar_datos (){
        SharedPreferences preferences = getSharedPreferences("datoslogin", Context.MODE_PRIVATE);
        usuario.setText(preferences.getString("usuario",""));
        password.setText(preferences.getString("password",""));
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(error.toString())
                .show();
        dialogo.hide();
    }

    @Override
    public void onResponse(JSONObject response) {
        dialogo.hide();
        Usuario miusuario = new Usuario();
        JSONArray json = response.optJSONArray("usuario");
        JSONObject jsonObject = null;
        try {
            jsonObject = json.getJSONObject(0);
            miusuario.setEmail(jsonObject.optString("us_usuario"));
            miusuario.setPassword(jsonObject.optString("us_password"));
            miusuario.setTipo(jsonObject.optString("tipo"));
        }catch (JSONException e){
                e.printStackTrace();
        }
        String tipo = miusuario.getTipo();
        guardardatos(tipo);
        if (tipo.equals("administrador")){
            Toast.makeText(MainActivity.this, "Usted es administrador", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getApplicationContext(), AdministradorActivity.class);
            startActivity(intent);
            finish();
        }else{
            if(tipo.equals("medico")){
                Toast.makeText(MainActivity.this, "Usted es medico", Toast.LENGTH_LONG).show();
            }else{
                if (tipo.equals("paciente")){
                    Toast.makeText(MainActivity.this, "Usted es paciente", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), PacienteActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    new SweetAlertDialog(MainActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Datos erroneos verifique!")
                            .show();
                }
            }
        }
    }
}