package com.sube.movil;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.poliveira.parallaxrecyclerview.ParallaxRecyclerAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    String restoredText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment2, container, false);
        mSearchView = (FloatingSearchView) rootView.findViewById(R.id.floating_search_view);
        myRecycler = (RecyclerView) rootView.findViewById(R.id.myRecycler);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity().getApplicationContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        myRecycler.setLayoutManager(manager);
        myRecycler.setHasFixedSize(true);
        prefs = getActivity().getSharedPreferences("ubicacion", MODE_PRIVATE);
        restoredText = prefs.getString("provincia", null);
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
            String url;
            if(restoredText.equals("Chaco")){
                url = "http://subemovil.000webhostapp.com/private/chaco.php";}
            else{
                url = "http://subemovil.000webhostapp.com/private/formosa.php";
            }
            Volley.newRequestQueue(getContext()).add(
                new JsonRequest<JSONArray>(Request.Method.POST, url, null,
                        new Response.Listener<JSONArray>() {
                            JSONObject jsonObject;

                            @Override
                            public void onResponse(JSONArray response) {
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

                    }
                }) {

                    @Override
                    protected Response<JSONArray> parseNetworkResponse(
                            NetworkResponse response) {
                        try {
                            String jsonString = new String(response.data,
                                    HttpHeaderParser
                                            .parseCharset(response.headers));
                            return Response.success(new JSONArray(jsonString),
                                    HttpHeaderParser
                                            .parseCacheHeaders(response));
                        } catch (UnsupportedEncodingException e) {
                            return Response.error(new ParseError(e));
                        } catch (JSONException je) {
                            return Response.error(new ParseError(je));
                        }
                    }
                });
    }

    private void setAdapter(){
        parallaxAdapter  = new Parallax(items) ;
        parallaxAdapter .setParallaxHeader(LayoutInflater.from(getActivity()).inflate(R.layout.parallaxheader, myRecycler, false), myRecycler);
        parallaxAdapter .setOnParallaxScroll(new ParallaxRecyclerAdapter.OnParallaxScroll() {
            @Override
            public void onParallaxScroll(float percentage, float offset, View parallax) {

            }
        });
        myRecycler.setAdapter(parallaxAdapter);
    }

}
