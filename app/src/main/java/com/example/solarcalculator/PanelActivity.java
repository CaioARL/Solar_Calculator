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

    EditText qtdeCelula;

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

//        Informações salvas no armazenamento do dipositivo
        preferences = getSharedPreferences("saved_info", Context.MODE_PRIVATE);

        taxa = findViewById(R.id.editTaxa);
        taxa.setHint(preferences.getFloat("taxa", 20F) + "%");

        qtdeCelula = findViewById(R.id.editCelula);
        qtdeCelula.setHint(preferences.getInt("qtde_celula", 1) + ".un");

        btnSave = findViewById(R.id.btnSavePanel);
        btnBack = findViewById(R.id.btnBack);

//        Quando botao salvar presionado
        btnSave.setOnClickListener(v -> {
            setTaxa(convertTaxa(taxa));
            setQtdeCelula(convertQtdeCelula(qtdeCelula));

            taxa.setHint(preferences.getFloat("taxa", 20F) + "%");
            qtdeCelula.setHint(preferences.getInt("qtde_celula", 1) + ".un");

            taxa.clearFocus();
            taxa.setText("");

            qtdeCelula.clearFocus();
            qtdeCelula.setText("");

            Toast.makeText(this, "Panel Saved", Toast.LENGTH_SHORT).show();
        });

//        Quando botao voltar pressionado
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(PanelActivity.this, MainActivity.class);
            startActivity(intent);
        });

    }

    private Float convertTaxa(EditText taxa){
        if(taxa!=null && taxa.getText() != null &&
                !taxa.getText().toString().isEmpty()){
            return Float.parseFloat(taxa.getText().toString().replace("%",""));
        }else {
            return preferences.getFloat("taxa", 20F);
        }
    }

    private void setTaxa(Float taxaValue){
        SharedPreferences.Editor editor = preferences.edit();

        editor.putFloat("taxa", taxaValue);
        editor.apply();
    }

    private Integer convertQtdeCelula(EditText qtdeCelula) {
        if (qtdeCelula != null && qtdeCelula.getText() != null &&
                !qtdeCelula.getText().toString().isEmpty()) {
            return Integer.parseInt(qtdeCelula.getText().toString().replace(".un", ""));
        } else {
            return preferences.getInt("qtde_celula", 1);
        }
    }

    private void setQtdeCelula(Integer qtdeCelulaValue){
        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt("qtde_celula", qtdeCelulaValue);
        editor.apply();
    }
}