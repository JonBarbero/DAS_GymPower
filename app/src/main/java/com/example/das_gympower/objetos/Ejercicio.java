package com.example.das_gympower.objetos;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class Ejercicio {

    private String musculoEjer;
    private String nombreEjer;
    private String descEjer;
    private String datoEjer;
    private Bitmap imagenEjer;
    private String rutaImagenEjer;

    public Ejercicio(String musculoEjer, String nombreEjer, String descEjer, String imagen){
        this.musculoEjer = musculoEjer;
        this.nombreEjer = nombreEjer;
        this.descEjer = descEjer;
        this.rutaImagenEjer = imagen;
    }

    public void setDatoEjer(String dato) {
        this.datoEjer = dato;

        try {
            byte[] byteCode= Base64.decode(dato,Base64.DEFAULT);

            int alto=100;//alto en pixeles
            int ancho=150;//ancho en pixeles

            Bitmap imagen= BitmapFactory.decodeByteArray(byteCode,0,byteCode.length);
            this.imagenEjer=Bitmap.createScaledBitmap(imagen,alto,ancho,true);


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setMusculoEjer(String musculoEjer) {
        this.musculoEjer = musculoEjer;
    }

    public void setNombreEjer(String nombreEjer) {
        this.nombreEjer = nombreEjer;
    }

    public void setDescEjer(String descEjer) {
        this.descEjer = descEjer;
    }

    public void setImagenEjer(Bitmap imagenEjer) {
        this.imagenEjer = imagenEjer;
    }

    public void setRutaImagenEjer(String rutaImagenEjer) {
        this.rutaImagenEjer = rutaImagenEjer;
    }

    public String getMusculoEjer() {
        return musculoEjer;
    }

    public String getNombreEjer() {
        return nombreEjer;
    }

    public String getDescEjer() {
        return descEjer;
    }

    public String getDatoEjer() {
        return datoEjer;
    }

    public Bitmap getImagenEjer() {
        return imagenEjer;
    }

    public String getRutaImagenEjer() {
        return rutaImagenEjer;
    }
}