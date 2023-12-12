package com.LuisRamos.weather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.LuisRamos.weather.services.model.Registrar;
import com.LuisRamos.weather.services.model.Root;
import com.LuisRamos.weather.services.model.WeatherService;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {


    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private GoogleMap myMap;


    private EditText txtCountryISOCode = null;
    private EditText txtCityName = null;
    private TextView lblCurrent = null;
    private TextView lblMin = null;
    private TextView lblMax = null;

    private WeatherService service = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        initViews();

        service = new WeatherService("a9cf0f7a3cc84a884d84d4df48f057c2");

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(@NonNull Location location) {

                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                    if (addresses.size() > 0) {
                        String cityName = addresses.get(0).getLocality();
                        String countryName = addresses.get(0).getCountryName();
                        String address = cityName + ", " + countryName;
                        Toast.makeText(MainActivity.this, "Ubicacion actual: " + address, Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
                Toast.makeText(MainActivity.this, "Habilita el proveedor de ubicación", Toast.LENGTH_SHORT).show();

            }
        };
        if (ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                android.content.pm.PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION
            );
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 0, locationListener);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 0, locationListener);
            } else {
                Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }







    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        myMap=googleMap;
        LatLng ubicacion =new  LatLng(-30.60458663024283, -71.20476553625748);
        myMap.addMarker(new MarkerOptions().position(ubicacion).title("IP santo tomas"));

        myMap.moveCamera(CameraUpdateFactory.newLatLng(ubicacion));
        myMap.moveCamera(CameraUpdateFactory.zoomTo(15));

        myMap.getUiSettings().setZoomControlsEnabled(true);
        myMap.getUiSettings().setCompassEnabled(true);

        myMap.getUiSettings().setZoomGesturesEnabled(true);
        myMap.getUiSettings().setScrollGesturesEnabled(true);

        myMap.setOnMapClickListener(this);



    }


@Override
public void onMapClick(LatLng latLng){
        double latitud = latLng.latitude;
        double longitud = latLng.longitude;

        Log.d("MapClick","Latitud:" + latitud+ ",Longitud: "+longitud);

        myMap.addMarker((new MarkerOptions().position(latLng).title("UBICACION Clickeada")));

}

    public  void initViews(){
        txtCountryISOCode = findViewById(R.id.txtCountryISOCode);
        txtCityName = findViewById(R.id.txtCityName);

        lblCurrent = findViewById(R.id.lblActual);
        lblMin = findViewById(R.id.lblMin);
        lblMax = findViewById(R.id.lblMax);
    }

    public void btnGetInfoOnClick(View view){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        StringBuilder text = new StringBuilder();

        if(txtCountryISOCode.getText().toString().isEmpty() || txtCityName.getText().toString().isEmpty()){
                text.append(getString(R.string.Fields_cannot_be_empty));
            alert.setMessage(text);
            alert.setPositiveButton("Cerrar",null);

            alert.show();
        }else{
            getWeatherInfo(txtCityName.getText().toString(),txtCountryISOCode.getText().toString());
        }

    }

    public void getWeatherInfo(String cityName, String countryISOCode){
        service.requestWeatherData(cityName, countryISOCode,(isNetworkError,statusCode, root) -> {
            if(!isNetworkError){
                if(statusCode == 200){
                    showWeatherInfo(root);
                }else{
                    Log.d("Weather", "Error de servicio");
                }
            }else{
                Log.d("Weather", "Error de red");
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public  void showWeatherInfo(Root root) {
        String temp =  String.valueOf(root.getMain().getTemp());
        String tempMin =  String.valueOf(root.getMain().getTempMin());
        String tempMax =  String.valueOf(root.getMain().getTempMax());

        lblCurrent.setText(getString(R.string.current)+" "+temp);
        lblMin.setText(getString(R.string.minimum)+" "+tempMin);
        lblMax.setText(getString(R.string.maximum)+" "+tempMax);
    }
}