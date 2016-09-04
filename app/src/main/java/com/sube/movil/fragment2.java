package com.sube.movil;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.arlib.floatingsearchview.FloatingSearchView;
import com.poliveira.parallaxrecyclerview.ParallaxRecyclerAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marcelo on 11/04/2015.
 */
public class fragment2 extends Fragment {

    List <PuntoVenta> items = new ArrayList();
    RecyclerView myRecycler;
    Parallax parallaxAdapter ;
    FloatingSearchView mSearchView;
    private List<PuntoVenta> orig;

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
        parallaxAdapter  = new Parallax(items) ;

        parallaxAdapter .setParallaxHeader(LayoutInflater.from(getActivity()).inflate(R.layout.parallaxheader, myRecycler, false), myRecycler);
        parallaxAdapter .setOnParallaxScroll(new ParallaxRecyclerAdapter.OnParallaxScroll() {
            @Override
            public void onParallaxScroll(float percentage, float offset, View parallax) {

            }
        });
        setupFloatingSearch();
        myRecycler.setAdapter(parallaxAdapter);
        return rootView;}



    private void setupFloatingSearch() {
        mSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {

            @Override
            public void onSearchTextChanged(String oldQuery, final String newQuery) {

                if (!oldQuery.equals("") && newQuery.equals("")) {
                    mSearchView.clearSuggestions();
                } else {
                    parallaxAdapter.getFilter().filter(newQuery.toString());
                }

                Log.i("","pnyyyy"+newQuery);
            }
        });}

    void getList(JSONArray list){
        if(list!=null) {
            for (int i = 0; i < list.length(); i++) {
                PuntoVenta puntoventa = new PuntoVenta();
                JSONObject json_data;
                try {
                    json_data = list.getJSONObject(i);
                    puntoventa.setTitle(json_data.getString("direccion"));
                    puntoventa.setDescription(json_data.getString("horario"));
                    items.add(puntoventa);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }
    }



        public  class NoticiaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Campos respectivos de un item
        public View view;
        private Context context;
        public TextView titulo;
        public TextView descripcion;
        CardView cardview;

        public NoticiaViewHolder(View v, Context context) {
            super(v);
            this.context = context;
            cardview =  (CardView) v.findViewById(R.id.card_view);
            cardview.setOnClickListener(this);
            titulo = (TextView) v.findViewById(R.id.txt_title);
            descripcion = (TextView) v.findViewById(R.id.txt_description);
        }

        @Override
        public void onClick(View v) {
        }


        }

}
