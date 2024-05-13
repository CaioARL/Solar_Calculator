package com.example.solarcalculator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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

public class PanelActivity extends AppCompatActivity {

    SharedPreferences preferences;

    EditText taxa;

    EditText qtdeCelula;

    EditText areaCelula;

    Spinner incidenciaMenu;

    Button btnSave;

    Button btnBack;

    Boolean selectChanged = false;

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

        areaCelula = findViewById(R.id.editArea);
        areaCelula.setHint(preferences.getFloat("area_celula", 1.5F) + "m²");

        btnSave = findViewById(R.id.btnSavePanel);
        btnBack = findViewById(R.id.btnBack);

//        Configurando listagem
        incidenciaMenu = findViewById(R.id.spinnerIncidencia);
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this, R.array.incidencia, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        incidenciaMenu.setAdapter(adapter);
        incidenciaMenu.setSelection(preferences.getInt("incidencia", 0));

//        Quando botao salvar presionado
        btnSave.setOnClickListener(v -> {
//            sets
            setTaxa(convertTaxa(taxa));
            setQtdeCelula(convertQtdeCelula(qtdeCelula));
            setAreaCelula(convertAreaCelula(areaCelula));

//            hints
            taxa.setHint(preferences.getFloat("taxa", 20F) + "%");
            qtdeCelula.setHint(preferences.getInt("qtde_celula", 1) + ".un");
            areaCelula.setHint(preferences.getFloat("area_celula", 1.5F) + "m²");

//            Selcted
            if(selectChanged){
                setIncidencia(incidenciaMenu.getSelectedItemPosition());
            }

//            clear focus e text
            taxa.clearFocus();
            taxa.setText("");

            qtdeCelula.clearFocus();
            qtdeCelula.setText("");

            areaCelula.clearFocus();
            areaCelula.setText("");

            Toast.makeText(this, "Panel Saved", Toast.LENGTH_SHORT).show();
        });

//        Quando botao voltar pressionado
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(PanelActivity.this, MainActivity.class);
            startActivity(intent);
        });

//        Quando incidencia for selecionada
        incidenciaMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectChanged = true;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

//    PRIVATES

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

    private Float convertAreaCelula(EditText areaCelula) {
        if (areaCelula != null && areaCelula.getText() != null &&
                !areaCelula.getText().toString().isEmpty()) {
            return Float.parseFloat(areaCelula.getText().toString().replace("m²", ""));
        } else {
            return preferences.getFloat("area_celula", 1.5F);
        }
    }

    private void setAreaCelula(Float areaCelulaValue){
        SharedPreferences.Editor editor = preferences.edit();

        editor.putFloat("area_celula", areaCelulaValue);
        editor.apply();
    }

    private void setIncidencia(Integer incidenciaValue){
        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt("incidencia", incidenciaValue);
        editor.apply();
    }
}