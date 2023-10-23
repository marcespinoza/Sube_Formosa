package com.sube.movil.Presenters;

import android.content.Context;
import android.content.SharedPreferences;

import com.sube.movil.Interfaces.SaldoFragment;
import com.sube.movil.Model.DatosLoginModel;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Marceloi7 on 20/02/2018.
 */

public class DatosLoginPresenter implements SaldoFragment.Presenter {

    private SaldoFragment.View view;
    private SaldoFragment.Model model;
    private Context context;
    public static final String DATOS_LOGIN = "datos_login";

    public DatosLoginPresenter(SaldoFragment.View view, Context context) {
        this.view=view;
        this.context = context;
        model = new DatosLoginModel(this, context);
    }

    @Override
    public void guardarUsuario(int tipo_documento, int usuario, String clave) {
          model.guardarUsuario(tipo_documento, usuario, clave);
    }

    @Override
    public void mostrarError() {
        model.mostrarError();
    }

    @Override
    public void obtenerDatosLogin() {
        SharedPreferences prefs = context.getSharedPreferences(DATOS_LOGIN, MODE_PRIVATE);
        int tipo_documento = prefs.getInt("tipo_documento",0);
        int usuario = prefs.getInt("usuario",0);
        String clave = prefs.getString("clave", null);
        if (usuario != 0 && clave != null) {
           view.retornarUsuario(tipo_documento, usuario, clave);
        }
    }
}
