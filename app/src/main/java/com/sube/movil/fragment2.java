package com.sube.movil;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.crashlytics.android.Crashlytics;
import com.github.rahatarmanahmed.cpv.CircularProgressView;
import com.poliveira.parallaxrecyclerview.ParallaxRecyclerAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.R.attr.tag;
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
            String url = null;
            switch (provincia_shared){
                case "Buenos Aires": url = "http://subemovil.000webhostapp.com/private/buenos_aires.php"; break;
                case "Capital Federal": url = "http://subemovil.000webhostapp.com/private/capital_federal.php"; break;
                case "Catamarca": url = "http://subemovil.000webhostapp.com/private/catamarca.php"; break;
                case "Chaco": url = "http://subemovil.000webhostapp.com/private/chaco.php"; break;
                case "Chubut": url = "http://subemovil.000webhostapp.com/private/chubut.php"; break;
                case "Corrientes": url = "http://subemovil.000webhostapp.com/private/corrientes.php"; break;
                case "Entre rios": url = "http://subemovil.000webhostapp.com/private/entre_rios.php"; break;
                case "Formosa": url = "http://subemovil.000webhostapp.com/private/formosa.php"; break;
                case "Jujuy": url = "http://subemovil.000webhostapp.com/private/jujuy.php"; break;
                case "Neuquen": url = "http://subemovil.000webhostapp.com/private/neuquen.php"; break;
                case "Rio negro": url = "http://subemovil.000webhostapp.com/private/rio_negro.php"; break;
                case "San Juan": url = "http://subemovil.000webhostapp.com/private/san_juan.php"; break;
                case "San Luis": url = "http://subemovil.000webhostapp.com/private/san_luis.php"; break;
                case "Santa Fe": url = "http://subemovil.000webhostapp.com/private/santa_fe.php"; break;
                case "Tierra del Fuego": url = "http://subemovil.000webhostapp.com/private/tierra_del_fuego.php"; break;
                default: Toast.makeText(getActivity(),"Seleccione una provincia", Toast.LENGTH_SHORT);
            }
            RequestQueue queue = Volley.newRequestQueue(getContext());
            StringRequest sr = new StringRequest(Request.Method.POST,url, new Response.Listener<String>() {
                @Override
                public void onResponse(String respons) {
                    JSONArray response = null;
                    try {
                        response=new JSONArray(respons);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JSONObject jsonObject;
                    progressView.stopAnimation();
                    progressView.setVisibility(View.INVISIBLE);
                    for(int i=0; i<response.length();i++){
                        PuntoVenta puntoventa = new PuntoVenta();
                        try {
                            jsonObject = response.getJSONObject(i);
                            puntoventa.setTitle(jsonObject.getString("direccion"));
                            puntoventa.setDescription(jsonObject.getString("horario"));
                            items.add(puntoventa);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    parallaxAdapter.notifyDataSetChanged();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(error.getMessage()==null){
                        Toast.makeText(getContext(),"Falló la conexión, intente de nuevo", Toast.LENGTH_SHORT);
                        Crashlytics.log(0, "on error", "Volleyerror");
                    }
                    else{
                        Log.e("error",error.getMessage());}
                }
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
