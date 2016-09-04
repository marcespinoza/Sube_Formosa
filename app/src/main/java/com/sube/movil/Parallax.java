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
public class Parallax extends ParallaxRecyclerAdapter implements Filterable {

    List <PuntoVenta> items, filterList;
    CustomFilter filter;

    public Parallax(List data) {
        super(data);
        items=data;
        filterList=data;
    }

    @Override
    public void onBindViewHolderImpl(RecyclerView.ViewHolder viewHolder, ParallaxRecyclerAdapter parallaxRecyclerAdapter, int i) {
        ((NoticiaViewHolder)viewHolder).titulo.setText(items.get(i).getTitle());
        ((NoticiaViewHolder)viewHolder).descripcion.setText(items.get(i).getDescription());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolderImpl(ViewGroup viewGroup, ParallaxRecyclerAdapter parallaxRecyclerAdapter, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.puntoventa_item_layout, viewGroup, false);
        return new NoticiaViewHolder(v,viewGroup.getContext());
    }

    @Override
    public int getItemCountImpl(ParallaxRecyclerAdapter parallaxRecyclerAdapter) {
        return items.size();
    }

    @Override
    public Filter getFilter() {
        if(filter==null)
        {
            filter=new CustomFilter(filterList,this);
        }
        return filter;
    }

}
