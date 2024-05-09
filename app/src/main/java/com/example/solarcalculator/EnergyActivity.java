package com.example.solarcalculator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class EnergyActivity extends AppCompatActivity {

    SharedPreferences preferences;

    EditText preco;

    Button btnSave;

    Button btnBack;

    Spinner periodoMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_energy);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        preferences = getSharedPreferences("saved_info", Context.MODE_PRIVATE);

        preco = findViewById(R.id.editPreco);
        preco.setHint("R$" + preferences.getFloat("preco", 0.50F));
        btnSave = findViewById(R.id.btnSaveEnergy);
        btnBack = findViewById(R.id.btnBack);
        periodoMenu = findViewById(R.id.spinnerPeriodos);
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this, R.array.periodos, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        periodoMenu.setAdapter(adapter);

        btnSave.setOnClickListener(v -> {
            setPreco(convertPreco(preco));
            preco.setHint("R$" + preferences.getFloat("preco", 0.50F));
            Toast.makeText(this, "Preço Saved", Toast.LENGTH_SHORT).show();
        });

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(EnergyActivity.this, MainActivity.class);
            startActivity(intent);
        });

    }

    private Float convertPreco(EditText preco){
        if(preco.getText() != null){
            return Float.parseFloat(preco.getText().toString());
        }
        return 0.50F;
    }

    private void setPreco(Float precoValue){
        SharedPreferences.Editor editor = preferences.edit();

        editor.putFloat("preco", precoValue);
        editor.apply();
    }
}