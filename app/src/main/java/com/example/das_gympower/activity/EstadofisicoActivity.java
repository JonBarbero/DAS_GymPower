package com.example.das_gympower.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.das_gympower.R;
import com.example.das_gympower.adapters.AdapterFisico;
import com.example.das_gympower.objetos.Alimento;
import com.example.das_gympower.objetos.Fisico;
import com.example.das_gympower.workers.ObtenerFisicoDB;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class EstadofisicoActivity extends AppCompatActivity {

    private ProgressDialog progreso;
    private List<Fisico> fisicos = new ArrayList<Fisico>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estadofisico);

        FloatingActionButton sum = findViewById(R.id.sum);
        sum.setOnClickListener(view -> toAddFisico(getCurrentFocus()));

        ListView estado = (ListView) findViewById(R.id.lv3);

        AdapterFisico elAdaptador = new AdapterFisico(getApplicationContext(), fisicos);
        estado.setAdapter(elAdaptador);

    }

    private void toAddFisico(View currentFocus) {
        Intent intent = new Intent(getApplicationContext(), com.example.das_gympower.activity.InsertarfisicoActivity.class);
        startActivity(intent);
    }

    @Override
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

        FloatingActionButton sum = findViewById(R.id.sum);
        sum.setOnClickListener(view -> toAddFisico(getCurrentFocus()));
    }

    private void cargarWebService() {
        progreso = new ProgressDialog(this);
        progreso.setMessage("Obteniendo los físicos del servidor...");
        progreso.show();
        progreso.hide();

        Data datos = new Data.Builder()
                .putString("obtener", "todo")
                .build();

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ObtenerFisicoDB.class).setInputData(datos).build();
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, workInfo -> {
                    if (workInfo != null && workInfo.getState().isFinished()) {
                        if (workInfo.getState().equals(WorkInfo.State.SUCCEEDED)) {
                            if (workInfo.getOutputData().getString("fisicos").equals("error")) {
                                Toast toast = Toast.makeText(getApplicationContext(), "En datos devuelve error", Toast.LENGTH_SHORT);
                                toast.show();
                                progreso.hide();
                            } else if (workInfo.getOutputData().getString("fisicos").equals("no hay fisicos")) {
                                Toast toast = Toast.makeText(getApplicationContext(), "No hay fisicos", Toast.LENGTH_SHORT);
                                toast.show();
                            } else{
                                String resultados = workInfo.getOutputData().getString("fisicos");
                                String[] resultSeparados;

                                resultSeparados = resultados.split("],");

                                String[] resultDesc = resultSeparados[0].split("\":");
                                String[] resultPeso= resultSeparados[1].split("\":");
                                String[] resultURL = resultSeparados[2].split("\":");
                                String[] resultNombre = resultSeparados[3].split("\":");

                                StringBuilder de = new StringBuilder(resultDesc[1]);
                                StringBuilder em = new StringBuilder(resultPeso[1]);
                                StringBuilder ur = new StringBuilder(resultURL[1]);
                                StringBuilder nombre = new StringBuilder(resultNombre[1]);

                                de.deleteCharAt(0);
                                de.deleteCharAt(0);
                                resultDesc = de.toString().split("\",\"");

                                em.deleteCharAt(0);
                                em.deleteCharAt(0);
                                em.deleteCharAt(em.length()-1);
                                resultPeso = em.toString().split("\",\"");

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

                                ListView estado = (ListView) findViewById(R.id.lv3);

                                fisicos.clear();

                                int j=resultURL.length-1;

                                for (int i=0;i<resultURL.length;i++){
                                    Fisico newFis;
                                    if (j==i){
                                        String resultDesc1=resultDesc[i].substring(0, resultDesc[i].length()-1);
                                        newFis = new Fisico(resultPeso[i], resultNombre[i], resultDesc1, resultURL[i]);
                                    }
                                    else{
                                        newFis = new Fisico(resultPeso[i], resultNombre[i], resultDesc[i], resultURL[i]);
                                    }
                                    fisicos.add(newFis);
                                }

                                AdapterFisico elAdaptador = new AdapterFisico(getApplicationContext(), fisicos);
                                estado.setAdapter(elAdaptador);
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