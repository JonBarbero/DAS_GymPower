package com.example.das_gympower.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.das_gympower.R;
import com.example.das_gympower.workers.ComprobarUsuarioDB;
import com.example.das_gympower.workers.LoginDB;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity {

    private String token;
    private EditText usuario, contra;
    private CheckBox checkbox;

    private ProgressDialog progreso ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usuario = (EditText) findViewById(R.id.email);
        contra = (EditText) findViewById(R.id.contraseña);
        checkbox = (CheckBox) findViewById(R.id.recordar);


        SharedPreferences pref = getSharedPreferences("log", Context.MODE_PRIVATE);
        usuario.setText(pref.getString("usuario", ""));
        contra.setText(pref.getString("contra", ""));

        //Proceso para guardar token de FCM
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()) {
                            token = task.getResult();
                            System.out.println("Token: " + token);
                        }
                    }
                });

    }

    public void log(View view) {
        cargarWebService(view);
    }

    private void cargarWebService(View view) {
        progreso = new ProgressDialog(this);
        progreso.setMessage("Comprobando tus credenciales...");
        progreso.show();

        String us = usuario.getText().toString();
        String con = contra.getText().toString();

        //Si el usuario ha presionado el checkbox
        if (checkbox.isChecked()) {
            gPref(view);
            Toast.makeText(this, "Checkbox pulsada", Toast.LENGTH_LONG).show();
        }

        if (!us.equals("") && !con.equals("")) {
            Log.d("Usuario en datos ", us);
            Data datos = new Data.Builder()
                    .putString("email", us)
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
                                    progreso.dismiss();
                                } else if (workInfo.getOutputData().getString("datos").equals("0")) {
                                    Toast toast = Toast.makeText(getApplicationContext(), "Este usuario no existe", Toast.LENGTH_SHORT);
                                    toast.show();
                                    progreso.hide();
                                    progreso.dismiss();
                                } else if (workInfo.getOutputData().getString("datos").equals("1")){
                                    procesoLogin(us, con);
                                }else{
                                    Log.d("WorkerInfo: ", workInfo.getOutputData().getString("datos"));
                                    progreso.hide();
                                    progreso.dismiss();
                                }
                            } else {
                                Toast toast = Toast.makeText(getApplicationContext(), "Algo ha ido mal", Toast.LENGTH_SHORT);
                                toast.show();
                                progreso.hide();
                                progreso.dismiss();
                            }
                        }
                    });
            WorkManager.getInstance(this).enqueue(otwr);
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "Introduce email y contraseña", Toast.LENGTH_SHORT);
            toast.show();
            progreso.hide();
            progreso.dismiss();
        }
    }

    private void procesoLogin(String us, String con) {
        Data datos = new Data.Builder()
                .putString("nombre", us)
                .putString("contra", con)
                .build();

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(LoginDB.class).setInputData(datos).build();
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, workInfo -> {
                    if (workInfo != null && workInfo.getState().isFinished()) {
                        if (workInfo.getState().equals(WorkInfo.State.SUCCEEDED)) {
                            if (workInfo.getOutputData().getString("datos").equals("error")) {
                                Toast toast = Toast.makeText(getApplicationContext(), "No se ha podido iniciar sesión, vuelve a intentarlo más tarde", Toast.LENGTH_SHORT);
                                toast.show();
                                progreso.hide();
                                progreso.dismiss();
                            } else if (workInfo.getOutputData().getString("datos").equals("loggeado con exito")) { //Si los datos son correctos hacer login
                                //Se imprime el nombre de usuario
                                Toast toast = Toast.makeText(getApplicationContext(), "¡Hola, " + us + "!", Toast.LENGTH_SHORT);
                                toast.show();
                                progreso.hide();
                                progreso.dismiss();

                                //Despues de loguearse main menu se abre
                                Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (workInfo.getOutputData().getString("datos").equals("incorrecto")) { //Si los datos son incorrectos no hacer nada
                                Toast toast = Toast.makeText(getApplicationContext(), "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT);
                                toast.show();
                                progreso.hide();
                                progreso.dismiss();
                            } else {
                                Toast toast = Toast.makeText(getApplicationContext(), "Algo ha ido mal", Toast.LENGTH_SHORT);
                                toast.show();
                                progreso.hide();
                                progreso.dismiss();
                            }
                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(), "Algo ha ido mal", Toast.LENGTH_SHORT);
                            toast.show();
                            progreso.hide();
                            progreso.dismiss();
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);
    }

    public void reg(View view) {
        Intent toReg = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(toReg);
        finish();
    }

    public void gPref(View view) {

        SharedPreferences prefGuardar = getSharedPreferences("log", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefGuardar.edit();
        editor.putString("usuario", usuario.getText().toString());
        editor.putString("contra", contra.getText().toString());
        editor.commit();
        Toast.makeText(this, "Contraseña y usuario guardados correctamente", Toast.LENGTH_LONG).show();
    }
}
