package com.example.solarcalculator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    SharedPreferences preferences;

    EditText editAddress;

    Button btnSearch;

    Toolbar toolbar;

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

        setSharedPreferences();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        editAddress = findViewById(R.id.editAddress);

        btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            intent.putExtra("ADDRESS", editAddress.getText().toString());
            startActivity(intent);
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if(itemId==R.id.menuPanel){
            Intent intent = new Intent(MainActivity.this, PanelActivity.class);
            startActivity(intent);
        }

        if(itemId==R.id.menuEnergy){
            Intent intent = new Intent(MainActivity.this, EnergyActivity.class);
            startActivity(intent);
        }

        if(itemId==R.id.menuExit){
            finishAffinity();
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.options_menu, menu);

        return true;
    }

    private void setSharedPreferences(){
        preferences = getSharedPreferences("saved_info", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();

        if(!preferences.contains("taxa") && !preferences.contains("preco")){
            editor.remove("taxa");
            editor.putFloat("taxa", 20F);
            editor.putFloat("preco", 0.5F);
            editor.apply();
        }
    }
}