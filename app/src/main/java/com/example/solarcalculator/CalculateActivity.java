package com.example.solarcalculator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.solarcalculator.dto.IrradiationDTO;
import com.example.solarcalculator.dto.WeatherDTO;
import com.example.solarcalculator.utils.CalculateActivityTextUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.opencsv.exceptions.CsvException;

import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CalculateActivity extends AppCompatActivity {
    private static final org.apache.commons.logging.Log log = LogFactory.getLog(CalculateActivity.class);
    OkHttpClient client;
    SharedPreferences preferences;
    TextView economyPeriodText;
    TextView economyROIText;
    TextView environmentCO2Text;
    TextView weatherForecastText;
    TextView weatherImpactText;
    Button btnVideoTutorial;
    Button btnHome;
    IrradiationDTO irradiationDTO;
    double areaPainel;
    double eficienciaPainel;
    int incidencia;
    int periodo;

    private static final String videoId = "c8e2RSPIzQg";
    private static final String videoTime = "5s";

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

    // Abrir video explicativo
    public void openYouTubeVideo(View view) {

        // Tenta abrir o vídeo no aplicativo do YouTube
        Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoId + "?t=" + videoTime));

        // Verifica se há um aplicativo que pode lidar com a Intent
        if (appIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(appIntent);
        } else {
            // Se não houver, abre no navegador web
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + videoId + "&t=" + videoTime));
            startActivity(webIntent);
        }
    }

    // ## PRIVATES ##
    private void initElements(){
        // informações salvas
        preferences = getSharedPreferences("saved_info", Context.MODE_PRIVATE);

        // Cliente HTTP
        client = new OkHttpClient();

        // Buttons
        btnHome = findViewById(R.id.btnHome);
        btnVideoTutorial = findViewById(R.id.btnVideoTutorial);

        //  Textviews
        economyPeriodText = findViewById(R.id.economyPeriod);
        economyROIText = findViewById(R.id.economyROI);
        environmentCO2Text = findViewById(R.id.environmentCO2);
        weatherForecastText = findViewById(R.id.weatherForecast);
        weatherImpactText = findViewById(R.id.weatherImpact);

        // DTO
        irradiationDTO = new IrradiationDTO();

        // Pega valores salvos
        areaPainel = preferences.getFloat("area_celula", 1.5F);
        eficienciaPainel = preferences.getFloat("taxa", 20F);
        incidencia = preferences.getInt("incidencia", 0);
        periodo = preferences.getInt("periodo", 0);

    }

    private void setButtonAndSelectFunctions(){
        // Quando botao new adress pressionado
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(CalculateActivity.this, MainActivity.class);
            startActivity(intent);
        });

        // Quando botao video tutorial pressionado
        btnVideoTutorial.setOnClickListener(this::openYouTubeVideo);
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
        // Chama api externa para pegar iradiação solar e posteriormente as horas de exposição solar
        doGetIrradiation(latitude, longitude);
    }

    private double sumIrradiationPerHours(IrradiationDTO irradiationDTO, int incidencia) {
        double sum = 0;
        if(incidencia==0)
            for (Double data : irradiationDTO.getHourly().getShortwave_radiation()) {
                sum += data;
            }
        if(incidencia==1)
            for (Double data : irradiationDTO.getHourly().getDirect_radiation()) {
                sum += data;
            }
        if(incidencia==2)
            for (Double data : irradiationDTO.getHourly().getDiffuse_radiation()) {
                sum += data;
            }
        if(incidencia==3)
            for (Double data : irradiationDTO.getHourly().getDirect_normal_irradiance()) {
                sum += data;
            }
        if(incidencia==4)
            for (Double data : irradiationDTO.getHourly().getGlobal_tilted_irradiance()) {
                sum += data;
            }
        if(incidencia==5)
            for (Double data : irradiationDTO.getHourly().getTerrestrial_radiation()) {
                sum += data;
            }
        return sum;
    }

    private void fillReport(double energiaGerada, float precoEnergia, double areaPainel, double eficienciaPainel, double irradiacao,
                            Integer numberOfPanels, int periodo, WeatherDTO weatherDTO) {
        CalculateActivityTextUtils calculateActivityTextUtils = new CalculateActivityTextUtils(energiaGerada, precoEnergia, areaPainel,
                eficienciaPainel, irradiacao, numberOfPanels, periodo, weatherDTO);

        // Setando valores nos campos de texto
        economyPeriodText.setText(calculateActivityTextUtils.getEconomy());
        economyROIText.setText(calculateActivityTextUtils.getROI());
        environmentCO2Text.setText(calculateActivityTextUtils.getCO2Reduction());
        weatherForecastText.setText(calculateActivityTextUtils.getWeatherForecast());
        weatherImpactText.setText(calculateActivityTextUtils.getWeatherImpactEnergyProduction());
    }

    private double calculateEnergyGenerated(double areaPainel, double irradiacaoSolar, double eficienciaPainel) {
        return ((areaPainel * (eficienciaPainel/100)) * preferences.getInt("qtde_celula", 1)) * irradiacaoSolar/100;
    }

    private static List<String> getDateRangeByPeriod(int periodo) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        Calendar calendar = Calendar.getInstance();

        List<String> dateRange = new ArrayList<>();

        // Data atual
        String currentDate = formatter.format(calendar.getTime());

        // Subtrai o mês
        if (periodo==1)
            calendar.add(Calendar.MONTH, -1);

        // Add data atual e data subtraída
        dateRange.add(formatter.format(calendar.getTime()));
        dateRange.add(currentDate);

        return dateRange;
    }

    private String getUrlBaseOpenMeteo(String latitude, String longitude){
        return "https://api.open-meteo.com/v1/forecast?latitude=" + latitude + "&longitude=" + longitude;
    }

    private String getIrradiationUrl(String latitude, String longitude, int periodo){
        List<String> dateRange = getDateRangeByPeriod(periodo);
        return getUrlBaseOpenMeteo(latitude, longitude) + "&hourly=shortwave_radiation,direct_radiation,diffuse_radiation,direct_normal_irradiance,global_tilted_irradiance,terrestrial_radiation" +
                "&start_date=" + dateRange.get(0) + "&end_date=" + dateRange.get(1);
    }

    private String getWheaterUrl(String latitude, String longitude){
        return getUrlBaseOpenMeteo(latitude, longitude) + "&hourly=temperature_2m,precipitation,cloud_cover,wind_speed_10m&forecast_days=1";
    }

    private void doGetIrradiation(String latitude, String longitude){
        // Pega url dinâmica
        Request request = new Request.Builder().url(getIrradiationUrl(latitude, longitude, periodo)).build();
        client.newCall(request).enqueue(new Callback() {
            // Caso ocorra erro preenche texto genérico
            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Log.e("TAG", "Erro no metodo doGetIrradiation" + e);
                    Toast.makeText(CalculateActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                runOnUiThread(() -> {
                    try {
                        assert response.body() != null;

                        String jsonResponse = response.body().string();

                        Gson gson = new Gson();
                        Type type = new TypeToken<IrradiationDTO>() {}.getType();

                        irradiationDTO = gson.fromJson(jsonResponse, type);

                        double irradiacaoSolar = sumIrradiationPerHours(irradiationDTO, incidencia);

                        // Cálculo da energia gerada pelo painel solar em kWh
                        double energiaGerada = calculateEnergyGenerated(areaPainel, irradiacaoSolar,
                                eficienciaPainel);

                        // Chama api externa para pegar informações do clima
                        doGetWeather(latitude, longitude, energiaGerada, irradiacaoSolar);

                    } catch (Exception e) {
                        log.error("Erro no método doGetIrradiation", e);
                        Toast.makeText(CalculateActivity.this, "Error on doGetIrradiation.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });
    }

    private void doGetWeather(String latitude, String longitude, double energiaGerada, double irradiacaoSolar){
        // Pega url dinâmica
        Request request = new Request.Builder().url(getWheaterUrl(latitude, longitude)).build();
        client.newCall(request).enqueue(new Callback() {
            // Caso ocorra erro preenche texto genérico
            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Log.e("TAG", "Erro no metodo doGetWeather" + e);
                    Toast.makeText(CalculateActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                runOnUiThread(() -> {
                    try {
                        assert response.body() != null;

                        String jsonResponse = response.body().string();

                        Gson gson = new Gson();
                        Type type = new TypeToken<WeatherDTO>() {}.getType();

                        WeatherDTO weatherDTO = gson.fromJson(jsonResponse, type);

                        // Setando valores finais nos campos de texto
                        fillReport(energiaGerada, preferences.getFloat("preco", 0.5F), areaPainel,
                                eficienciaPainel, irradiacaoSolar,
                                preferences.getInt("qtde_celula", 1), periodo, weatherDTO);

                    } catch (Exception e) {
                        log.error("Erro no método doGetWeather", e);
                        Toast.makeText(CalculateActivity.this, "Error on doGetWeather.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });
    }
}