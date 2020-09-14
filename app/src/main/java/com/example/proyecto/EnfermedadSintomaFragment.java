package com.example.proyecto;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.proyecto.Entidades.Enfermedad;
import com.example.proyecto.Entidades.Medico;
import com.example.proyecto.Entidades.sintoma;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EnfermedadSintomaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EnfermedadSintomaFragment extends Fragment implements Response.Listener<JSONObject>,Response.ErrorListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Spinner senfermedad,ssintoma ;
    ProgressDialog dialogo;
    JsonObjectRequest jsonObjectRequest;
    Button btnasociar;

    public EnfermedadSintomaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EnfermedadSintomaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EnfermedadSintomaFragment newInstance(String param1, String param2) {
        EnfermedadSintomaFragment fragment = new EnfermedadSintomaFragment();
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
        View vista = inflater.inflate(R.layout.fragment_enfermedad_sintoma, container, false);
        senfermedad = vista.findViewById(R.id.spinerenfermedad);
        ssintoma = vista.findViewById(R.id.spinersintoma);
        btnasociar = vista.findViewById(R.id.asociar);
        llenar_spiner();
        btnasociar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               asociar();
            }
        });
        return vista;
    }

    private void llenar_spiner() {
        dialogo = new ProgressDialog(this.getContext());
        dialogo.setMessage("Espere...");
        dialogo.show();
        String url="https://dep2020.000webhostapp.com/listar_sintomas_enfermedades.php";

        url = url.replace(" ","%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url,null,this,this);
        RequestQueue requestQueue = Volley.newRequestQueue(this.getContext());
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(getContext(), "No se pudo conectar debido a:"+error.toString(), Toast.LENGTH_LONG).show();
        dialogo.hide();
    }

    @Override
    public void onResponse(JSONObject response) {
        dialogo.hide();
        JSONArray json = response.optJSONArray("enfermedad");
        JSONArray json2 = response.optJSONArray("sintoma");
        JSONObject jsonObject = null;
        JSONObject jsonObject2 = null;
        ArrayList<String> enfermedades = new ArrayList<>();
        ArrayList<String> sintomas = new ArrayList<>();
        try {
            for (int i = 0;i<json.length(); i++ ){
                Enfermedad enfermedad = new Enfermedad();
                jsonObject = json.getJSONObject(i);
                enfermedad.setCodigo(jsonObject.optString("codigo"));
                enfermedad.setNombre(jsonObject.optString("nombre"));
                enfermedad.setGravedad(jsonObject.optString("gravedad"));
                enfermedades.add(enfermedad.getNombre());
            }
            ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this.getContext(),android.R.layout.simple_dropdown_item_1line, enfermedades);
            senfermedad.setAdapter(adaptador);
        }catch (JSONException e){
            e.printStackTrace();
        }

        try {
            for (int i = 0;i<json2.length(); i++ ){
                sintoma sintoma = new sintoma();
                jsonObject = json2.getJSONObject(i);
                sintoma.setCodigo(jsonObject.optString("codigo"));
                sintoma.setDescripcion(jsonObject.optString("descripcion"));
                sintomas.add(sintoma.getDescripcion());
            }
            ArrayAdapter<String> adaptador2 = new ArrayAdapter<String>(this.getContext(),android.R.layout.simple_dropdown_item_1line, sintomas);
            ssintoma.setAdapter(adaptador2);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void asociar(){
        dialogo = new ProgressDialog(this.getContext());
        dialogo.setMessage("Espere...");
        dialogo.show();
        String url = "https://dep2020.000webhostapp.com/asociar.php?enfermedad="+senfermedad.getSelectedItem()+"&sintoma="+ssintoma.getSelectedItem();
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getContext(), ""+response, Toast.LENGTH_LONG).show();
                dialogo.hide();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialogo.hide();
                Toast.makeText(getContext(), "No se pudo conectar debido a:"+error.toString(), Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this.getContext());
        requestQueue.add(request);
    }
}