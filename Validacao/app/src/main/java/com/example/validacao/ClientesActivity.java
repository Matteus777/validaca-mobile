package com.example.validacao;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.validacao.databinding.ActivityClientesBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientesActivity extends AppCompatActivity
        implements
        SwipeRefreshLayout.OnRefreshListener,
        Response.ErrorListener {

    private AppBarConfiguration appBarConfiguration;
    private ActivityClientesBinding binding;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ClienteAdapter adapter;
    private ListView lvClientes;
    private List<ClienteEntity> listaDeClientes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientes);
        lvClientes =  findViewById(R.id.lvClientes);
        listaDeClientes = new ArrayList<>();
        adapter = new ClienteAdapter(ClientesActivity.this,listaDeClientes);
        lvClientes.setAdapter(adapter);

        binding = ActivityClientesBinding.inflate(getLayoutInflater());

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getData();
    }

    @Override
    public void onRefresh() {
                getData();
    }


    private void getData(){

        RequestQueue queue = VolleySingleton.getInstance(ClientesActivity.this).getRequestQueue();
        String url = "http://192.168.2.100:3000/clients";

        JsonArrayRequest jsObjRequest =
                null;

        jsObjRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
    null,
                new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                listaDeClientes.clear();
                try {
                    for(int i = 0; i < jsonArray.length(); i++) {


                        JSONObject jsonobject = jsonArray.getJSONObject(i);

                        int id = jsonobject.getInt("id");
                        String name = jsonobject.getString("name");
                        String document = jsonobject.getString("document");
                        ClienteEntity cliente = new ClienteEntity();
                        cliente.setId(id);
                        cliente.setName(name);
                        cliente.setDocument(document);
                        listaDeClientes.add(cliente);
                        adapter.notifyDataSetChanged();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mSwipeRefreshLayout.setRefreshing(false);

            }
                    },this){
            @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            HashMap<String, String> headers = new HashMap<String, String>();
                SharedPreferences prefs = getSharedPreferences(
                        "com.example.app", Context.MODE_PRIVATE);
                String token = prefs.getString("token","");
            headers.put("Authorization", String.format("Bearer %s",token));
            return headers;
        }};
        queue.add(jsObjRequest);
        };

    @Override
    public void onErrorResponse(VolleyError error) {
        mSwipeRefreshLayout.setRefreshing(false);
        String message = "";
        if(error.networkResponse.statusCode == 401){
            message = "teste!";
        }else{
            message ="Erro no processo de login.";
        }
        Snackbar.make(findViewById(android.R.id.content).getRootView(), message, 3000).show();
    }



    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

}

