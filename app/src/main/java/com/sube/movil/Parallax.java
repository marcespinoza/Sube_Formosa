package com.sube.movil;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.poliveira.parallaxrecyclerview.ParallaxRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Marcelo on 04/09/2016.
 */
public class Parallax extends RecyclerView.Adapter<NoticiaViewHolder> implements Filterable {

    List <PuntoVenta> items, filterList;
    CustomFilter filter;

    public Parallax(List <PuntoVenta> items) {
        this.items=items;
        filterList=items;
    }



    @Override
    public Filter getFilter() {
        if(filter==null)
        {
            filter=new CustomFilter(filterList,this);
        }
        return filter;
    }

    @Override
    public NoticiaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.puntoventa_item_layout, parent, false);
        return new NoticiaViewHolder(v,parent.getContext());
    }

    @Override
    public void onBindViewHolder(NoticiaViewHolder viewHolder, int i) {
        ((NoticiaViewHolder)viewHolder).titulo.setText(items.get(i).getTitle());
        ((NoticiaViewHolder)viewHolder).descripcion.setText(items.get(i).getDescription());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
