package com.example.solarcalculator;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    SharedPreferences preferences;
    EditText editAddress;
    Button btnSearch;
    Toolbar toolbar;
    SwitchCompat locationSwitch;
    FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializa o cliente de localização
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Gerencia valores em cache
        setSharedPreferences();

        // Inicializa elementos da tela e atribui as funções para os botões
        this.initElements();
        this.setButtonFunctions();

        // Verifica e solicita permissão de localização
        checkLocationPermission();
    }

    // Atribui funcões para os campos do menu superior quando selecionados
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menuPanel) {
            Intent intent = new Intent(MainActivity.this, PanelActivity.class);
            startActivity(intent);
        }
        if (itemId == R.id.menuEnergy) {
            Intent intent = new Intent(MainActivity.this, EnergyActivity.class);
            startActivity(intent);
        }
        if (itemId == R.id.menuExit) {
            finishAffinity();
        }
        return super.onOptionsItemSelected(item);
    }

    // Cria menu superior
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    // Ao abrir tela para permitir localização
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissão concedida, pode acessar a localização
                locationSwitch.setVisibility(SwitchCompat.VISIBLE);
            } else {
                // Permissão negada, lidar com o caso de permissão negada
                locationSwitch.setVisibility(SwitchCompat.GONE);
            }
        }
    }

    // ##PRIVATES##
    private void initElements() {
        toolbar = findViewById(R.id.toolbar);
        editAddress = findViewById(R.id.editAddress);
        btnSearch = findViewById(R.id.btnSearch);

        // Switch não visível enquanto permissão de localização != true
        locationSwitch = findViewById(R.id.locationSwitch);
        locationSwitch.setVisibility(SwitchCompat.GONE);

        setSupportActionBar(toolbar);
    }

    private void setButtonFunctions() {
        // Botão search
        btnSearch.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            intent.putExtra("ADDRESS", editAddress.getText().toString());
            startActivity(intent);
        });

        // Switch usar localização atual
        locationSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                getLastLocation();
                editAddress.setVisibility(EditText.GONE);
            } else {
                editAddress.setText(null);
                editAddress.setVisibility(EditText.VISIBLE);
            }
        });
    }

    // Geocoder para pegar endereço com base nas coordenadas
    private void getAddressFromLocation(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String streetAddress = address.getAddressLine(0);
                String coordinatesAddress = address.getLatitude() + " " + address.getLongitude();
                editAddress.setText(coordinatesAddress);
                Toast.makeText(MainActivity.this, streetAddress, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.this, "Address not found", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            Log.e("TAG", "Erro no metodo getAddressFromLocation" + e);
            Toast.makeText(this, "Unable to get street address", Toast.LENGTH_LONG).show();
        }
    }

    // Pega valores salvos anteriormente ou atribui padrões caso primeira entrada
    private void setSharedPreferences() {
        preferences = getSharedPreferences("saved_info", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        if (!preferences.contains("taxa")) editor.putFloat("taxa", 20F);
        if (!preferences.contains("preco")) editor.putFloat("preco", 0.5F);
        if (!preferences.contains("periodo")) editor.putInt("periodo", 0);
        if (!preferences.contains("qtde_celula")) editor.putInt("qtde_celula", 1);
        if (!preferences.contains("area_celula")) editor.putFloat("area_celula", 1.5F);
        if (!preferences.contains("incidencia")) editor.putInt("incidencia", 0);
        editor.apply();
    }

    // Verifica e solicita permissão de localização
    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permissão não foi concedida, solicita a permissão
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            locationSwitch.setVisibility(SwitchCompat.VISIBLE);
        }
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        fusedLocationClient.getLastLocation().addOnCompleteListener(this, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Location location = task.getResult();
                getAddressFromLocation(location);
            } else {
                Toast.makeText(MainActivity.this, "Failed to get location", Toast.LENGTH_LONG).show();
            }
        });
    }

}
