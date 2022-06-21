package com.example.das_gympower.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ImageButton;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.das_gympower.R;

public class MainMenuActivity extends AppCompatActivity {

    ImageButton btnNutricion, btnEntrenamiento, btnEstadofísico;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        btnNutricion = findViewById(R.id.nutricionid);
        btnEntrenamiento = findViewById(R.id.entrenamientoid);
        btnEstadofísico = findViewById(R.id.estadofisicoid);

        btnNutricion.setOnClickListener(view -> {
            finish();
            Intent intent = new Intent(getApplicationContext(), NutricionActivity.class);
            startActivity(intent);
        });

        btnEntrenamiento.setOnClickListener(view -> {
            finish();
            Intent intent = new Intent(getApplicationContext(), EntrenamientoActivity.class);
            startActivity(intent);
        });

        btnEstadofísico.setOnClickListener(view -> {
            finish();
            Intent intent = new Intent(getApplicationContext(), EstadofisicoActivity.class);
            startActivity(intent);
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == event.KEYCODE_BACK) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onResume() {
        super.onResume();

        btnNutricion.setOnClickListener(view -> {
            finish();
            Intent intent = new Intent(getApplicationContext(), NutricionActivity.class);
            startActivity(intent);
        });

        btnEntrenamiento.setOnClickListener(view -> {
            finish();
            Intent intent = new Intent(getApplicationContext(), EntrenamientoActivity.class);
            startActivity(intent);
        });

        btnEstadofísico.setOnClickListener(view -> {
            finish();
            Intent intent = new Intent(getApplicationContext(), EstadofisicoActivity.class);
            startActivity(intent);
        });
    }
}
