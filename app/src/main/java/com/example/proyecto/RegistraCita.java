package com.example.proyecto;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import cn.pedant.SweetAlert.SweetAlertDialog;
import android.graphics.Color;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.proyecto.Entidades.Enfermedad;
import com.example.proyecto.Entidades.Medico;
import com.example.proyecto.Entidades.sintoma;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegistraCita#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegistraCita extends Fragment implements DatePickerDialog.OnDateSetListener, Response.Listener<JSONObject>,Response.ErrorListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextView fechacita;
    Button elegir_fecha;
    SweetAlertDialog pDialog;
    JsonObjectRequest jsonObjectRequest;
    Spinner medicocita;
    public RegistraCita() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegistraCita.
     */
    // TODO: Rename and change types and number of parameters
    public static RegistraCita newInstance(String param1, String param2) {
        RegistraCita fragment = new RegistraCita();
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
        View view =  inflater.inflate(R.layout.fragment_registra_cita, container, false);
        fechacita = view.findViewById(R.id.fechacita);
        elegir_fecha = view.findViewById(R.id.elegirfecha);
        medicocita = view.findViewById(R.id.medicocita);
        llenar_spinner();
        elegir_fecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showdatepicker();
            }
        });
        return  view;
    }

    private void showdatepicker(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        String fecha = i2+"-"+i1+"-"+i;
        fechacita.setText(fecha);
    }

    private void llenar_spinner() {

        pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Cargando ...");
        pDialog.setCancelable(true);
        pDialog.show();


        String url="https://dep2020.000webhostapp.com/listar_medicos.php";

        url = url.replace(" ","%20");

        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url,null,this,this);
        RequestQueue requestQueue = Volley.newRequestQueue(this.getContext());
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        new SweetAlertDialog(getContext(), SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText("No se pudo conectar debido a:"+error.toString())
                .show();
        pDialog.hide();
    }

    @Override
    public void onResponse(JSONObject response) {
        pDialog.hide();
        JSONArray json = response.optJSONArray("medico");
        JSONObject jsonObject = null;
        ArrayList<String> medicos = new ArrayList<>();
        try {
            for (int i = 0;i<json.length(); i++ ){
                Medico medico = new Medico();
                jsonObject = json.getJSONObject(i);
                medico.setCedula(jsonObject.optString("cedula"));
                medico.setNombre(jsonObject.optString("nombre_completo"));
                medico.setEdad(jsonObject.optString("edad"));
                medico.setEspecialidad(jsonObject.optString("especialidad"));
                medico.setEmail(jsonObject.optString("email"));
                medico.setClave(jsonObject.optString("password"));
                medico.setDireccion(jsonObject.optString("direccion"));
                medico.setTelefono(jsonObject.optString("telefono"));
                medicos.add(medico.getNombre());
            }
            ArrayAdapter<String> adaptador = new ArrayAdapter<String>(getContext(),android.R.layout.simple_dropdown_item_1line, medicos);
            medicocita.setAdapter(adaptador);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}