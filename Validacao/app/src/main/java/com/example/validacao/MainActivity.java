package com.example.validacao;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity implements
        Response.Listener,
        Response.ErrorListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = getSharedPreferences(
                "com.example.app", Context.MODE_PRIVATE);
        boolean manterLogado = prefs.getBoolean("manterLogado",false);
        if(manterLogado){
            Intent k = new Intent(MainActivity.this, ClientesActivity.class);
            startActivity(k);
        }
        setContentView(R.layout.activity_main);
        Button loginButton = findViewById(R.id.button);
        Button btnSenha = findViewById(R.id.btnSenha);
        btnSenha.setOnClickListener(view ->{
            Intent k = new Intent(MainActivity.this, RecSenhaActivity.class);
            startActivity(k);
        });
        loginButton.setOnClickListener(view -> {
            EditText loginText = findViewById(R.id.editTextTextEmailAddress);
            EditText password = findViewById(R.id.editTextTextPassword);


            RequestQueue queue = VolleySingleton.getInstance(view.getContext()).getRequestQueue();
            String url = "http://192.168.2.100:3000/usuario/login";

            JsonObjectRequest jsObjRequest =
                    null;

            try {
                jsObjRequest = new JsonObjectRequest(
                        Request.Method.POST, // Requisição via HTTP_GET
                        url,   // url da requisição
                        new JSONObject(String.format("{\"email\":\"%s\",\"password\":\"%s\"}",loginText.getText().toString(),password.getText().toString())),  // JSONObject a ser enviado via POST
                        this,  // Response.Listener
                        this);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            queue.add(jsObjRequest);
        });
            }

    @Override
    public void onErrorResponse(VolleyError error) {
        String message = "";
        if(error.networkResponse.statusCode == 401){
            message = "Email ou senha incorretos!";
        }else{
            message ="Erro no processo de login.";
        }
        Snackbar.make(getWindow().getDecorView().getRootView(), message, 3000).show();
    }

    @Override
    public void onResponse(Object response) {
        JSONObject obj = null;
        try {
            obj = new JSONObject(response.toString());
            SharedPreferences prefs = getSharedPreferences(
                    "com.example.app", Context.MODE_PRIVATE);
            CheckBox manterLogadoCheckBox = findViewById(R.id.checkBox);
            manterLogadoCheckBox.isChecked();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("token",obj.getString("accessToken"));
            editor.putBoolean("manterLogado",manterLogadoCheckBox.isChecked());
            editor.apply();
            Intent k = new Intent(MainActivity.this, ClientesActivity.class);
            startActivity(k);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}