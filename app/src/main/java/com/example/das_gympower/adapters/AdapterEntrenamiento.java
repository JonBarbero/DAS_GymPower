package com.example.das_gympower.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageRequest;
import com.example.das_gympower.R;
import com.example.das_gympower.objetos.Ejercicio;
import com.example.das_gympower.objetos.VolleySingleton;

import java.util.List;


public class AdapterEntrenamiento extends BaseAdapter {

    private List<Ejercicio> ejercicios;
    Context contexto;
    LayoutInflater inflater;

    public AdapterEntrenamiento(Context contexto, List<Ejercicio> ejercicios) {
        this.contexto = contexto;
        this.inflater = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.ejercicios = ejercicios;
    }

    @Override
    //Devuelve la cantidad de ejercicios
    public int getCount() {
        return ejercicios.size();
    }

    @Override
    public Ejercicio getItem(int i) {
        return ejercicios.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view=inflater.inflate(R.layout.ejer_item,null);

        TextView tvNombreEjer= (TextView) view.findViewById(R.id.nombreEjer);
        TextView tvDescEjer=(TextView) view.findViewById(R.id.descEjer);
        TextView tvmusculo= (TextView) view.findViewById(R.id.musculo);

        tvNombreEjer.setText(ejercicios.get(i).getNombreEjer());
        tvDescEjer.setText(ejercicios.get(i).getDescEjer());
        tvmusculo.setText(ejercicios.get(i).getMusculoEjer());

        if (ejercicios.get(i).getRutaImagenEjer()!=null){
            cargarImagenWebService(ejercicios.get(i).getRutaImagenEjer(), view);
        }
        return view;
    }

    private void cargarImagenWebService(String rutaImagen, View view) {

        String urlImagen=rutaImagen;
        urlImagen= urlImagen.replace("\\/","/");
        ImageRequest imageRequest=new ImageRequest(urlImagen, response -> {
            ImageView ivImagen= (ImageView) view.findViewById(R.id.idImagen2);
            ivImagen.setImageBitmap(response);
        }, 0, 0, ImageView.ScaleType.CENTER, null, error -> Log.d("Url consultada: ", error.toString()));
        //request.add(imageRequest);
        VolleySingleton.getIntanciaVolley(contexto).addToRequestQueue(imageRequest);
    }

}
