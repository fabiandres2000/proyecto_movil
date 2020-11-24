package com.example.proyecto;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.proyecto.Entidades.Paciente;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import cn.pedant.SweetAlert.SweetAlertDialog;
import android.graphics.Color;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RealizarDiagnostico#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class RealizarDiagnostico extends Fragment implements   Response.Listener<JSONObject>,Response.ErrorListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RealizarDiagnostico.
     */
    // TODO: Rename and change types and number of parameters
    public static RealizarDiagnostico newInstance(String param1, String param2) {
        RealizarDiagnostico fragment = new RealizarDiagnostico();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public RealizarDiagnostico() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    ProgressDialog dialogo;
    TextView cedula,nombre,edad,direccion,telefono;
    String usuariop,tipop;
    JsonObjectRequest jsonObjectRequest;
    LinearLayout layout_base;
    Button btn_diagnosticar;

    SweetAlertDialog pDialog;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_realizar_diagnostico, container, false);
        cedula = view.findViewById(R.id.cedula_rd);
        nombre = view.findViewById(R.id.nombre_rd);
        edad = view.findViewById(R.id.edad_rd);
        direccion = view.findViewById(R.id.direccion_rd);
        telefono = view.findViewById(R.id.telefono_rd);
        layout_base = view.findViewById(R.id.lista_check_sintomas);
        btn_diagnosticar = view.findViewById(R.id.btn_diagnosticar);
        btn_diagnosticar.setOnClickListener(enviar);

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
                lista_sintomas();
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
            cedula.setText(paciente.getCedula());
            nombre.setText(paciente.getNombre());
            edad.setText(paciente.getEdad());
            direccion.setText(paciente.getDireccion());
            telefono.setText(paciente.getTelefono());
            pDialog.hide();
        }catch (JSONException e){
            e.printStackTrace();
            Toast.makeText(getContext(), "no se pudo establecer conexion", Toast.LENGTH_LONG).show();
            pDialog.hide();
        }

    }

    public void lista_sintomas(){
        String url = "https://dep2020.000webhostapp.com/listar_sintomas.php";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray json = response.optJSONArray("sintoma");
                JSONObject jsonObject = null;
                try {
                    for (int i = 0;i<json.length(); i++ ){
                        jsonObject = json.getJSONObject(i);
                        int id = (jsonObject.optInt("id"));
                        String descripcion = (jsonObject.optString("descripcion"));
                        CheckBox cb = new CheckBox(getContext());
                        cb.setId(id);
                        cb.setText(descripcion);
                        cb.setTextColor(Color.parseColor("#FFFFFF"));
                        cb.setOnClickListener(seleccionar);
                        layout_base.addView(cb);
                    }
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

    ArrayList<Integer> seleccionados = new ArrayList<>();

    private View.OnClickListener seleccionar = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            boolean checked = ((CheckBox) view).isChecked();
            if (checked){
                  seleccionados.add(id);
            }else{
                 seleccionados.remove(new Integer(id));
            }
        }
    };

    public String enfermedad = "";
    public String probabilidad = "";
    private View.OnClickListener enviar = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String seleccionados2;

            final SweetAlertDialog pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Espere ...");
            pDialog.setCancelable(true);
            pDialog.show();

            StringBuilder strBuilder = new StringBuilder();
            for (int i = 0; i< seleccionados.size();i++){
                strBuilder.append (seleccionados.get(i)+" ");
            }
            seleccionados2 = strBuilder.toString();
            String url = "https://dep2020.000webhostapp.com/realizar_diagnostico.php?sintomaspaciente="+seleccionados2;
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(final JSONObject response) {

                    JSONArray json = response.optJSONArray("respuesta");
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = json.getJSONObject(0);
                        probabilidad = jsonObject.optString("probabilidad");
                        enfermedad = jsonObject.optString("enfermedad");
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    String respuesta = "Segùn los resultados usted tiene("+probabilidad+"%) de probabilidad de tener la enfermedad ("+enfermedad+")";

                    new SweetAlertDialog(getContext(), SweetAlertDialog.WARNING_TYPE)
                            .setTitleText(respuesta)
                            .setContentText("¿desea guardar el diagnostico?")
                            .setConfirmText("Si")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                    guardar_diagnostico(probabilidad,enfermedad,paciente.getCedula());
                                }
                            })
                            .setCancelButton("no", new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                }
                            })
                            .show();

                    pDialog.hide();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getContext(), "No se pudo conectar debido a:"+error.toString(), Toast.LENGTH_LONG).show();
                    pDialog.hide();
                }
            });
            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(request);

        }
    };

    SweetAlertDialog pDialog2;
    public  void  guardar_diagnostico(String probabilidad, String enfermedad, String cedula){
        pDialog2 = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog2.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog2.setTitleText("Guardando diagnostico ...");
        pDialog2.setCancelable(true);
        pDialog2.show();

        String url = "https://dep2020.000webhostapp.com/guardar_diagnostico.php?enfermedad="+enfermedad+"&cedula="+cedula+"&probabilidad="+probabilidad;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(final JSONObject response) {
                pDialog2.hide();
                JSONArray json = response.optJSONArray("respuesta");
                JSONObject jsonObject = null;
                try {
                    jsonObject = json.getJSONObject(0);
                    String res = jsonObject.optString("res");

                    new SweetAlertDialog(getContext())
                            .setTitleText(res)
                            .show();

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pDialog2.hide();
                new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("No se pudo conectar debido a:"+error.toString())
                        .show();

            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(request);
    }
}