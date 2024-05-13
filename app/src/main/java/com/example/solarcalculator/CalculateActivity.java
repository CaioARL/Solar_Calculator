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

import com.example.solarcalculator.Dto.CordenadasDTO;
import com.example.solarcalculator.Dto.GhiDTO;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.util.TimeZone;

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

        energy.setText(String.format("%s kWh", String.format(Double.toString(energiaGerada))));
        money.setText(String.format("R$%s", String.format(Double.toString(energiaGerada*preferences.getFloat("preco", 0.5F)))));
    }

    private Double doCalc(String latitude, String longitude) throws IOException, CsvException {
        Calendar calendar = Calendar.getInstance();

        // Periodo em dias
        int periodo = preferences.getInt("periodo", 0);

        // Área do painel solar em metros quadrados (m²)
        double areaPainel = preferences.getFloat("area_celula", 1.5F);

        // Eficiência do painel solar (como um valor percentual)
        double eficienciaPainel = preferences.getFloat("taxa", 20F);

        // Cálculo das horas de exposição solar
        double horasExposicaoSolar = calculateSolarExposureHours(Double.parseDouble(latitude) , Double.parseDouble(longitude));

        // Irradiação solar em kW/m² (valor médio para o local)
        double irradiacaoSolar = periodo==2?findByLatLon(latitude, longitude).getAnual():findByLatLon(latitude, longitude).getValueOfMonth(calendar.get(Calendar.MONTH));

        // Cálculo da energia gerada pelo painel solar em kWh
        double energiaGerada = preferences.getInt("qtde_celula", 1) * (areaPainel * (irradiacaoSolar/1000.0) * (eficienciaPainel / 100) * horasExposicaoSolar);

        if(periodo==0){
            return Math.round(energiaGerada * 100.0) / 100.0;
        }
        if(periodo==1){
            return Math.round((energiaGerada*30) * 100.0) / 100.0;
        }
        if(periodo==2){
            return Math.round((energiaGerada*365) * 100.0) / 100.0;
        }

        return null;
    }

    public GhiDTO findByLatLon(String latitude, String longitude) throws IOException, CsvException {
        CordenadasDTO cordenadas = new CordenadasDTO(latitude, longitude);

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
        // Pega Cordenadas formatadas
        cordenadas = cordenadas.getCordenadasToSearch();

        // Converter latitude e longitude para double e arredondar
        double targetLat = Math.round(Double.parseDouble(cordenadas.getLatitude()) * 10.0) / 10.0;
        double targetLon = Math.round(Double.parseDouble(cordenadas.getLongitude()) * 100.0) / 100.0;

        // Definir um limite de tolerância para encontrar valores próximos
        double tolerance = 0.3; // Ajuste conforme necessário

        // Pesquisar na lista pelo objeto com a latitude e longitude aproximadas
        assert ghis != null;
        GhiDTO closestGhi = null;
        double minDistance = Double.MAX_VALUE;
        for (GhiDTO ghi : ghis) {
            double lat = Double.parseDouble(ghi.getLat());
            double lon = Double.parseDouble(ghi.getLon());
            double distance = Math.sqrt(Math.pow(lat - targetLat, 2) + Math.pow(lon - targetLon, 2));
            if (distance < tolerance && distance < minDistance) {
                closestGhi = ghi;
                minDistance = distance;
            }
        }

        // Retornar o objeto mais próximo encontrado
        return closestGhi;
    }

    private double calculateSolarExposureHours(double latitude, double longitude) {
        Calendar calendar = Calendar.getInstance();
        int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
        TimeZone timeZone = calendar.getTimeZone();
        LocalDate date = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            date = LocalDate.ofEpochDay(calendar.get(Calendar.DATE));
        }

        // Calcular a declinação solar
        double declination = calculateSolarDeclination(dayOfYear);

        // Calcular o ângulo de elevação solar ao nascer e ao pôr do sol
        double solarElevationAtSunrise = calculateSolarElevation(latitude, longitude, declination, -90, timeZone);
        double solarElevationAtSunset = calculateSolarElevation(latitude, longitude, declination, 90, timeZone);

        // Calcular as horas de exposição solar

        return (solarElevationAtSunset - solarElevationAtSunrise) / 15.0;
    }

    // Método para calcular a declinação solar
    private static double calculateSolarDeclination(int dayOfYear) {
        return 23.45 * Math.sin(Math.toRadians(360.0 * (284.0 + dayOfYear) / 365.0));
    }

    // Método para calcular o ângulo de elevação solar
    private static double calculateSolarElevation(double latitude, double longitude, double declination, double hourAngle, TimeZone timeZone) {
        // Calcular o ângulo horário
        double solarTime = hourAngle + 4 * (longitude - 15 * timeZone.getRawOffset() / 3600.0);
        double solarHourAngle = solarTime * 15; // Convertendo para graus

        // Calcular o ângulo de elevação solar

        return Math.toDegrees(Math.asin(Math.sin(Math.toRadians(latitude)) *
                Math.sin(Math.toRadians(declination)) + Math.cos(Math.toRadians(latitude)) *
                Math.cos(Math.toRadians(declination)) * Math.cos(Math.toRadians(solarHourAngle))));
    }
}