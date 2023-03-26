package com.example.validacao;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class RecSenhaActivity extends AppCompatActivity  implements Response.Listener,
        Response.ErrorListener {
    public static int count = 0;
    private double lat;
    private double lng;
    private FusedLocationProviderClient fusedLocationClient;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rec_senha);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        isLocationPermissionGranted();
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                     if (location != null) {
                         lat =location.getLatitude();
                        lng=location.getLongitude();
                        }
                    }
                });
        Button btnOpenCamera = findViewById(R.id.button2);
        btnOpenCamera.setOnClickListener(view -> {

            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, 9);
        });


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 9 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
             System.out.println(lat);
             System.out.println(lng);
            RequestQueue queue = VolleySingleton.getInstance(getBaseContext()).getRequestQueue();
            String url = "http://192.168.2.100:3000/usuario/recsenha";
            EditText etEmail = findViewById(R.id.editTextTextEmailAddress3);
            String user = etEmail.getText().toString();
            if(user.length() == 0 || user == null || !user.contains("@")){
                Snackbar.make(getWindow().getDecorView().getRootView(), "Preencha os campos corretamente.", 3000).show();
return;
            }
            JsonObjectRequest jsObjRequest =
                    null;
            JSONObject body = new JSONObject();
            try {
                body.put("user",user);
                body.put("lat",lat);
                body.put("long",lng);
                body.put("image",encoded);
            } catch (JSONException e) {
                e.printStackTrace();
            }

                jsObjRequest = new JsonObjectRequest(
                        Request.Method.POST, // Requisição via HTTP_GET
                        url,   // url da requisição
                        body,
                        this,  // Response.Listener
                        this);

            queue.add(jsObjRequest);

        }
    }


    private boolean isLocationPermissionGranted() {
      if(ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
        ) {
          String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION
          };
            ActivityCompat.requestPermissions(
                    this,permissions,
                    0
            );
            return false;
        } else {
            return true;
        }
    }
    @Override
    public void onErrorResponse(VolleyError error) {
        String message = "";
        if(error.networkResponse.statusCode == 401){
            message = "Email ou senha incorretos!";
        }else{
            message ="Erro no processo de recuperação de senha.";
        }
        Snackbar.make(getWindow().getDecorView().getRootView(), message, 3000).show();
    }

    @Override
    public void onResponse(Object response) {
        Snackbar.make(getWindow().getDecorView().getRootView(), "Imagem enviada.", 3000).show();

    }



}