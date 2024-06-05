package com.example.solarcalculator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.solarcalculator.dto.CordenadasDTO;
import com.example.solarcalculator.dto.GiDTO;
import com.example.solarcalculator.dto.SunriseSunsetDTO;
import com.google.gson.Gson;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CalculateActivity extends AppCompatActivity {
    OkHttpClient client;
    SharedPreferences preferences;
    TextView energy;
    TextView money;
    Button btnNewAddress;
    SunriseSunsetDTO sunInfo;

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

        // Inicializa elementos da tela e atribui as funções para os botões e selects
        this.initElements();
        this.setButtonAndSelectFunctions();

        // Realiza cáculos
        this.doCalc();
    }

    // ## PRIVATES ##
    private void initElements(){
        // informações salvas
        preferences = getSharedPreferences("saved_info", Context.MODE_PRIVATE);

        btnNewAddress = findViewById(R.id.btnBack);
        energy = findViewById(R.id.textEnergy);
        money = findViewById(R.id.textMoney);
        client = new OkHttpClient();
    }

    private void setButtonAndSelectFunctions(){
        // Quando botao new adress pressionado
        btnNewAddress.setOnClickListener(v -> {
            Intent intent = new Intent(CalculateActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    // Função de transição para o doCalc principal, verifica coordenadas informadas anteriormente(SearchActivity)
    private void doCalc(){
        // Pega latitude e longitude
        String latitude = null, longitude = null;
        Intent intent = getIntent();
        if (intent != null) {
            latitude = intent.getStringExtra("LATITUDE");
            longitude = intent.getStringExtra("LONGITUDE");
        }

        // Verifica null e executa calculo
        assert latitude != null;
        assert longitude != null;
        if (!latitude.isEmpty() && !longitude.isEmpty()) {
            try {
                doCalc(latitude, longitude);
            } catch (IOException | CsvException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void doCalc(String latitude, String longitude) throws IOException, CsvException {
        Calendar calendar = Calendar.getInstance();

        // Periodo em dias
        int periodo = preferences.getInt("periodo", 0);

        // Área do painel solar em metros quadrados (m²)
        double areaPainel = preferences.getFloat("area_celula", 1.5F);

        // Eficiência do painel solar (como um valor percentual)
        double eficienciaPainel = preferences.getFloat("taxa", 20F);

        // Cálculo das horas de exposição solar
        calculateSolarExposureHours(latitude, longitude, () -> {
            // Irradiação solar em kW/m² (valor médio para o local)
            double irradiacaoSolar = 0;
            try {
                irradiacaoSolar = periodo == 2 ? findByCoordinates(latitude, longitude).getAnnual()
                        : findByCoordinates(latitude, longitude).getValueOfMonth(calendar.get(Calendar.MONTH));
            } catch (IOException | CsvException e) {
                Log.e("TAG", "Erro no metodo calculateSolarExposureHours" + e);
                Toast.makeText(this, "Error on calculateSolarExposureHours", Toast.LENGTH_LONG).show();
            }

            // Cálculo da energia gerada pelo painel solar em kWh
            double energiaGerada = calculateEnergyGenerated(areaPainel, irradiacaoSolar, eficienciaPainel,
                    sunInfo.getExposureHours());

            energiaGerada = adjustEnergyGeneratedByPeriod(energiaGerada, periodo);

            // Setando valores finais nos campos de texto
            energy.setText(String.format("%s kWh", String.format(Double.toString(energiaGerada))));
            money.setText(String.format("R$%s", String.format(Double
                    .toString((double) Math.round((energiaGerada * preferences.getFloat("preco", 0.5F)) * 100)
                            / 100))));
        });
    }

    private double calculateEnergyGenerated(double areaPainel, double irradiacaoSolar, double eficienciaPainel,
            double horasExposicaoSolar) {
        return preferences.getInt("qtde_celula", 1)
                * (areaPainel * (irradiacaoSolar / 1000.0) * (eficienciaPainel / 100) * horasExposicaoSolar) * 0.85;
    }

    private double adjustEnergyGeneratedByPeriod(double energiaGerada, int periodo) {
        if (periodo == 0) {
            return Math.round(energiaGerada * 100.0) / 100.0;
        }
        if (periodo == 1) {
            return Math.round((energiaGerada * 30) * 100.0) / 100.0;
        }
        if (periodo == 2) {
            return Math.round((energiaGerada * 365) * 100.0) / 100.0;
        }

        return 0D;
    }

    public GiDTO findByCoordinates(String latitude, String longitude) throws IOException, CsvException {
        CordenadasDTO cordenadas = new CordenadasDTO(latitude, longitude);

        // Incidência para base de dados
        int incidencia = preferences.getInt("incidencia", 0);

        // Obtendo o InputStream do arquivo CSV na pasta "raw"
        InputStream inputStream = getInputStreamByIncidencia(incidencia);

        // Criando um InputStreamReader para o InputStream
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

        // Usando OpenCSV para criar o CSVReader
        CSVReader csvReader = new CSVReader(inputStreamReader);

        // Pular linha de cabeçalho
        csvReader.skip(1);

        // Criar a lista para armazenar os objetos giDTO
        List<GiDTO> gis = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            gis = csvReader.readAll().stream()
                    .map(line -> {
                        String[] parts = line[0].split(";");
                        GiDTO gi = new GiDTO();
                        gi.setId(Long.parseLong(parts[0]));
                        gi.setCountry(parts[1]);
                        gi.setLon(parts[2]);
                        gi.setLat(parts[3]);
                        gi.setAnnual(Long.parseLong(parts[4]));
                        gi.setJan(Long.parseLong(parts[5]));
                        gi.setFev(Long.parseLong(parts[6]));
                        gi.setMar(Long.parseLong(parts[7]));
                        gi.setAbr(Long.parseLong(parts[8]));
                        gi.setMai(Long.parseLong(parts[9]));
                        gi.setJun(Long.parseLong(parts[10]));
                        gi.setJul(Long.parseLong(parts[11]));
                        gi.setAgo(Long.parseLong(parts[12]));
                        gi.setSet(Long.parseLong(parts[13]));
                        gi.setOut(Long.parseLong(parts[14]));
                        gi.setNov(Long.parseLong(parts[15]));
                        gi.setDec(Long.parseLong(parts[16]));
                        return gi;
                    })
                    .collect(Collectors.toList());
        }
        // Pega Cordenadas formatadas
        cordenadas = cordenadas.getCordenadasToSearch();

        // Converter latitude e longitude para double e arredondar
        double targetLat = Math.round(Double.parseDouble(cordenadas.getLatitude()) * 10.0) / 10.0;
        double targetLon = Math.round(Double.parseDouble(cordenadas.getLongitude()) * 100.0) / 100.0;

        // Definir um limite de tolerância para encontrar valores próximos
        double tolerance = 0.3; // Ajuste conforme necessário

        // Pesquisar na lista pelo objeto com a latitude e longitude aproximadas
        assert gis != null;
        GiDTO closestGi = null;
        double minDistance = Double.MAX_VALUE;
        for (GiDTO gi : gis) {
            double lat = Double.parseDouble(gi.getLat());
            double lon = Double.parseDouble(gi.getLon());
            double distance = Math.sqrt(Math.pow(lat - targetLat, 2) + Math.pow(lon - targetLon, 2));
            if (distance < tolerance && distance < minDistance) {
                closestGi = gi;
                minDistance = distance;
            }
        }

        // Retornar o objeto mais próximo encontrado
        return closestGi;
    }

    private InputStream getInputStreamByIncidencia(int incidencia) {
        switch (incidencia) {
            case 0:
                return getResources().openRawResource(R.raw.global_horizontal_means);
            case 1:
                return getResources().openRawResource(R.raw.direct_normal_means);
            case 2:
                return getResources().openRawResource(R.raw.tilted_latitude_means);
            case 3:
                return getResources().openRawResource(R.raw.diffuse_means);
            case 4:
                return getResources().openRawResource(R.raw.par_means);
            default:
                throw new IllegalArgumentException("Incidencia inválida: " + incidencia);
        }
    }

    private void calculateSolarExposureHours(String latitude, String longitude, Runnable callback) {
        // Invoca a obtenção dos dados solares
        doGetSolarExposureHours(new CordenadasDTO(latitude, longitude), () -> {
            // Callback chamado quando os dados solares estiverem disponíveis
            LocalTime sunrise, sunset;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                sunrise = LocalTime.parse(sunInfo.getResults().getSunrise().substring(11, 19));
                sunset = LocalTime.parse(sunInfo.getResults().getSunset().substring(11, 19));

                sunInfo.setExposureHours(Duration.between(sunrise, sunset).toHours());
            }

            callback.run(); // Chama o callback quando os dados estiverem disponíveis
        });
    }

    // Pega url com as coordenadas informadas
    private String getExposureUrl(CordenadasDTO cordenadas) {
        return "https://api.sunrise-sunset.org/json?lat=" + cordenadas.getLatitude() + "&lng="
                + cordenadas.getLongitude() + "&formatted=0";
    }

    // Chama API externa para pegar horas de exposição solar
    private void doGetSolarExposureHours(CordenadasDTO cordenadas, Runnable callback) {
        Request request = new Request.Builder().url(getExposureUrl(cordenadas)).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Log.e("TAG", "Erro no metodo doGetSolarExposureHours" + e));
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                runOnUiThread(() -> {
                    try {
                        assert response.body() != null;

                        String jsonResponse = response.body().string();

                        Gson gson = new Gson();
                        sunInfo = gson.fromJson(jsonResponse, SunriseSunsetDTO.class);

                        callback.run(); // Chama o callback quando os dados estiverem disponíveis

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        });
    }
}