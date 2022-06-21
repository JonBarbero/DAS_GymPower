package com.example.das_gympower.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.das_gympower.R;
import com.example.das_gympower.workers.InsertarFisico;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class InsertarfisicoActivity extends AppCompatActivity {

    private TextView nombretv, pesotv, desctv;
    private ProgressDialog progreso ;
    private ImageView imgiv;
    Uri uri = null;
    String url = null;
    private Button bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrarfisico);

        nombretv = (TextView) findViewById(R.id.editTextTextPersonName);
        pesotv = (TextView) findViewById(R.id.pesofisico);
        desctv = (TextView) findViewById(R.id.editTextTextMultiLine);
        imgiv = (ImageView) findViewById(R.id.imageView2);
        bt = (Button) findViewById(R.id.button2);

        imgiv.setOnClickListener(v -> elegirImagen());

        bt.setOnClickListener(v -> {
            String nombre = nombretv.getText().toString();
            subirImagen(nombre);
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            Intent intent = new Intent(getApplicationContext(), com.example.das_gympower.activity.MainMenuActivity.class);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }

    private void elegirImagen() {
        if (ContextCompat.checkSelfPermission(InsertarfisicoActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            seleccionImagenGaleria();
        } else {
            solicitudPermisosGaleria.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    private void seleccionImagenGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galeriaActivityResultLauncher.launch(intent);

    }

    private ActivityResultLauncher<Intent> galeriaActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    // Gestionamos el resultado de nuestro intent
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Obtener URI de la imagen
                        Intent data = result.getData();
                        uri = data.getData();

                        // Settear la imagen seleccionada
                        imgiv.setImageURI(uri);
                    } else {
                        Toast.makeText(InsertarfisicoActivity.this, "Cancelado por el Usuario", Toast.LENGTH_SHORT).show();
                    }
                }
            }
    );

    private ActivityResultLauncher<String> solicitudPermisosGaleria =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted->{
                if (isGranted) {
                    // isGranted --> Es concedido el permiso
                    seleccionImagenGaleria();
                } else {
                    Toast.makeText(InsertarfisicoActivity.this, "Permiso denegado", Toast.LENGTH_SHORT).show();
                }
            });

    public void sumar(View view, String url1) {
        progreso = new ProgressDialog(this);
        progreso.setMessage("Sumando físico al servidor...");
        progreso.show();

        String nombre = nombretv.getText().toString();
        String peso = pesotv.getText().toString();
        String desc = desctv.getText().toString();
        String urlIMG;

        nombre = nombre.replace(" ","%20");
        desc = desc.replace(" ","%20");

        StringBuilder urlTf = new StringBuilder(url1);
        urlTf.deleteCharAt(0);
        urlIMG = urlTf.toString();
        Log.d("url de la imagen ", urlIMG);

        if (peso.equals("")){
            peso="no hay peso disponible";
        }

        if (nombre.equals("")){
            nombre="no hay nombre disponible";
        }

        if (desc.equals("")){
            desc="no hay descripcion disponible";
        }

        nombre = nombre.replace(" ","%20");
        peso = peso.replace(" ","%20");
        desc = desc.replace(" ","%20");

        Data datos = new Data.Builder()
                .putString("nombre", nombre)
                .putString("peso", peso)
                .putString("descripcion", desc)
                .putString("url", urlIMG)
                .build();

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(InsertarFisico.class).setInputData(datos).build();
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, workInfo -> {
                    if (workInfo != null && workInfo.getState().isFinished()) {
                        if (workInfo.getState().equals(WorkInfo.State.SUCCEEDED)) {
                            if (workInfo.getOutputData().getString("datos").equals("error")) {
                                Toast toast = Toast.makeText(getApplicationContext(), "No se ha podido insertar", Toast.LENGTH_SHORT);
                                toast.show();
                                nombretv.setText("");
                                pesotv.setText("");
                                desctv.setText("");
                                progreso.hide();
                                progreso.dismiss();
                            } else if (workInfo.getOutputData().getString("datos").equals("no registrado")) {
                                Toast toast = Toast.makeText(getApplicationContext(), "Su físico no ha podido ser insertado, intentelo más tarde", Toast.LENGTH_SHORT);
                                toast.show();
                                nombretv.setText("");
                                pesotv.setText("");
                                desctv.setText("");
                                progreso.hide();
                                progreso.dismiss();
                            } else if (workInfo.getOutputData().getString("datos").equals("registrado")) {
                                Toast toast = Toast.makeText(getApplicationContext(), "Su físico ha sido insertado con éxito", Toast.LENGTH_SHORT);
                                toast.show();
                                nombretv.setText("");
                                pesotv.setText("");
                                desctv.setText("");
                                progreso.hide();
                                progreso.dismiss();
                            } else {
                                Toast toast = Toast.makeText(getApplicationContext(), "Algo ha ido mal", Toast.LENGTH_SHORT);
                                toast.show();
                                nombretv.setText("");
                                pesotv.setText("");
                                desctv.setText("");
                                progreso.hide();
                                progreso.dismiss();
                            }
                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(), "No está en comprobación de datos", Toast.LENGTH_SHORT);
                            toast.show();
                            nombretv.setText("");
                            pesotv.setText("");
                            desctv.setText("");
                            progreso.hide();
                            progreso.dismiss();
                        }
                    }
                });

        WorkManager.getInstance(this).enqueue(otwr);
    }

    private void subirImagen(String nombre) {
        String carpetaImagenes = "fisico/"; // Aqui almacenamos todas las imagenes de los usuarios
        String nombreImagen = carpetaImagenes + nombre;

        if (uri != null) {
            StorageReference reference = FirebaseStorage.getInstance().getReference(nombreImagen);
            reference.putFile(uri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful()) ;
                        String uriImagen = " " + uriTask.getResult(); // Obtenemos la uri que se ha subido al Storage
                        //Enviamos la uri a la base de datos
                        url = uriImagen;
                        Log.d("url respuesta ", url);
                        sumar(getCurrentFocus(), uriImagen);
                    });
        } else {
            sumar(getCurrentFocus(), "  ");
        }
    }

}