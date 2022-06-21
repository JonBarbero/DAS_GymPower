package com.example.das_gympower.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.das_gympower.R;
import com.example.das_gympower.workers.ComprobarUsuarioDB;
import com.example.das_gympower.workers.RegistroDB;

import org.json.JSONObject;


public class RegisterActivity extends AppCompatActivity implements Response.ErrorListener{

    private EditText us, con, correo;
    private ProgressDialog progreso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        us = (EditText) findViewById(R.id.usuario);
        correo = (EditText) findViewById(R.id.correoelectronico);
        con = (EditText) findViewById(R.id.contra);

        RequestQueue request = Volley.newRequestQueue(this);

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Cuando le das para atrás vuelve a la pantalla de Login
        if (keyCode == event.KEYCODE_BACK) {
            Intent intent = new Intent(getApplicationContext(), com.example.das_gympower.activity.LoginActivity.class);
            startActivity(intent);
        }

        return super.onKeyDown(keyCode, event);
    }

    public void registrar(View view){
        progreso = new ProgressDialog(this);
        progreso.setMessage("Realizando el registro...");
        progreso.show();
        String us1 = us.getText().toString();
        String con1 = con.getText().toString();
        String correo1 = correo.getText().toString();


        Log.d("Usuario que mete en datos ", us1);
        Data datos = new Data.Builder()
                .putString("email", correo1)
                .build();

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ComprobarUsuarioDB.class).setInputData(datos).build();
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, workInfo -> {
                    if (workInfo != null && workInfo.getState().isFinished()) {
                        if (workInfo.getState().equals(WorkInfo.State.SUCCEEDED)) {
                            if (workInfo.getOutputData().getString("datos").equals("error")) {
                                Toast toast = Toast.makeText(getApplicationContext(), "Algo ha ido mal", Toast.LENGTH_SHORT);
                                toast.show();
                                progreso.hide();
                            } else if (workInfo.getOutputData().getString("datos").equals("0")) {
                                procesoRegistro(us1, con1, correo1);
                            } else if (workInfo.getOutputData().getString("datos").equals("1")){
                                con.setText("");
                                us.setText("");
                                Toast toast = Toast.makeText(getApplicationContext(), "Este usuario ya existe", Toast.LENGTH_SHORT);
                                toast.show();
                                progreso.hide();
                            }else{
                                Log.d("WorkerInfo: ", workInfo.getOutputData().getString("datos"));
                                progreso.hide();
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

    private void procesoRegistro(String us2, String con2, String correo2) {
        Data datos = new Data.Builder()
                .putString("nombre", us2)
                .putString("contra", con2)
                .putString("email", correo2)
                .build();

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(RegistroDB.class).setInputData(datos).build();
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, workInfo -> {
                    if (workInfo != null && workInfo.getState().isFinished()) {
                        if (workInfo.getState().equals(WorkInfo.State.SUCCEEDED)) {
                            if (workInfo.getOutputData().getString("datos").equals("error")) {
                                Toast toast= Toast.makeText(getApplicationContext(),"No se ha podido crear un nuevo usuario, vuelve a intentarlo más tarde",Toast.LENGTH_SHORT);
                                toast.show();
                                progreso.hide();
                            } else if (workInfo.getOutputData().getString("datos").equals("registrado")) {
                                con.setText("");
                                us.setText("");
                                Toast toast= Toast.makeText(getApplicationContext(),"Usuario " + us2 + " creado correctamente",Toast.LENGTH_SHORT);
                                toast.show();
                                progreso.hide();
                                progreso.dismiss();
                                Intent intent = new Intent(RegisterActivity.this, MainMenuActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (workInfo.getOutputData().getString("datos").equals("ya existe")) {
                                con.setText("");
                                us.setText("");
                                Toast toast= Toast.makeText(getApplicationContext(),"El nombre de usuario " + us2 + " ya está cogido",Toast.LENGTH_SHORT);
                                toast.show();
                                progreso.hide();
                            } else {
                                Toast toast= Toast.makeText(getApplicationContext(),"Algo ha ido mal",Toast.LENGTH_SHORT);
                                toast.show();
                                progreso.hide();
                            }
                        } else {
                            Toast toast= Toast.makeText(getApplicationContext(),"Algo ha ido mal",Toast.LENGTH_SHORT);
                            toast.show();
                            progreso.hide();
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);
    }


    @Override
    public void onErrorResponse(VolleyError error) {
        progreso.hide();
        Toast.makeText(this, "No se pudo registrar"+error.toString(), Toast.LENGTH_SHORT).show();
        Log.d("ERROR: ", error.toString());
        con.setText("");
        us.setText("");
    }
}