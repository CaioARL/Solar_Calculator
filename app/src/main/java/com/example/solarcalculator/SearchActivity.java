package com.example.solarcalculator;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.solarcalculator.dto.BoundingBoxDTO;
import com.example.solarcalculator.dto.PlaceDTO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SearchActivity extends AppCompatActivity {
    OkHttpClient client;
    TextView textAddress;
    TextView textLat;
    TextView textLon;
    WebView webView;
    Button btnBack;
    Button btnCalculate;
    TextView textError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializa elementos da tela e atribui as funções para os botões
        this.initElements();
        this.setButtonFunctions();

        // Pega Intent(valores da tela anterior (MainActivity)) e realiza busca do endeço e preenchimento dos campos
        Intent intent = getIntent();
        if(intent != null) {
            String address = intent.getStringExtra("ADDRESS");
            doGet(address);
        }
    }

    private void initElements() {
        // Configurango OkHttpClient
        client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS) // Tempo de conexão
                .writeTimeout(30, TimeUnit.SECONDS)   // Tempo de escrita
                .readTimeout(30, TimeUnit.SECONDS)    // Tempo de leitura
                .build();

        btnBack = findViewById(R.id.btnBack);
        btnCalculate = findViewById(R.id.btnCalculate);
        textAddress = findViewById(R.id.textAddress);
        textLat = findViewById(R.id.textLat);
        textLon = findViewById(R.id.textLon);
        webView = findViewById(R.id.webView);
    }

    private void setButtonFunctions(){
        // Botão calcular
        btnCalculate.setOnClickListener(v -> {
            String latitude = textLat.getText().toString();
            String longitude = textLon.getText().toString();

            // Se endereço não encontrado, não permitir calculo
            if (!latitude.isEmpty() && !longitude.isEmpty() && !(latitude.equals("0.0000000") && longitude.equals("0.0000000"))) {
                Intent intent = new Intent(SearchActivity.this, CalculateActivity.class);
                intent.putExtra("LATITUDE", latitude);
                intent.putExtra("LONGITUDE", longitude);
                startActivity(intent);
            }
        });

        // Botão voltar
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(SearchActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    // Gera url baseada no endereço informado
    private String getUrl(String address){
        return "https://nominatim.openstreetmap.org/search.php?q=" + address + "&format=jsonv2";
    }

    public void doGet(String address){
        // Pega url dinâmica
        Request request = new Request.Builder().url(getUrl(address)).build();
        client.newCall(request).enqueue(new Callback() {
            // Caso ocorra erro preenche texto genérico
            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    textError = findViewById(R.id.textViewError);
                    textError.setText("Erro ao tentar buscar informações do endereço, por favor tente mais tarde!");
                    Log.e("TAG", "Erro no metodo doGet" + e);
                    Toast.makeText(SearchActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                runOnUiThread(() -> {
                    try {
                        textError = findViewById(R.id.textViewError);

                        assert response.body() != null;

                        String jsonResponse = response.body().string();

                        Gson gson = new Gson();
                        Type listType = new TypeToken<List<PlaceDTO>>(){}.getType();
                        List<PlaceDTO> placeList = gson.fromJson(jsonResponse, listType);

                        // Retorna lista de PlaceDTO
                        if (!placeList.isEmpty()) {
                            PlaceDTO dto = placeList.get(0);

                            // Carrega mapa do endereço
                            loadOpenStreetMap(convertToBoundingBoxDTO(dto.getBoundingbox()));

                            // Atribui os devidos valores aos campos
                            textAddress.setText(dto.getDisplayName());
                            textLat.setText(dto.getLat());
                            textLon.setText(dto.getLon());
                            textError.setTextColor(getResources().getColor(R.color.red));
                            textError.setText("Caso necessário altere os valor de configurações no menu superior da tela inicial");
                        }
                        else{
                            textAddress.setText("Endereço não encontrado");
                            textLat.setText("0.0000000");
                            textLon.setText("0.0000000");
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            }

        });
    }

    // Acessa url com coordenas e mostra na view o mapa da localidade
    @SuppressLint({"SetJavaScriptEnabled", "SetTextI18n"})
    private void loadOpenStreetMap(BoundingBoxDTO bound) {
        try {
            String mapUrl = "https://www.openstreetmap.org/export/embed.html?bbox=" + bound.getMinLongitude() + "%2C" + bound.getMinLatitude() + "%2C"
                    + bound.getMaxLongitude() + "%2C" + bound.getMaxLatitude() + "&layer=mapnik";

            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadUrl(mapUrl);
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private BoundingBoxDTO convertToBoundingBoxDTO(List<String> boundingbox) {
        if (boundingbox == null || boundingbox.size() != 4) {
            return null;
        }
        BoundingBoxDTO boundingBoxDTO = new BoundingBoxDTO();
        boundingBoxDTO.setMinLatitude(boundingbox.get(0));
        boundingBoxDTO.setMaxLatitude(boundingbox.get(1));
        boundingBoxDTO.setMinLongitude(boundingbox.get(2));
        boundingBoxDTO.setMaxLongitude(boundingbox.get(3));

        return boundingBoxDTO;
    }
}