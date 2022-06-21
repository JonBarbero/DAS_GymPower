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
import com.example.das_gympower.objetos.Alimento;
import com.example.das_gympower.objetos.VolleySingleton;

import java.util.List;


public class AdapterNutricion extends BaseAdapter {

    private List<Alimento> alimentos;
    Context contexto;
    LayoutInflater inflater;

    public AdapterNutricion(Context contexto, List<Alimento> alimentos) {
        this.contexto = contexto;
        this.inflater = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.alimentos = alimentos;
    }

    @Override
    //Devuelve la cantidad de alimentos de la lista
    public int getCount() {
        return alimentos.size();
    }

    @Override
    public Alimento getItem(int i) {
        return alimentos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view=inflater.inflate(R.layout.ali_item,null);
        TextView tvNombre= (TextView) view.findViewById(R.id.nombreAli);
        TextView tvDesc=(TextView) view.findViewById(R.id.descAli);
        TextView tvmacro= (TextView) view.findViewById(R.id.macronutriente);
        tvNombre.setText(alimentos.get(i).getNombreAli());
        tvmacro.setText(alimentos.get(i).getMacroAli());
        tvDesc.setText(alimentos.get(i).getDescAli());

        if (alimentos.get(i).getRutaImagenAli()!=null){
            cargarImagenWebService(alimentos.get(i).getRutaImagenAli(), view);
        }
        return view;
    }

    private void cargarImagenWebService(String rutaImagen, View view) {

        String urlImagen=rutaImagen;
        urlImagen= urlImagen.replace("\\/","/");
        ImageRequest imageRequest=new ImageRequest(urlImagen, response -> {
            ImageView ivImagen= (ImageView) view.findViewById(R.id.idImagen);
            ivImagen.setImageBitmap(response);
        }, 0, 0, ImageView.ScaleType.CENTER, null, error -> Log.d("Url consultada: ", error.toString()));
        //request.add(imageRequest);
        VolleySingleton.getIntanciaVolley(contexto).addToRequestQueue(imageRequest);
    }

}