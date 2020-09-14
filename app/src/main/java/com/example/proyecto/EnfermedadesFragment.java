package com.example.proyecto;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EnfermedadesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EnfermedadesFragment extends Fragment  implements Response.Listener<JSONObject>,Response.ErrorListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    EditText codigo,nombre;
    Spinner gravedad;
    ProgressDialog dialogo;
    Button guardar;
    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    public EnfermedadesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EnfermedadesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EnfermedadesFragment newInstance(String param1, String param2) {
        EnfermedadesFragment fragment = new EnfermedadesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_enfermedades, container, false);
        codigo = root.findViewById(R.id.codigo_enfermedad);
        nombre = root.findViewById(R.id.nombre_enfermedad);
        gravedad = root.findViewById(R.id.gravedad);
        guardar = root.findViewById(R.id.boton_guardar);

        request = Volley.newRequestQueue(getContext());

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cargarwebservice();
            }
        });

       return  root;
    }

    public void cargarwebservice(){
        dialogo = new ProgressDialog(getContext());
        dialogo.setMessage("cargando...");
        dialogo.show();
        String url="https://dep2020.000webhostapp.com/registrar_enfermedad.php?codigo="+codigo.getText().toString()
                +"&nombre="+nombre.getText().toString()
                +"&gravedad="+gravedad.getSelectedItem().toString();

        url = url.replace(" ","%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url,null,this,this);

        request.add(jsonObjectRequest);
    }
    @Override
    public void onResponse(JSONObject response) {
        Toast.makeText(getContext(), "Datos Registrados correctamente", Toast.LENGTH_LONG).show();
        dialogo.hide();
        codigo.setText("");
        nombre.setText("");
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(), "No se pudo conectar debido a:"+error.toString(), Toast.LENGTH_LONG).show();
        dialogo.hide();
    }
}