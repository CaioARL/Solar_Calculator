package com.example.solarcalculator.utils;

import android.location.Address;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GeocoderUtils {
    // Geocoder para pegar endereço com base nas coordenadas
    public static String getAddressFromLocation(Location location, AppCompatActivity activity) {
        android.location.Geocoder geocoder = new android.location.Geocoder(activity, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String streetAddress = address.getAddressLine(0);
                String coordinatesAddress = address.getLatitude() + " " + address.getLongitude();
                Toast.makeText(activity, streetAddress, Toast.LENGTH_SHORT).show();
                return coordinatesAddress;
            } else {
                Toast.makeText(activity, "Address not found", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            Log.e("TAG", "Erro no metodo getAddressFromLocation" + e);
            Toast.makeText(activity, "Unable to get street address", Toast.LENGTH_SHORT).show();
        }
        return null;
    }
}
