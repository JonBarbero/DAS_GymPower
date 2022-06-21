package com.example.das_gympower.objetos;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

public class Alimento {

    private String macroAli;
    private String nombreAli;
    private String descAli;
    private String datoAli;
    private Bitmap imagenAli;
    private String rutaImagenAli;

    public Alimento(String macro, String nombre, String desc, String imagen){
        this.macroAli = macro;
        this.nombreAli = nombre;
        this.descAli = desc;
        this.rutaImagenAli = imagen;
    }

    public void setDatoAli(String dato) {
        this.datoAli = dato;

        try {
            byte[] byteCode= Base64.decode(dato,Base64.DEFAULT);

            int alto=100;//alto en pixeles
            int ancho=150;//ancho en pixeles

            Bitmap imagen= BitmapFactory.decodeByteArray(byteCode,0,byteCode.length);
            this.imagenAli=Bitmap.createScaledBitmap(imagen,alto,ancho,true);


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setMacroAli(String macroAli) {
        this.macroAli = macroAli;
    }

    public void setNombreAli(String nombreAli) {
        this.nombreAli = nombreAli;
    }

    public void setDescAli(String descAli) {
        this.descAli = descAli;
    }

    public void setImagenAli(Bitmap imagenAli) {
        this.imagenAli = imagenAli;
    }

    public void setRutaImagenAli(String rutaImagenAli) {
        this.rutaImagenAli = rutaImagenAli;
    }

    public String getMacroAli() {
        return macroAli;
    }

    public String getNombreAli() {
        return nombreAli;
    }

    public String getDescAli() {
        return descAli;
    }

    public String getDatoAli() {
        return datoAli;
    }

    public Bitmap getImagenAli() {
        return imagenAli;
    }

    public String getRutaImagenAli() {
        return rutaImagenAli;
    }
}
