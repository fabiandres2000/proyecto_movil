package com.example.proyecto;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CitasMedicoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CitasMedicoFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    String usuariop,tipop;
    JsonObjectRequest jsonObjectRequest;
    ListView listacitas;
    SweetAlertDialog pDialog;

    public CitasMedicoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CitasMedicoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CitasMedicoFragment newInstance(String param1, String param2) {
        CitasMedicoFragment fragment = new CitasMedicoFragment();
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
        View view =  inflater.inflate(R.layout.fragment_citas_medico, container, false);
        listacitas = view.findViewById(R.id.lista_citas_medico);

        pDialog =  new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("espere un momento...");
        pDialog.setCancelable(true);
        pDialog.show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences preferences = getActivity().getSharedPreferences("datoslogin", Context.MODE_PRIVATE);
                boolean sesion = preferences.getBoolean("session", false);
                tipop = preferences.getString("tipo","ninguno");
                usuariop = preferences.getString("usuario","ninguno");
                obtener_medico();
            }
        },2000);

        listacitas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                atender_cita(i);
            }
        });

        return  view;
    }

    String medico;
    public void obtener_medico(){
        String url="https://dep2020.000webhostapp.com/consultar_medico.php?email="+usuariop;
        url = url.replace(" ","%20");
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pDialog.hide();
                JSONArray json = response.optJSONArray("medico");
                JSONObject jsonObject = null;
                try {
                    jsonObject = json.getJSONObject(0);
                    medico = jsonObject.optString("nombre_completo");
                    llenar_lista_citas(medico);
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText(error.toString())
                        .show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this.getContext());
        requestQueue.add(request);
    }


    ArrayList<String> lista;
    ArrayList<String> listaid;
    public  void llenar_lista_citas(String medico){
        String url = "https://dep2020.000webhostapp.com/listar_citas_medico.php?medico="+medico;
        url = url.replace(" ","%20");
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pDialog.hide();
                JSONArray json = response.optJSONArray("citas");
                JSONObject jsonObject = null;
                lista = new ArrayList<>();
                listaid = new ArrayList<>();
                for (int i = 0;i<json.length(); i++ ){
                    try {
                        jsonObject = json.getJSONObject(i);
                        String id =  (jsonObject.optString("id"));
                        String medico = (jsonObject.optString("medico"));
                        String paciente = (jsonObject.optString("paciente"));
                        String fecha = (jsonObject.optString("fecha"));
                        String hora = (jsonObject.optString("hora"));
                        String minuto = (jsonObject.optString("minuto"));
                        String estado = (jsonObject.optString("estado"));
                        listaid.add(id);
                        lista.add("   Medico:   "+medico+"\n"+"   Paciente:   "+paciente+"\n"+"   Fecha:   "+fecha+"\n"+"   Hora:   "+hora+"\n"+"   Minuto:   "+minuto+"\n"+"   Estado:   "+estado);
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }

                ArrayAdapter<String> adaptador = new ArrayAdapter<String>(getContext(), R.layout.lisviewcita, lista);
                listacitas.setAdapter(adaptador);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.hide();
                new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("Aùn no se le ha asociado  ninguna cita")
                        .show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this.getContext());
        requestQueue.add(request);
    }

    public void atender_cita(int posicion){
        String cita = lista.get(posicion);
        final String idcita = listaid.get(posicion);
        new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("¿Esta seguro de cancelar la cita medica?")
                .setContentText(cita)
                .setConfirmText("Si!")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                        atender_cita_2(idcita);
                    }
                })
                .setCancelButton("No!", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }

    public void  atender_cita_2(String id_cita){

        pDialog =  new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Atendiendo cita...");
        pDialog.setCancelable(true);
        pDialog.show();

        String url = "https://dep2020.000webhostapp.com/atender_cita.php?id_cita="+id_cita;
        url = url.replace(" ","%20");
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                pDialog.hide();
                JSONArray json = response.optJSONArray("respuesta");
                JSONObject jsonObject = null;
                try {
                    jsonObject = json.getJSONObject(0);
                    String respuesta =  (jsonObject.optString("res"));
                    new SweetAlertDialog(getContext())
                            .setTitleText(respuesta)
                            .show();
                    llenar_lista_citas(medico);
                }catch (JSONException e){
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog.hide();
                new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText(error.toString())
                        .show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this.getContext());
        requestQueue.add(request);
    }
}