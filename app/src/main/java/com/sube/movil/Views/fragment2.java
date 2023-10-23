package com.sube.movil.Views;


import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.sube.movil.Parallax;
import com.sube.movil.PuntoVenta;
import com.sube.movil.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Marcelo on 11/04/2015.
 */
public class fragment2 extends Fragment {

    List <PuntoVenta> items = new ArrayList();
    RecyclerView myRecycler;
    Parallax parallaxAdapter ;
    FloatingSearchView mSearchView;
    private List<PuntoVenta> orig;
    SharedPreferences prefs;
    String provincia_shared;
    String ciudad_shared;
    CircularProgressView progressView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment2, container, false);
        progressView = (CircularProgressView) rootView.findViewById(R.id.progress_view);
        progressView.startAnimation();
        mSearchView = (FloatingSearchView) rootView.findViewById(R.id.floating_search_view);
        myRecycler = (RecyclerView) rootView.findViewById(R.id.myRecycler);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity().getApplicationContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        myRecycler.setLayoutManager(manager);
        myRecycler.setHasFixedSize(true);
        prefs = getActivity().getSharedPreferences("ubicacion", MODE_PRIVATE);
        provincia_shared = prefs.getString("provincia", "");
        ciudad_shared = prefs.getString("ciudad", null);
        setAdapter();
        makeJsonArrayRequest();
        setupFloatingSearch();
        return rootView;
    }

    //Inicializo la barra de busqueda y configuro el filtro
    private void setupFloatingSearch() {
        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {

            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {

                if (!oldQuery.equals("") && newQuery.equals("")) {
                    mSearchView.clearSuggestions();
                } else {
                    parallaxAdapter.getFilter().filter(newQuery.toString());
                }
                parallaxAdapter.notifyDataSetChanged();
            }
        });}

        //Obtengo lista de puntos de ventas y recargas
        private void makeJsonArrayRequest() {
            String url = "https://api-subemovil.onrender.com/api/recargas";
            RequestQueue queue = Volley.newRequestQueue(getContext());
            JSONObject postData = new JSONObject();
            try {
                postData.put("provincia", provincia_shared);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest sr = new JsonObjectRequest(Request.Method.POST,url, postData, response -> {
                JSONArray jsonResponse = null;
                try {
                    jsonResponse = new JSONArray(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JSONObject jsonObject;
                progressView.stopAnimation();
                progressView.setVisibility(View.INVISIBLE);
                for(int i=0; i<response.length();i++){
                    /*PuntoVenta puntoventa = new PuntoVenta();
                    try {
                        jsonObject = response.getJSONObject(i);
                        puntoventa.setTitle(jsonObject.getString("direccion"));
                        puntoventa.setDescription(jsonObject.getString("horario"));
                        items.add(puntoventa);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/
                }
                parallaxAdapter.notifyDataSetChanged();
            }, error -> {
                if(error.getMessage()==null){
                    Toast.makeText(getContext(),"Falló la conexión, intente de nuevo", Toast.LENGTH_SHORT);
                }
                else{
                    Log.e("error",error.getMessage());}
            }){
                @Override
                protected Map<String,String> getParams(){
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("ciudad",ciudad_shared);
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("Content-Type","application/x-www-form-urlencoded");
                    return params;
                }
            };
            queue.add(sr);

    }

    private void setAdapter(){
        parallaxAdapter  = new Parallax(items) ;
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        myRecycler.setLayoutManager(mLayoutManager);
        myRecycler.setItemAnimator(new DefaultItemAnimator());
        myRecycler.setAdapter(parallaxAdapter);
    }

}
