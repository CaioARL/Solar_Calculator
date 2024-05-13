package com.example.solarcalculator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.solarcalculator.Dto.GhiDTO;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;


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

        btnNewAddress = findViewById(R.id.btnBack);
        energy = findViewById(R.id.textEnergy);
        money = findViewById(R.id.textMoney);
        String latitude = null, longitude = null;
        double energiaGerada = 0D;

//        informações salvas
        preferences = getSharedPreferences("saved_info", Context.MODE_PRIVATE);


//        Quando botao new adress pressionado
        btnNewAddress.setOnClickListener(v -> {
            Intent intent = new Intent(CalculateActivity.this, MainActivity.class);
            startActivity(intent);
        });

//        Pega latitude e longitude
        Intent intent = getIntent();
        if (intent != null) {
            latitude = intent.getStringExtra("LATITUDE");
            longitude = intent.getStringExtra("LONGITUDE");
        }

        assert latitude != null;
        assert longitude != null;
        if(!latitude.isEmpty() && !longitude.isEmpty()){
            try {
                energiaGerada = doCalc(latitude, longitude);
            } catch (IOException | CsvException e) {
                throw new RuntimeException(e);
            }
        }

        energy.setText(String.format(Double.toString(energiaGerada)));
//        money.setText(doMoney(generatedEnergy, preferences.getFloat("preco", 0.50F), preferences.getFloat("taxa", 20F)));
    }

    private Double doCalc(String latitude, String longitude) throws IOException, CsvException {

        // Área do painel solar em metros quadrados (m²)
        double areaPainel = preferences.getFloat("area_celula", 1.5F);

        // Eficiência do painel solar (como um valor percentual)
        double eficienciaPainel = preferences.getFloat("taxa", 20F);

        // Dia do ano
        Calendar calendar = Calendar.getInstance();
        int N = calendar.get(Calendar.DAY_OF_YEAR);

        // Conversão da latitude para radianos
        double latitudeRad = Math.toRadians(Double.parseDouble(latitude));

        // Cálculo da declinação solar
        double delta = 0.409 * Math.sin((2 * Math.PI * (N - 1) / 365) - 1.39);

        // Cálculo das horas de exposição solar
        double horasExposicaoSolar = (1 / Math.PI) * (Math.acos(-Math.tan(latitudeRad) * Math.tan(delta)) + (Math.PI / 12));

        // Irradiação solar em kW/m² (valor médio para o local)
        double irradiacaoSolar = findByLatLon(latitude, longitude).getValueOfMonth(calendar.get(Calendar.MONTH));

        // Cálculo da energia gerada pelo painel solar em kWh
        return areaPainel * irradiacaoSolar * eficienciaPainel * horasExposicaoSolar;
    }

    public GhiDTO findByLatLon(String latitude, String longitude) throws IOException, CsvException {
        // Obtendo o InputStream do arquivo CSV na pasta "raw"
        InputStream inputStream = getResources().openRawResource(R.raw.global_horizontal_means);

        // Criando um InputStreamReader para o InputStream
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

        // Usando OpenCSV para criar o CSVReader
        CSVReader csvReader = new CSVReader(inputStreamReader);

        // Pular linha de cabeçalho
        csvReader.skip(1);

        // Criar a lista para armazenar os objetos GhiDTO
        List<GhiDTO> ghis = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            ghis = csvReader.readAll().stream()
                    .map(line -> {
                        String[] parts = line[0].split(";");
                        GhiDTO ghi = new GhiDTO();
                        ghi.setId(Long.parseLong(parts[0]));
                        ghi.setUf(parts[1]);
                        ghi.setLon(parts[2]);
                        ghi.setLat(parts[3]);
                        ghi.setAnual(Long.parseLong(parts[4]));
                        ghi.setJan(Long.parseLong(parts[5]));
                        ghi.setFev(Long.parseLong(parts[6]));
                        ghi.setMar(Long.parseLong(parts[7]));
                        ghi.setAbr(Long.parseLong(parts[8]));
                        ghi.setMai(Long.parseLong(parts[9]));
                        ghi.setJun(Long.parseLong(parts[10]));
                        ghi.setJul(Long.parseLong(parts[11]));
                        ghi.setAgo(Long.parseLong(parts[12]));
                        ghi.setSet(Long.parseLong(parts[13]));
                        ghi.setOut(Long.parseLong(parts[14]));
                        ghi.setNov(Long.parseLong(parts[15]));
                        ghi.setDec(Long.parseLong(parts[16]));
                        return ghi;
                    })
                    .collect(Collectors.toList());
        }

        // Pesquisar na lista pelo objeto com a latitude e longitude especificadas
        assert ghis != null;
        for (GhiDTO ghi : ghis) {
            if (ghi.getLat().equals(latitude) && ghi.getLon().equals(longitude)) {
                return ghi; // Retorne o objeto quando for encontrado
            }
        }

        // Retornar null se não encontrar nenhum objeto com a latitude e longitude especificadas
        return null;
    }
}