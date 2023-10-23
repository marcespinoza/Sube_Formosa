package com.sube.movil.Model;

import android.content.Context;
import android.content.SharedPreferences;

import com.sube.movil.Interfaces.SaldoFragment;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Marceloi7 on 20/02/2018.
 */

public class DatosLoginModel implements SaldoFragment.Model {

    private SaldoFragment.Presenter presenter;
    private Context context;
    public static final String DATOS_LOGIN = "datos_login";

    public DatosLoginModel(SaldoFragment.Presenter presenter, Context context) {
        this.presenter=presenter;
        this.context = context;
    }

    @Override
    public void guardarUsuario(int tipo_documento,int usuario, String clave) {
        if(usuario!=0 && clave!=null){
         SharedPreferences.Editor editor = context.getSharedPreferences(DATOS_LOGIN,MODE_PRIVATE).edit();
         editor.putInt("tipo_documento",tipo_documento-1);
         editor.putInt("usuario", usuario);
         editor.putString("clave", clave);
         editor.apply();
        }else{
            mostrarError();
        }
    }

    @Override
    public void mostrarError() {
        presenter.mostrarError();
    }

}
