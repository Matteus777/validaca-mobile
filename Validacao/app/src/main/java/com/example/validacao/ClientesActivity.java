package com.example.validacao;

import static android.net.NetworkCapabilities.TRANSPORT_CELLULAR;
import static android.net.NetworkCapabilities.TRANSPORT_LOWPAN;
import static android.net.NetworkCapabilities.TRANSPORT_WIFI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.NetworkOnMainThreadException;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientesActivity extends AppCompatActivity
        implements
        SwipeRefreshLayout.OnRefreshListener,
        ClienteAdapter.customButtonListener,
        Response.ErrorListener {

    private AppBarConfiguration appBarConfiguration;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ClienteAdapter adapter;
    private ListView lvClientes;
    private List<ClienteEntity> listaDeClientes;
    private boolean isMobileConnected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback);
        } else {
            NetworkRequest request = new NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build();
            connectivityManager.registerNetworkCallback(request, networkCallback);
        };

        setContentView(R.layout.activity_clientes);
        lvClientes =  findViewById(R.id.lvClientes);
        listaDeClientes = new ArrayList<>();
        adapter = new ClienteAdapter(ClientesActivity.this,listaDeClientes);
        adapter.setCustomButtonListner(ClientesActivity.this);
        lvClientes.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ClientesActivity.this, CreateCliente.class);
                intent.putExtra("client",listaDeClientes.get(position));
                startActivity(intent);
            }
        });
        lvClientes.setAdapter(adapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent k = new Intent(ClientesActivity.this, CreateCliente.class);
                startActivity(k);
            }
        });

        getData();
    }

    @Override
    public void onRefresh() {
                getData();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getData();
    }

    private void getData(){
        if(listaDeClientes != null) {
            listaDeClientes.clear();
        }
            if(!NetworkManager.isNetworkConnected(getBaseContext())  || BatteryHelper.getBatteryPercentage(getBaseContext())<=20){
                getFromLocal();
                mSwipeRefreshLayout.setRefreshing(false);
                return;
            }
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

                try {
                    for(int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonobject = jsonArray.getJSONObject(i);
                        int id = jsonobject.getInt("id");
                        String name = jsonobject.getString("name");
                        String document = jsonobject.getString("document");
                        String phone = jsonobject.getString("phone");
                        String email = jsonobject.getString("email");
                        String responsiblePerson = jsonobject.getString("responsiblePerson");
                        UsuarioEntity internalResponsible = new UsuarioEntity();
                        if(jsonobject.has("internalResponsible") && jsonobject.getString("internalResponsible") != "null"){
                            JSONObject internalResponsibleObject = (JSONObject)  jsonobject.get("internalResponsible");
                            internalResponsible.setId(internalResponsibleObject.getInt("id"));
                            internalResponsible.setName(internalResponsibleObject.getString("name"));
                        };

                        ClienteEntity cliente = new ClienteEntity();
                        cliente.setEmail(email);
                        cliente.setPhone(phone);
                        cliente.setResponsiblePerson(responsiblePerson);
                        cliente.setInternalResponsible(internalResponsible);
                        cliente.setId(id);
                        cliente.setName(name);
                        cliente.setDocument(document);
                        listaDeClientes.add(cliente);
                    }
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                syncLocal(listaDeClientes);
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
        if(error.getClass()==(TimeoutError.class)){
            getFromLocal();
        return;
        }
        if(error.networkResponse.statusCode == 401){
            message = "Usuário não autenticado!";
        }else if(error.networkResponse.statusCode == 404) {
            listaDeClientes.clear();
            return;
        }else{

            try {
                 String responseBody = new String(error.networkResponse.data, "utf-8");
                JSONObject data = new JSONObject(responseBody);
                message = data.getString("message");
            } catch (JSONException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        Snackbar.make(findViewById(android.R.id.content).getRootView(), message, 3000).show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


    @Override
    public void onButtonClickListner(int position, ClienteEntity value) {
        if(!NetworkManager.isNetworkConnected(getBaseContext())){
            Snackbar.make(findViewById(android.R.id.content).getRootView(), "Você não está conectado à internet.", 3000).show();
        return;
        }
        RequestQueue queue = VolleySingleton.getInstance(ClientesActivity.this).getRequestQueue();
        String url = "http://192.168.2.100:3000/client/"+value.getId();

        StringRequest jsObjRequest = new StringRequest(
                Request.Method.DELETE,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String jsonObject) {
                        Snackbar.make(findViewById(android.R.id.content).getRootView(), "Cliente excluído com sucesso!", 3000).show();
                    getData();
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


    public void syncLocal(List<ClienteEntity> clientes){
        DBController crud = new DBController(getBaseContext());
        crud.deleteAll();
        for(int i = 0;i<clientes.size();i++){
            crud.insertUsuario(clientes.get(i).getInternalResponsible());
          crud.insertClient(clientes.get(i));

        }
    }

    public void getFromLocal(){
        listaDeClientes.clear();
        DBController crud = new DBController(getBaseContext());
        List<ClienteEntity> localList = crud.getClients();
        listaDeClientes.addAll(localList);
        adapter.notifyDataSetChanged();
    }

    ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {

        @Override
        public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
            super.onCapabilitiesChanged(network, networkCapabilities);
            if (  NetworkManager.isNetworkConnected(getBaseContext()) != isMobileConnected ){
                isMobileConnected = NetworkManager.isNetworkConnected(getBaseContext());
                if( isMobileConnected) {
                    getData();
                }
        }
            Log.i("Tag", "onCapChanged");
        }


    };


}

