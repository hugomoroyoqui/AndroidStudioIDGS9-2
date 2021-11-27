package com.ut.firebaseejemplo.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ut.firebaseejemplo.Activities.FirebaseList;
import com.ut.firebaseejemplo.R;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TestFirebase extends Fragment {

    View view;

    Button btnSendCoordinates;
    String TAG = "firebaseTest";

    Button goToList;

    // Declaramos proveedor
    //FusedLocationProviderClient fusedLocationClient;

    double latitude = 0, longitude = 0;

    FusedLocationProviderClient fusedLocationClient;
    LocationCallback locationCallback;

    // Declaracion
    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_test_firebase, container, false);

        // inicializacion
        db = FirebaseFirestore.getInstance();

        goToList = view.findViewById(R.id.btnGoToList);

        goToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToListActivity();
            }
        });

        btnSendCoordinates = view.findViewById(R.id.btnSendCoordinates);

        btnSendCoordinates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCoordinates();
            }
        });

        // Inicializamos proveedor
        /*fusedLocationClient = LocationServices
                .getFusedLocationProviderClient(getContext());*/

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {

                if (locationResult == null){
                    return;
                }

                for (Location location : locationResult.getLocations()){
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    Toast.makeText(getContext(),
                            "" + latitude + ", " + longitude,
                            Toast.LENGTH_SHORT).show();

                    Log.d("LocationTest", "" + latitude + ", " + longitude);
                }

            }
        };

        return view;
    }

    private void goToListActivity() {
        startActivity(new Intent(getContext(), FirebaseList.class));
    }

    public void sendCoordinates(){
        Map<String, Object> coordinates = new HashMap<>();
        coordinates.put("latitude", latitude);
        coordinates.put("longitude", longitude);
        coordinates.put("date", new Date());

        db.collection("Coordinates")
        .add(coordinates)
        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                Toast.makeText(getContext(),
                        "ID: " + documentReference.getId(), Toast.LENGTH_SHORT).show();
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error adding document", e);
                Toast.makeText(getContext(),
                        "Error: " + e, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        checkPermission();
    }

    @Override
    public void onPause() {
        super.onPause();

        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    private void checkPermission() {
        int permCode = 120;

        String[] perms = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        int accessFineLocation = getActivity()
                .checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);

        int accessCoarseLocation = getActivity()
                .checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);

        if (accessFineLocation == PackageManager.PERMISSION_GRANTED &&
                accessCoarseLocation == PackageManager.PERMISSION_GRANTED) {
            checkGPSSensor();
        } else {
            requestPermissions(perms, permCode);
        }
    }

    private void checkGPSSensor() {
        LocationManager locationManager = (LocationManager)
                getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            getCoordinates();
        } else {
            alertNoGPS();
        }
    }

    private void alertNoGPS() {
        // Inicializacion
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Titulo
        builder.setTitle("Solicitud de permisos");

        // Mensaje
        builder.setMessage("Se requieren los permisos de ubicacion");

        // Boton aceptar
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });

        // boton cancelar
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        // Crear y mostrar
        AlertDialog alert = builder.create();
        alert.show();

    }

    private void getCoordinates() {
        if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkPermission();
        } else {
            /*fusedLocationClient.getLastLocation()
            .addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    Log.d("locationTest", latitude + ", " + longitude);
                }
            });*/

            fusedLocationClient = LocationServices
                    .getFusedLocationProviderClient(getActivity());


            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(20000);
            locationRequest.setFastestInterval(10000);

            fusedLocationClient.requestLocationUpdates(
                   locationRequest,
                   locationCallback,
                    Looper.getMainLooper()
            );

        }
    }


}