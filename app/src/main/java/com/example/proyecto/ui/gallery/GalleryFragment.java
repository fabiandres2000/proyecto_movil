package com.example.proyecto.ui.gallery;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class GalleryFragment extends Fragment implements Response.Listener<JSONObject>,Response.ErrorListener {

    private GalleryViewModel galleryViewModel;

    EditText cedula,nombrecompleto,edad,especialidad,clave,email,direccion,telefono;
    Button btnLogin;
    ProgressDialog dialogo;

    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        galleryViewModel =
                ViewModelProviders.of(this).get(GalleryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);

        cedula = root.findViewById(R.id.cedulalayout);
        nombrecompleto = root.findViewById(R.id.nombrecompleto);
        edad = root.findViewById(R.id.edadlayout);
        especialidad = root.findViewById(R.id.especialidad);
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
        dialogo = new ProgressDialog(getContext());
        dialogo.setMessage("cargando...");
        dialogo.show();
        String url="https://dep2020.000webhostapp.com/registrar_medico.php?cedula="+cedula.getText().toString()
                +"&nombre_completo="+nombrecompleto.getText().toString()
                +"&edad="+edad.getText().toString()
                +"&especialidad="+especialidad.getText().toString()
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
        Toast.makeText(getContext(), "Datos Registrados correctamente", Toast.LENGTH_LONG).show();
        dialogo.hide();
        cedula.setText("");
        nombrecompleto.setText("");
        edad.setText("");
        especialidad.setText("");
        email.setText("");
        clave.setText("");
        direccion.setText("");
        telefono.setText("");
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(), "No se pudo conectar debido a:"+error.toString(), Toast.LENGTH_LONG).show();
        dialogo.hide();
    }


}