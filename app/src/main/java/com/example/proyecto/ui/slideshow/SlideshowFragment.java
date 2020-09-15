package com.example.proyecto.ui.slideshow;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.pedant.SweetAlert.SweetAlertDialog;
import android.graphics.Color;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.proyecto.R;

import org.json.JSONObject;

public class SlideshowFragment extends Fragment implements Response.Listener<JSONObject>,Response.ErrorListener{

    private SlideshowViewModel slideshowViewModel;

    EditText cedula,nombrecompleto,edad,regimen,clave,email,direccion,telefono;
    Button btnLogin;
    SweetAlertDialog dialogo;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);

        cedula = root.findViewById(R.id.cedulalayout);
        nombrecompleto = root.findViewById(R.id.nombrecompleto);
        edad = root.findViewById(R.id.edadlayout);
        regimen = root.findViewById(R.id.especialidad);
        clave = root.findViewById(R.id.clave);
        email = root.findViewById(R.id.emaillayout);
        direccion = root.findViewById(R.id.direccionlayout);
        telefono = root.findViewById(R.id.telefonolayout);
        btnLogin = root.findViewById(R.id.btnLogin);

        request = Volley.newRequestQueue(getContext());

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cargarwebservice();
            }
        });

        return root;
    }

    public void cargarwebservice(){

        dialogo = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        dialogo.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        dialogo.setTitleText("Espere ...");
        dialogo.setCancelable(true);
        dialogo.show();

        String url="https://dep2020.000webhostapp.com/registrar_paciente.php?cedula="+cedula.getText().toString()
                +"&nombre_completo="+nombrecompleto.getText().toString()
                +"&edad="+edad.getText().toString()
                +"&regimen="+regimen.getText().toString()
                +"&email="+email.getText().toString()
                +"&password="+clave.getText().toString()
                +"&direccion="+direccion.getText().toString()
                +"&telefono="+telefono.getText().toString();

        url = url.replace(" ","%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url,null,this,this);

        request.add(jsonObjectRequest);
    }

    @Override
    public void onResponse(JSONObject response) {
        dialogo.hide();
        cedula.setText("");
        nombrecompleto.setText("");
        edad.setText("");
        regimen.setText("");
        email.setText("");
        clave.setText("");
        direccion.setText("");
        telefono.setText("");
        new SweetAlertDialog(getContext())
                .setTitleText("Paciente registrado correctamente!")
                .show();
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        dialogo.hide();
        new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText("No se pudo conectar debido a:"+error.toString())
                .show();
    }

}