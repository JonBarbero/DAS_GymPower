package com.example.das_gympower.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.das_gympower.R;
import com.example.das_gympower.adapters.AdapterEntrenamiento;
import com.example.das_gympower.adapters.AdapterNutricion;
import com.example.das_gympower.objetos.Alimento;
import com.example.das_gympower.objetos.Ejercicio;
import com.example.das_gympower.workers.ObtenerEntrenamientoDB;

import java.util.ArrayList;
import java.util.List;

public class EntrenamientoActivity extends AppCompatActivity {

    private ProgressDialog progreso;
    private List<Ejercicio> ejercicios = new ArrayList<Ejercicio>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrenamiento);

        ListView entrenamiento = (ListView) findViewById(R.id.lv2);

        //Creamos el adapter de entrenamiento
        AdapterEntrenamiento elAdaptador = new AdapterEntrenamiento(getApplicationContext(), ejercicios);
        entrenamiento.setAdapter(elAdaptador);
    }

    @Override
    //Al darle atrás vuelve al menu inicial
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            Intent intent = new Intent(getApplicationContext(), com.example.das_gympower.activity.MainMenuActivity.class);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarWebService();
    }

    private void cargarWebService() {
        progreso = new ProgressDialog(this);
        progreso.setMessage("Obteniendo los ejercicios del servidor...");
        progreso.show();
        progreso.hide();

        Data datos = new Data.Builder()
                .putString("obtener", "todo")
                .build();

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ObtenerEntrenamientoDB.class).setInputData(datos).build();
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, workInfo -> {
                    if (workInfo != null && workInfo.getState().isFinished()) {
                        if (workInfo.getState().equals(WorkInfo.State.SUCCEEDED)) {
                            if (workInfo.getOutputData().getString("ejercicios").equals("error")) {
                                Toast toast = Toast.makeText(getApplicationContext(), "En datos devuelve error", Toast.LENGTH_SHORT);
                                toast.show();
                                progreso.hide();
                            } else if (workInfo.getOutputData().getString("ejercicios").equals("no hay ejercicios")) {
                                Toast toast = Toast.makeText(getApplicationContext(), "No hay ejercicios", Toast.LENGTH_SHORT);
                                toast.show();
                            } else{
                                String resultados = workInfo.getOutputData().getString("ejercicios");
                                String[] resultSeparados;

                                resultSeparados = resultados.split("],");

                                String[] resultDesc = resultSeparados[0].split("\":");
                                String[] resultMusculo= resultSeparados[1].split("\":");
                                String[] resultURL = resultSeparados[2].split("\":");
                                String[] resultNombre = resultSeparados[3].split("\":");

                                StringBuilder de = new StringBuilder(resultDesc[1]);
                                StringBuilder em = new StringBuilder(resultMusculo[1]);
                                StringBuilder ur = new StringBuilder(resultURL[1]);
                                StringBuilder nombre = new StringBuilder(resultNombre[1]);

                                de.deleteCharAt(0);
                                de.deleteCharAt(0);
                                resultDesc = de.toString().split("\",\"");

                                em.deleteCharAt(0);
                                em.deleteCharAt(0);
                                em.deleteCharAt(em.length()-1);
                                resultMusculo = em.toString().split("\",\"");

                                ur.deleteCharAt(0);
                                ur.deleteCharAt(0);
                                ur.deleteCharAt(ur.length()-1);
                                resultURL = ur.toString().split("\",\"");

                                nombre.deleteCharAt(0);
                                nombre.deleteCharAt(0);
                                nombre.deleteCharAt(nombre.length()-1);
                                nombre.deleteCharAt(nombre.length()-1);
                                nombre.deleteCharAt(nombre.length()-1);
                                resultNombre = nombre.toString().split("\",\"");

                                ListView entrenamiento = (ListView) findViewById(R.id.lv2);

                                ejercicios.clear();

                                int j=resultURL.length-1;

                                for (int i=0;i<resultURL.length;i++){
                                    Ejercicio newEjer;
                                    if (j==i){
                                        String resultDesc1=resultDesc[i].substring(0, resultDesc[i].length()-1);
                                        newEjer = new Ejercicio(resultMusculo[i], resultNombre[i], resultDesc1, resultURL[i]);
                                    }
                                    else{
                                        newEjer = new Ejercicio(resultMusculo[i], resultNombre[i], resultDesc[i], resultURL[i]);
                                    }
                                    ejercicios.add(newEjer);
                                }

                                AdapterEntrenamiento elAdaptador = new AdapterEntrenamiento(getApplicationContext(), ejercicios);
                                entrenamiento.setAdapter(elAdaptador);
                                progreso.hide();
                                progreso.dismiss();
                            }
                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(), "Algo ha ido mal", Toast.LENGTH_SHORT);
                            toast.show();
                            progreso.hide();
                        }
                    }
                });

        WorkManager.getInstance(this).enqueue(otwr);
    }
}