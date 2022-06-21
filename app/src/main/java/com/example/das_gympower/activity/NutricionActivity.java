package com.example.das_gympower.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.das_gympower.R;
import com.example.das_gympower.adapters.AdapterNutricion;
import com.example.das_gympower.objetos.Alimento;
import com.example.das_gympower.workers.ObtenerNutricionDB;

import java.util.ArrayList;
import java.util.List;

public class NutricionActivity extends AppCompatActivity {

    private ProgressDialog progreso;
    private List<Alimento> alimentos = new ArrayList<Alimento>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutricion);

        ListView comida = (ListView) findViewById(R.id.lv1);

        //Adapter de nutrición
        AdapterNutricion elAdaptador = new AdapterNutricion(getApplicationContext(), alimentos);
        comida.setAdapter(elAdaptador);

    }

    @Override
    //Al darle atrás vuelve al menu principal
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
        progreso.setMessage("Obteniendo los alimentos del servidor...");
        progreso.show();
        progreso.hide();

        Data datos = new Data.Builder()
                .putString("obtener", "todo")
                .build();

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ObtenerNutricionDB.class).setInputData(datos).build();
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, workInfo -> {
                    if (workInfo != null && workInfo.getState().isFinished()) {
                        if (workInfo.getState().equals(WorkInfo.State.SUCCEEDED)) {
                            if (workInfo.getOutputData().getString("alimentos").equals("error")) {
                                Toast toast = Toast.makeText(getApplicationContext(), "En datos devuelve error", Toast.LENGTH_SHORT);
                                toast.show();
                                progreso.hide();
                            } else if (workInfo.getOutputData().getString("alimentos").equals("no hay alimentos")) {
                                Toast toast = Toast.makeText(getApplicationContext(), "No hay alimentos", Toast.LENGTH_SHORT);
                                toast.show();
                            } else{
                                String resultados = workInfo.getOutputData().getString("alimentos");
                                String[] resultSeparados;

                                resultSeparados = resultados.split("],");

                                String[] resultDesc = resultSeparados[0].split("\":");
                                String[] resultMacronutriente= resultSeparados[1].split("\":");
                                String[] resultURL = resultSeparados[2].split("\":");
                                String[] resultNombre = resultSeparados[3].split("\":");

                                StringBuilder de = new StringBuilder(resultDesc[1]);
                                StringBuilder em = new StringBuilder(resultMacronutriente[1]);
                                StringBuilder ur = new StringBuilder(resultURL[1]);
                                StringBuilder nombre = new StringBuilder(resultNombre[1]);

                                de.deleteCharAt(0);
                                de.deleteCharAt(0);
                                resultDesc = de.toString().split("\",\"");

                                em.deleteCharAt(0);
                                em.deleteCharAt(0);
                                em.deleteCharAt(em.length()-1);
                                resultMacronutriente = em.toString().split("\",\"");

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

                                ListView comida = (ListView) findViewById(R.id.lv1);

                                alimentos.clear();

                                int j=resultURL.length-1;

                                for (int i=0;i<resultURL.length;i++){
                                    Alimento newAli=null;
                                    if (j==i){
                                        String resultDesc1=resultDesc[i].substring(0, resultDesc[i].length()-1);
                                        newAli = new Alimento(resultMacronutriente[i], resultNombre[i], resultDesc1, resultURL[i]);
                                    }
                                    else{
                                        newAli = new Alimento(resultMacronutriente[i], resultNombre[i], resultDesc[i], resultURL[i]);
                                    }
                                    alimentos.add(newAli);
                                }

                                AdapterNutricion elAdaptador = new AdapterNutricion(getApplicationContext(), alimentos);
                                comida.setAdapter(elAdaptador);
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