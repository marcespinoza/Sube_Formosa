package com.sube.movil.Interfaces;

public interface CargaSaldoInterface {

    interface Model {
        void guardarMonto();
    }

    interface Presenter{
        void enviarMonto(int monto);
    }

    interface View{
        void obtenerMonto();
    }

}
