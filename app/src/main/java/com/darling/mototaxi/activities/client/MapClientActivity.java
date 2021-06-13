package com.darling.mototaxi.activities.client;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.darling.mototaxi.R;
import com.darling.mototaxi.activities.MainActivity;
import com.darling.mototaxi.includs.MyToolbar;
import com.darling.mototaxi.providers.AuthProvider;
import com.darling.mototaxi.providers.GeoFireProvider;
import com.darling.mototaxi.providers.TokenProvider;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapClientActivity extends AppCompatActivity implements OnMapReadyCallback {


    private GoogleMap mMap;
    private SupportMapFragment mMapFragment;
    private AuthProvider mAuthProvider;

    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocation;
    private GeoFireProvider mGeofireProvider;
    private final static int LOCATION_REQUEST_CODE = 1;
    private final static int SETTING_REQUEST_CODE = 2;

    //icono
    private Marker mMarker;
    private LatLng mCurrentLatlng;
    private List <Marker> mDriversMarkers = new ArrayList<>();
    private boolean mIsFirstTime =true;
    private AutocompleteSupportFragment mAutocomplete;
    private AutocompleteSupportFragment mAutocompleteDestination;
    private PlacesClient mPlaces;
    //almacena el origen y destino
    private String mOrigin;
    private  LatLng mOriginLatLn;

    private String mDestination;
    private  LatLng mDestinationLatLn;

    //lugares
    private GoogleMap.OnCameraIdleListener mCameraListener;

    private Button mButtonRequestDriver;

    private TokenProvider mTokenProvider;


    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            for (Location location : locationResult.getLocations()) {
                if (getApplicationContext() != null) {

                    mCurrentLatlng = new LatLng(location.getLatitude(), location.getLongitude());

                    /*

                    if (mMarker != null){
                        mMarker.remove();
                    }
                    mMarker = mMap.addMarker(new MarkerOptions().position(
                            new LatLng(location.getLatitude(), location.getLongitude())
                            )
                                    .title("tu posicion")
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.iconoposicion1))
                    ); */
                    //obtenemos la localizacion del usuario en tiempo real
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                    .zoom(15F)
                                    .build()
                    ));
                    if (mIsFirstTime){
                        mIsFirstTime= false;
                        getActiveDriver();
                        limitSearch();
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_client);
        MyToolbar.show(this, "Mapa del Cliente", false);
        mAuthProvider = new AuthProvider();
        //con esto iniciamos o detenemos la ubicacion
        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);
        //instancia a las variables
        mGeofireProvider = new GeoFireProvider();
        mTokenProvider = new TokenProvider();
        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
        mButtonRequestDriver = findViewById(R.id.btnRequestDriver);

        if (!Places.isInitialized()){
            Places.initialize(getApplicationContext(), getResources().getString(R.string.google_maps_key));
        }
        mPlaces = Places.createClient(this);

        //llamamos a los metodos
        instanceAutoCompleteOrigin();
        instanceAutoCompleteDestination();
        onCameraMove();

        mButtonRequestDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestDriver();
            }
        });

        generateToken();
    }

    private void requestDriver() {

        if( mOriginLatLn != null  && mDestinationLatLn != null ){
            Intent intent = new Intent(MapClientActivity.this, DetailRequestActivity.class);
            intent.putExtra("origin_lat", mOriginLatLn.latitude);
            intent.putExtra("origin_lng", mOriginLatLn.longitude);
            intent.putExtra("destination_lat", mDestinationLatLn.latitude);
            intent.putExtra("destination_lng", mDestinationLatLn.longitude);
            intent.putExtra("origin", mOrigin);
            intent.putExtra("destination", mDestination);

            startActivity(intent);
        }
        else {
            Toast.makeText(this,"Selecciona el lugar de recogida y el destino", Toast.LENGTH_SHORT).show();
        }
    }

    //limitar por regiones
    private void limitSearch(){
        LatLng northSide = SphericalUtil.computeOffset(mCurrentLatlng, 5000, 0);
        LatLng southSide = SphericalUtil.computeOffset(mCurrentLatlng, 5000, 180);
        //estbleccer el pais
        mAutocomplete.setCountry("Mex");
        mAutocomplete.setLocationBias(RectangularBounds.newInstance(southSide, northSide));
        mAutocompleteDestination.setCountry("Mex");
        mAutocompleteDestination.setLocationBias(RectangularBounds.newInstance(southSide, northSide));

    }

    private void onCameraMove(){
        mCameraListener = new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                try {
                    Geocoder geocoder = new Geocoder(MapClientActivity.this);
                    mOriginLatLn = mMap.getCameraPosition().target;
                    List<Address> addressList = geocoder.getFromLocation(mOriginLatLn.latitude, mOriginLatLn.longitude, 1);
                    String city = addressList.get(0).getLocality();
                    String country = addressList.get(0).getCountryName();
                    String address = addressList.get(0).getAddressLine(0);
                    mOrigin = address + " " + city;
                    mAutocomplete.setText(address + " " + city);

                } catch (Exception e){
                    Log.d("Error :", "Mensaje error:" + e.getMessage());
                }
            }
        };
    }

    private void instanceAutoCompleteOrigin(){

        mAutocomplete = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.placeAutocompleteOrigin);
        mAutocomplete.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));
        //cambiamos nombres de los datos
        mAutocomplete.setHint("Origen");
        mAutocomplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                //aqui haces para obtener el nombre del lugar y la latitud del lugar
                mOrigin = place.getName();
                mOriginLatLn = place.getLatLng();
                Log.d("PLACE", "Name:" + mOrigin);
                Log.d("PLACE", "Lat:" + mOriginLatLn.latitude);
                Log.d("PLACE", "Lng:" + mOriginLatLn.longitude);

            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });

    }

    public void instanceAutoCompleteDestination(){

        mAutocompleteDestination = (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.placeAutocompleteDestination);
        mAutocompleteDestination.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));
        mAutocompleteDestination.setHint("Destino");
        mAutocompleteDestination.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                //aqui haces para obtener el nombre del lugar y la latitud del lugar
                mDestination = place.getName();
                mDestinationLatLn = place.getLatLng();
                Log.d("PLACE", "Name:" + mDestination);
                Log.d("PLACE", "Lat:" + mDestinationLatLn.latitude);
                Log.d("PLACE", "Lng:" + mDestinationLatLn.longitude);

            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if (gpsActived()){
                        mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                        mMap.setMyLocationEnabled(true);
                    } else {
                        showAlertDialogNOGPS();
                    }
                } else {
                    checkLocationPermission();
                }
            } else {
                checkLocationPermission();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTING_REQUEST_CODE && gpsActived()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            mMap.setMyLocationEnabled(true);
        } else if (requestCode == SETTING_REQUEST_CODE && gpsActived()){
            showAlertDialogNOGPS();
        }
    }

    //mostrar dialog para las configuraciones

    private void showAlertDialogNOGPS(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Activa la ubicacion para continuar")
                .setPositiveButton("Configuraciones", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), SETTING_REQUEST_CODE);
                    }
                })
                .create()
                .show();
    }

    //si tiene activo el gps del dispostivo o no
    private boolean gpsActived(){
        boolean isActive = false;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            isActive=true;
        }
        return isActive;
    }

    //metodo para la ubicacion
    private void startLocation(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (gpsActived()){

                    mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                    mMap.setMyLocationEnabled(true);
                }
                else {
                    showAlertDialogNOGPS();
                }

            } else {
                checkLocationPermission();
            }
        }else{
            if (gpsActived()){

                mFusedLocation.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                mMap.setMyLocationEnabled(true);
            }
            else {
                showAlertDialogNOGPS();
            }
        }
    }
    private void checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                new AlertDialog.Builder(this)
                        .setTitle("Proporciona los permisos para continuar")
                        .setMessage("Esta aplicacion requiere de los permisos de ubicacion")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //nos habilita los permisos para la ubicacion del dispositivo :V
                                ActivityCompat.requestPermissions(MapClientActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
                            }
                        })
                        //para mostrar el alertdialog
                        .create()
                        .show();
            }
            else {
                ActivityCompat.requestPermissions(MapClientActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);

            }
        }
    }

    private void getActiveDriver(){
        mGeofireProvider.getActiveDriver(mCurrentLatlng, 10).addGeoQueryDataEventListener(new GeoQueryDataEventListener() {
            @Override
            public void onDataEntered(DataSnapshot dataSnapshot, GeoLocation location) {

                //añadiremos los  marcares de los mototaxistas que se conecten en la app
                for( Marker marker: mDriversMarkers){
                    if (marker.getTag() != null){
                        if (marker.getTag().equals(dataSnapshot)){
                            return;
                        }
                    }
                }
                LatLng driverLatLng = new LatLng(location.latitude, location.longitude);
                Marker marker = mMap.addMarker(new MarkerOptions().position(driverLatLng).title("Mototaxi Disponible").icon(BitmapDescriptorFactory.fromResource(R.drawable.iconomoto)));
                marker.setTag(dataSnapshot);
                mDriversMarkers.add(marker);
            }

            @Override
            public void onDataExited(DataSnapshot dataSnapshot) {

                //añadiremos los  marcares de los mototaxistas que se conecten en la app
                for( Marker marker: mDriversMarkers){
                    if (marker.getTag() != null){
                        if (marker.getTag().equals(dataSnapshot)){
                            marker.remove();
                            mDriversMarkers.remove(marker);
                            return;
                        }
                    }
                }

            }

            @Override
            public void onDataMoved(DataSnapshot dataSnapshot, GeoLocation location) {
                //actualizar la posicion de los mototaxis

                //añadiremos los  marcares de los mototaxistas que se conecten en la app
                for( Marker marker: mDriversMarkers){
                    if (marker.getTag() != null){
                        if (marker.getTag().equals(dataSnapshot)){
                            marker.setPosition(new LatLng(location.latitude, location.longitude));
                        }
                    }
                }

            }

            @Override
            public void onDataChanged(DataSnapshot dataSnapshot, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

    }


        @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnCameraIdleListener(mCameraListener);

        mLocationRequest = new LocationRequest();
        //es el intervalo de tiempo que se estara actualizando la ubicacion
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(5);

        startLocation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.driver_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()== R.id.action_logout){
            logout();

        }
        return  super.onOptionsItemSelected(item);
    }
    void logout(){
        mAuthProvider.logout();
        //al momento de cerrar sesion nos envia al mainactivity
        Intent intent = new Intent(MapClientActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    void generateToken(){
        mTokenProvider.create(mAuthProvider.getId());
    }
}