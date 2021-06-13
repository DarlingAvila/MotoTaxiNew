package com.darling.mototaxi.providers;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class GeoFireProvider {
    private DatabaseReference mDatabase;
    private GeoFire mGeoFire;


    public GeoFireProvider() {
        //aqui se muestra la ramas de los activos motaxis
       mDatabase = FirebaseDatabase.getInstance().getReference().child("active_mototaxis");
       mGeoFire = new GeoFire(mDatabase);
    }
    //nos permite guara la localizacion de los usuarios
    public void saveLocation(String idDriver, LatLng latLng){
        mGeoFire.setLocation(idDriver, new GeoLocation(latLng.latitude, latLng.longitude));
    }

    //remueve la localizacion
    public void removeLocation(String idDriver){
        mGeoFire.removeLocation(idDriver);

    }
    public GeoQuery getActiveDriver(LatLng latLng, double radius){
        GeoQuery geoQuery = mGeoFire.queryAtLocation(new GeoLocation(latLng.latitude, latLng.longitude), radius );
        geoQuery.removeAllListeners();
        return geoQuery;
    }
}
