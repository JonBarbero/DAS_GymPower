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
import com.example.das_gympower.objetos.Fisico;
import com.example.das_gympower.objetos.VolleySingleton;

import java.util.List;


public class AdapterFisico extends BaseAdapter {

    private List<Fisico> fisicos;
    Context contexto;
    LayoutInflater inflater;

    public AdapterFisico(Context contexto, List<Fisico> fisicos) {
        this.contexto = contexto;
        this.inflater = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.fisicos = fisicos;
    }

    @Override
    public int getCount() {
        return fisicos.size();
    }

    @Override
    public Fisico getItem(int i) {
        return fisicos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view=inflater.inflate(R.layout.fisico_item,null);
        TextView tvNombre= (TextView) view.findViewById(R.id.nombreFisico);
        TextView tvDesc=(TextView) view.findViewById(R.id.descFisico);
        TextView tvPeso= (TextView) view.findViewById(R.id.peso);
        ImageView ivFoto= (ImageView) view.findViewById(R.id.idImagen3);
        tvNombre.setText(fisicos.get(i).getNombreFis());
        tvPeso.setText(fisicos.get(i).getPeso());
        tvDesc.setText(fisicos.get(i).getDescFis());

        if (fisicos.get(i).getRutaImagenFis()!=null){
            cargarImagenWebService(fisicos.get(i).getRutaImagenFis(), view);
        }else{
            ivFoto.setImageResource(R.drawable.mas);
        }

        return view;
    }

    private void cargarImagenWebService(String rutaImagen, View view) {


        String urlImagen=rutaImagen;
        urlImagen= urlImagen.replace("\\/","/");
        ImageRequest imageRequest=new ImageRequest(urlImagen, response -> {
            ImageView ivFoto= (ImageView) view.findViewById(R.id.idImagen3);
            ivFoto.setImageBitmap(response);
        }, 0, 0, ImageView.ScaleType.CENTER, null, error -> Log.d("Url consultada: ", error.toString()));
        //request.add(imageRequest);
        VolleySingleton.getIntanciaVolley(contexto).addToRequestQueue(imageRequest);
    }

}