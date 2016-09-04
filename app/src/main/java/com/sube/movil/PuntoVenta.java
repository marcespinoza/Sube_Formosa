package com.sube.movil;

/**
 * Created by jaltuzarra on 02/09/2016.
 */
public class PuntoVenta {

    String img;
    String title;
    String description;

    public PuntoVenta(){}

    public PuntoVenta(String img, String title, String description){
        this.img = img;
        this.title = title;
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getImg() {
        if (img != null){
            if (img.isEmpty())
                return "tes";
        }else{
            return "tes";
        }

        return img;
    }

    public String getDescription() {
        return description;
    }
}