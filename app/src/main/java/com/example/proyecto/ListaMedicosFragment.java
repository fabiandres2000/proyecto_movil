package com.example.proyecto;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import cn.pedant.SweetAlert.SweetAlertDialog;
import android.graphics.Color;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.proyecto.Entidades.Enfermedad;
import com.example.proyecto.Entidades.Medico;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListaMedicosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListaMedicosFragment extends Fragment implements  Response.Listener<JSONObject>,Response.ErrorListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ListView lista ;
    SweetAlertDialog dialogo;
    RequestQueue request;
    JsonObjectRequest jsonObjectRequest;

    public ListaMedicosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListaMedicosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListaMedicosFragment newInstance(String param1, String param2) {
        ListaMedicosFragment fragment = new ListaMedicosFragment();
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
        View vista = inflater.inflate(R.layout.fragment_lista_medicos, container, false);
        lista = vista.findViewById(R.id.listamedicos);
        llenar_lista();
        return vista;
    }

    private void llenar_lista() {

        dialogo = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        dialogo.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        dialogo.setTitleText("Espere ...");
        dialogo.setCancelable(true);
        dialogo.show();

        String url="https://dep2020.000webhostapp.com/listar_medicos.php";

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

        JSONArray json = response.optJSONArray("medico");
        JSONObject jsonObject = null;
        ArrayList<String> medicos = new ArrayList<>();

        for (int i = 0;i<json.length(); i++ ){
            Medico medico = new Medico();
            try {
                jsonObject = json.getJSONObject(i);
                medico.setCedula(jsonObject.optString("cedula"));
                medico.setNombre(jsonObject.optString("nombre_completo"));
                medico.setEdad(jsonObject.optString("edad"));
                medico.setEspecialidad(jsonObject.optString("especialidad"));
                medico.setEmail(jsonObject.optString("email"));
                medico.setClave(jsonObject.optString("password"));
                medico.setDireccion(jsonObject.optString("direccion"));
                medico.setTelefono(jsonObject.optString("telefono"));

                medicos.add("   Cedula: "+medico.getCedula()+"\n"+"   Nombre: "+medico.getNombre()+"\n"+"   Edad: "+medico.getEdad()+"años \n"+"   Especialidad: "+medico.getEspecialidad()+"\n"+"   Email: "+medico.getEmail()+"\n"+"   Telefono: "+medico.getTelefono()+"\n"+"   Direccion: "+medico.getDireccion());
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this.getContext(), R.layout.listview3, medicos);
        lista.setAdapter(adaptador);
    }
}