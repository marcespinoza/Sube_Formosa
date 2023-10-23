package com.sube.movil;

import android.content.Context;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Marcelo on 04/09/2016.
 */
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