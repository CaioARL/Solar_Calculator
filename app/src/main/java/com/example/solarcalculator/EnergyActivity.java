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

public class EnergyActivity extends AppCompatActivity {
    SharedPreferences preferences;
    EditText preco;
    Button btnSave;
    Button btnBack;
    Spinner periodoMenu;
    Boolean selectChanged = false;

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

        // Inicializa elementos da tela e atribui as funções para os botões e selects
        this.initElements();
        this.setButtonAndSelectFunctions();

        //Configurando listagem de periodos
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this, R.array.periodos, android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        periodoMenu.setAdapter(adapter);
        periodoMenu.setSelection(preferences.getInt("periodo", 0));
    }

    // ##PRIVATES##
    private void initElements() {
        //Informações salvas no armazenamento do dipositivo
        preferences = getSharedPreferences("saved_info", Context.MODE_PRIVATE);

        preco = findViewById(R.id.editPreco);
        preco.setHint("R$" + preferences.getFloat("preco", 0.50F));

        btnSave = findViewById(R.id.btnSaveEnergy);
        btnBack = findViewById(R.id.btnBack);

        periodoMenu = findViewById(R.id.spinnerPeriodos);
    }

    private void setButtonAndSelectFunctions() {
        //Quando botao salvar pressionado
        btnSave.setOnClickListener(v -> {
            setPreco(convertPreco(preco));

            preco.setHint("R$" + preferences.getFloat("preco", 0.50F));

            if(selectChanged){
                setPeriodo(periodoMenu.getSelectedItemPosition());
            }

            preco.clearFocus();
            preco.setText("");

            Toast.makeText(this, "Energy Saved", Toast.LENGTH_SHORT).show();
        });

        //Quando botao voltar pressionado
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(EnergyActivity.this, MainActivity.class);
            startActivity(intent);
        });

        //Quando periodo for selecionado
        periodoMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectChanged = true;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    // ## CONVERTS E SETS ##
    private Float convertPreco(EditText preco){
        if(preco!=null && preco.getText() != null &&
                !preco.getText().toString().isEmpty()){
            return Float.parseFloat(preco.getText().toString());
        }else {
            return preferences.getFloat("preco", 0.50F);
        }
    }

    private void setPreco(Float precoValue){
        SharedPreferences.Editor editor = preferences.edit();

        editor.putFloat("preco", precoValue);
        editor.apply();
    }

    private void setPeriodo(Integer periodoValue){
        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt("periodo", periodoValue);
        editor.apply();
    }
}