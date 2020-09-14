package com.example.proyecto;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.proyecto.Entidades.sintoma;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ListaSintomasFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListaSintomasFragment extends Fragment implements  Response.Listener<JSONObject>,Response.ErrorListener {

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
    public ListaSintomasFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListaSintomasFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListaSintomasFragment newInstance(String param1, String param2) {
        ListaSintomasFragment fragment = new ListaSintomasFragment();
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
        View vista = inflater.inflate(R.layout.fragment_lista_sintomas, container, false);
        lista = vista.findViewById(R.id.lista);
        llenar_lista();
        return vista;
    }

    private void llenar_lista() {
        dialogo = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        dialogo.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        dialogo.setTitleText("Espere ...");
        dialogo.setCancelable(true);
        dialogo.show();

        String url="https://dep2020.000webhostapp.com/listar_sintomas.php";

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

        JSONArray json = response.optJSONArray("sintoma");
        JSONObject jsonObject = null;
        ArrayList<String> sintomas = new ArrayList<>();

        for (int i = 0;i<json.length(); i++ ){
            sintoma sintoma = new sintoma();
            try {
                jsonObject = json.getJSONObject(i);
                sintoma.setCodigo(jsonObject.optString("codigo"));
                sintoma.setDescripcion(jsonObject.optString("descripcion"));
                sintomas.add("  "+sintoma.getCodigo()+"                         "+sintoma.getDescripcion());
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        ArrayAdapter<String> adaptador = new ArrayAdapter<String>(this.getContext(), R.layout.lisview2, sintomas);
        lista.setAdapter(adaptador);
    }
}