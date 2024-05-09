package com.example.solarcalculator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PanelActivity extends AppCompatActivity {

    SharedPreferences preferences;

    EditText taxa;

    Button btnSave;

    Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_panel);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        preferences = getSharedPreferences("saved_info", Context.MODE_PRIVATE);

        taxa = findViewById(R.id.editTaxa);
        taxa.setHint(String.valueOf(preferences.getFloat("taxa", 20F)) + "%");
        btnSave = findViewById(R.id.btnSavePanel);
        btnBack = findViewById(R.id.btnBack);

        btnSave.setOnClickListener(v -> {
            setTaxa(convertTaxa(taxa));
            taxa.setHint(String.valueOf(preferences.getFloat("taxa", 20F)) + "%");
            Toast.makeText(this, "Taxa Saved", Toast.LENGTH_SHORT).show();
        });

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(PanelActivity.this, MainActivity.class);
            startActivity(intent);
        });

    }

    private Float convertTaxa(EditText taxa){
        if(taxa.getText() != null){
            return Float.parseFloat(taxa.getText().toString().replace("%",""));
        }
        return 20F;
    }

    private void setTaxa(Float taxaValue){
        SharedPreferences.Editor editor = preferences.edit();

        editor.putFloat("taxa", taxaValue);
        editor.apply();
    }
}