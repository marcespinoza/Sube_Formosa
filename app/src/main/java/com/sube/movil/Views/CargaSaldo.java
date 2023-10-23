package com.sube.movil.Views;

import android.graphics.Typeface;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.arlib.floatingsearchview.util.adapter.TextWatcherAdapter;
import com.sube.movil.Interfaces.CargaSaldoInterface;
import com.sube.movil.Presenters.CargaSaldoPresenter;
import com.sube.movil.R;

import java.math.BigDecimal;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by Marceloi7 on 05/06/2018.
 */

public class CargaSaldo extends Fragment implements CargaSaldoInterface.View{

    TextView aviso;
    FancyButton continuar;
    EditText montoEditText;
    private CargaSaldoInterface.Presenter presenter;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.carga_saldo, container, false);
        Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.roboto_light);
        montoEditText = rootView.findViewById(R.id.monto);
        montoEditText.addTextChangedListener(new TextWatcherAdapter() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                super.onTextChanged(s, start, before, count);
                  if(count>0){
                    continuar.setEnabled(true);
                  }else{
                    continuar.setEnabled(false);
                  }
               }
        });
        continuar = rootView.findViewById(R.id.continuar);
        continuar.setEnabled(false);
        continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                obtenerMonto();
            }
        });
        presenter = new CargaSaldoPresenter();
        aviso = rootView.findViewById(R.id.aviso_carga_saldo);
        aviso.setTypeface(typeface);
        return rootView;
    }

    @Override
    public void obtenerMonto() {
        int monto =  Integer.parseInt(montoEditText.getText().toString());
        if(monto==0){
            View v = getActivity().findViewById(android.R.id.content);
            Snackbar snackbar = Snackbar.make(v,"Ingrese un monto mayor a cero", Snackbar.LENGTH_LONG);
            snackbar.getView().setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.material_deep_orange_400));
            snackbar.show();
        }else{
            presenter.enviarMonto(monto);
        }
    }
}
