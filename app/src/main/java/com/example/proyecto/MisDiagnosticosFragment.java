package com.example.proyecto;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.TestLooperManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.proyecto.Entidades.Paciente;
import com.example.proyecto.Entidades.sintoma;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MisDiagnosticosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MisDiagnosticosFragment extends Fragment implements   Response.Listener<JSONObject>,Response.ErrorListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    SweetAlertDialog pDialog;
    String usuariop,tipop;
    JsonObjectRequest jsonObjectRequest;
    ListView lista_diagnosticos;
    String cedula_usar;

    public MisDiagnosticosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MisDiagnosticosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MisDiagnosticosFragment newInstance(String param1, String param2) {
        MisDiagnosticosFragment fragment = new MisDiagnosticosFragment();
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
        View view = inflater.inflate(R.layout.fragment_mis_diagnosticos, container, false);
        lista_diagnosticos = view.findViewById(R.id.lista_diagnosticos);
        pDialog =  new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Espere mientras se cargan los datos...");
        pDialog.setCancelable(true);
        pDialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences preferences = getActivity().getSharedPreferences("datoslogin", Context.MODE_PRIVATE);
                boolean sesion = preferences.getBoolean("session", false);
                tipop = preferences.getString("tipo","ninguno");
                usuariop = preferences.getString("usuario","ninguno");
                consultar_paciente();
            }
        },2000);
        return  view;
    }

    private void consultar_paciente() {
        String url="https://dep2020.000webhostapp.com/consultar_paciente.php?email="+usuariop;
        url = url.replace(" ","%20");
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url,null,this,this);
        RequestQueue requestQueue = Volley.newRequestQueue(this.getContext());
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(error.toString())
                .show();
        pDialog.hide();
    }
    Paciente paciente;
    @Override
    public void onResponse(JSONObject response) {

        JSONArray json = response.optJSONArray("paciente");
        JSONObject jsonObject = null;
        try {
            paciente = new Paciente();
            jsonObject = json.getJSONObject(0);
            paciente.setCedula(jsonObject.optString("cedula"));
            paciente.setNombre(jsonObject.optString("nombre_completo"));
            paciente.setEdad(jsonObject.optString("edad"));
            paciente.setRegimen(jsonObject.optString("regimen"));
            paciente.setEmail(jsonObject.optString("email"));
            paciente.setClave(jsonObject.optString("password"));
            paciente.setDireccion(jsonObject.optString("direccion"));
            paciente.setTelefono(jsonObject.optString("telefono"));
            cedula_usar = paciente.getCedula();
            lista_diagnosticos();
        }catch (JSONException e){
            e.printStackTrace();
            Toast.makeText(getContext(), "no se pudo establecer conexion", Toast.LENGTH_LONG).show();
            pDialog.hide();
        }

    }

    public void lista_diagnosticos(){
        pDialog.hide();
        String url = "https://dep2020.000webhostapp.com/listar_diagnosticos.php?cedula="+cedula_usar;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray json = response.optJSONArray("diagnosticos");
                JSONObject jsonObject = null;
                ArrayList<String> lista = new ArrayList<>();

                for (int i = 0;i<json.length(); i++ ){
                    try {
                        jsonObject = json.getJSONObject(i);
                        String enfermedad = (jsonObject.optString("enfermedad"));
                        String probabilidad = (jsonObject.optString("probabilidad"));
                        String fecha = (jsonObject.optString("fecha"));
                        String hora = (jsonObject.optString("hora"));
                        lista.add("   Enfermedad:   "+enfermedad+"\n"+"   Probabilidad:   "+probabilidad+"%\n"+"   Fecha:   "+fecha+"\n"+"   Hora:   "+hora);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }

                ArrayAdapter<String> adaptador = new ArrayAdapter<String>(getContext(), R.layout.lisviewdiagnosticos, lista);
                lista_diagnosticos.setAdapter(adaptador);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("Aùn no ha registrado ningun diagnostico:"+error.toString())
                        .show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this.getContext());
        requestQueue.add(request);
    }

}