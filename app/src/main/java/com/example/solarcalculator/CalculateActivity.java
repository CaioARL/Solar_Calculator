package com.example.solarcalculator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class CalculateActivity extends AppCompatActivity {

    SharedPreferences preferences;

    TextView energy;

    TextView money;

    Button btnNewAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_calculate);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        preferences = getSharedPreferences("saved_info", Context.MODE_PRIVATE);

        btnNewAddress = findViewById(R.id.btnBack);
        btnNewAddress.setOnClickListener(v -> {
            Intent intent = new Intent(CalculateActivity.this, MainActivity.class);
            startActivity(intent);
        });

        energy = findViewById(R.id.textEnergy);
        money = findViewById(R.id.textMoney);

        Intent intent = getIntent();
        if (intent != null) {
            String latitude = intent.getStringExtra("LATITUDE");
            String longitude = intent.getStringExtra("LONGITUDE");
            try {
                String generatedEnergy = doCalc(latitude, longitude);
                energy.setText(doCalc(latitude, longitude));
                money.setText(doMoney(generatedEnergy, preferences.getFloat("preco", 0.50F), preferences.getFloat("taxa", 20F)));
            } catch (Exception e) {
                Log.e("TAG", "Erro no metodo onCreate");
                throw new RuntimeException(e);
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private String doCalc(String latitude, String longitude) {
        try {
            // Declinação solar (em radianos)
            double declinacaoSolar = calcularDeclinacaoSolar();

            // Duração do dia em horas
            double duracaoDoDia = 24;

            // Irradiação solar média (em W/m²)
            double irradiacaoSolarMedia = 0;

            // Loop para calcular a irradiação solar média ao longo de 24 horas
            for (int hora = 0; hora < 24; hora++) {
                // Ângulo horário (em radianos)
                double anguloHorario = calcularAnguloHorario(hora, Double.parseDouble(longitude));

                // Ângulo de elevação solar (em radianos)
                double anguloElevacaoSolar = calcularAnguloElevacaoSolar(Double.parseDouble(latitude), declinacaoSolar, anguloHorario);

                // Intensiveness solar máxima (em W/m²)
                double intensidadeSolarMaxima = 1000; // Estimativa aproximada da intensidade solar máxima

                // Se o ângulo de elevação solar for positivo (sol acima do horizonte)
                if (anguloElevacaoSolar > 0) {
                    // Adiciona a contribuição da irradiação solar nesta hora
                    irradiacaoSolarMedia += intensidadeSolarMaxima * Math.sin(anguloElevacaoSolar);
                }
            }

            // Divide pelo número de horas para obter a média
            irradiacaoSolarMedia /= 24;

            // Converter a irradiação solar média de Wh/m² para kWh/m²
            double energiaTotalKWh = irradiacaoSolarMedia * duracaoDoDia / 1000;

            return String.format("%.2f kWh/m²", energiaTotalKWh);
        } catch (Exception e) {
            Log.e("TAG", "Erro no método doCalc");
            throw new RuntimeException(e);
        }
    }

    public static double calcularDeclinacaoSolar() {
        // Dia do ano (0 a 365, onde 0 é 1 de janeiro)
        int diaDoAno = 120; // Exemplo de 1º de maio

        // Ângulo de declinação solar (em radianos)
        double declinacaoSolar = 23.45 * Math.sin(Math.toRadians(360 * (284 + diaDoAno) / 365.0));

        return Math.toRadians(declinacaoSolar);
    }

    public static double calcularAnguloElevacaoSolar(double latitude, double declinacaoSolar, double anguloHorario) {
        // Ângulo de elevação solar (em radianos)

        return Math.asin(Math.sin(Math.toRadians(latitude)) * Math.sin(declinacaoSolar)
                + Math.cos(Math.toRadians(latitude)) * Math.cos(declinacaoSolar) * Math.cos(anguloHorario));
    }

    public static double calcularAnguloHorario(int hora, double longitude) {
        // Ângulo horário (em radianos)
        return Math.toRadians(15 * (hora - 12) + longitude / 15);
    }

    @SuppressLint("DefaultLocale")
    public String doMoney(String energiaSolarGerada, Float precoEnergia, Float taxaConversao) {
        try {
            // Substituir vírgulas por pontos
            energiaSolarGerada = energiaSolarGerada.replace(",", ".").replace("kWh/m²", "");

            // Converter a energia solar gerada para quilowatts
            float energiaSolarKW = Float.parseFloat(energiaSolarGerada);

            // Calcular o dinheiro gerado
            Float valor = (energiaSolarKW * precoEnergia * taxaConversao)/100;

            return String.format("R$%.2f", valor);
        } catch (Exception e) {
            Log.e("TAG", "Erro no metodo doMoney", e);
            throw new RuntimeException(e);
        }
    }

}