package com.sube.movil;

import android.location.Location;

import java.util.Comparator;

/**
 * Created by Marcelo on 12/03/2017.
 */

public class Marker {


    String horario;
    Location ubicacion;
    String direccion;

    public Marker(String horario, Location ubicacion, String direccion) {
        this.horario = horario;
        this.ubicacion = ubicacion;
        this.direccion = direccion;
    }

    public Marker() {
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }


    public Location getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(Location ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }


}
