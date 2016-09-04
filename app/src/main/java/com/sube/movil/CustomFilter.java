package com.sube.movil;

/**
 * Created by Marcelo on 04/09/2016.
 */
import android.widget.Filter;

import com.poliveira.parallaxrecyclerview.ParallaxRecyclerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hp on 3/17/2016.
 */
public class CustomFilter extends Filter{
    Parallax adapter;
    List<PuntoVenta> filterList;
    public CustomFilter(List<PuntoVenta> filterList,Parallax adapter)
    {
        this.adapter=adapter;
        this.filterList=filterList;
    }
    //FILTERING OCURS
    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results=new FilterResults();
        //CHECK CONSTRAINT VALIDITY
        if(constraint != null && constraint.length() > 0)
        {
            //CHANGE TO UPPER
            constraint=constraint.toString().toUpperCase();
            //STORE OUR FILTERED PLAYERS
            ArrayList<PuntoVenta> filteredPlayers=new ArrayList<>();
            for (int i=0;i<filterList.size();i++)
            {
                //CHECK
                if(filterList.get(i).getDescription().toUpperCase().contains(constraint) || filterList.get(i).getTitle().toUpperCase().contains(constraint))
                {
                    //ADD PLAYER TO FILTERED PLAYERS
                    filteredPlayers.add(filterList.get(i));
                }
            }
            results.count=filteredPlayers.size();
            results.values=filteredPlayers;
        }else
        {
            results.count=filterList.size();
            results.values=filterList;
        }
        return results;
    }
    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.items= (ArrayList<PuntoVenta>) results.values;
        //REFRESH
        adapter.notifyDataSetChanged();
    }
}