package com.sube.movil.Interfaces;

/**
 * Created by Marceloi7 on 20/02/2018.
 */

public interface SaldoFragment {
    interface Model{
        void guardarUsuario(int tipo_documento, int usuario, String clave);
        void mostrarError();
    }
    interface Presenter{
        void guardarUsuario(int tipo_documento, int usuario, String clave);
        void mostrarError();
        void obtenerDatosLogin();
    }

    interface View{
        void cargarUsuario();
       void guardarUsuario();
        void mostrarError();
        void retornarUsuario(int tipo_documento, int usuario, String clave);
    }
}
