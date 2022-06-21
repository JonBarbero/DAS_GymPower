package com.example.das_gympower.objetos;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class Fisico {

    private String peso;
    private String nombreFis;
    private String descFis;
    private String datoFis;
    private Bitmap imagenFis;
    private String rutaImagenFis;

    public Fisico(String peso, String nombre, String desc, String imagen){
        this.peso = peso;
        this.nombreFis = nombre;
        this.descFis = desc;
        this.rutaImagenFis = imagen;
    }

    public void setDatoFis(String dato) {
        this.datoFis = dato;

        try {
            byte[] byteCode= Base64.decode(dato,Base64.DEFAULT);

            int alto=100;//alto en pixeles
            int ancho=150;//ancho en pixeles

            Bitmap foto= BitmapFactory.decodeByteArray(byteCode,0,byteCode.length);
            this.imagenFis=Bitmap.createScaledBitmap(foto,alto,ancho,true);


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setPeso(String peso) {
        this.peso = peso;
    }

    public void setNombreFis(String nombreFis) {
        this.nombreFis = nombreFis;
    }

    public void setDescFis(String descFis) {
        this.descFis = descFis;
    }

    public void setImagenFis(Bitmap imagenFis) {
        this.imagenFis = imagenFis;
    }

    public void setRutaImagenFis(String rutaImagenFis) {
        this.rutaImagenFis = rutaImagenFis;
    }

    public String getPeso() {
        return peso;
    }

    public String getNombreFis() {
        return nombreFis;
    }

    public String getDescFis() {
        return descFis;
    }

    public String getDatoFis() {
        return datoFis;
    }

    public Bitmap getImagenFis() {
        return imagenFis;
    }

    public String getRutaImagenFis() {
        return rutaImagenFis;
    }
}
