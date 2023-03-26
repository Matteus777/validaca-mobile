package com.example.validacao;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class CreateCliente extends AppCompatActivity {
ArrayAdapter<UsuarioEntity> adp1;
ClienteEntity objectEdit = new ClienteEntity();
List<UsuarioEntity> list = new ArrayList<UsuarioEntity>();
boolean isEdit = false;
Spinner spUsers;

    @Override
    protected void onStart() {
        super.onStart();
        Intent i = getIntent();
        ClienteEntity edit = (ClienteEntity)i.getSerializableExtra("client");
        if(edit != null){
            isEdit= true;
            objectEdit = edit;
            fillData();
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_cliente);
        spUsers = findViewById(R.id.spUsers);
        adp1 = new ArrayAdapter<UsuarioEntity>(this,
                android.R.layout.simple_list_item_1, list);

        adp1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spUsers.setAdapter(adp1);
        EditText etDocument =  (EditText)findViewById(R.id.etDocument);
etDocument.addTextChangedListener( MaskWatcher.buildDocument());
        EditText etPhone =  (EditText)findViewById(R.id.etPhone);
    etPhone.addTextChangedListener(new MaskWatcher("(##) #####-####"));
        spUsers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {
               objectEdit.setInternalResponsible(list.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
            }
        });

        Button btnSave = findViewById(R.id.btnSave);
         btnSave.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

                 EditText etName =  (EditText)findViewById(R.id.etName);
                 EditText etDocument =  (EditText)findViewById(R.id.etDocument);
                 EditText etEmail =  (EditText)findViewById(R.id.etEmail);
                 EditText etPhone =  (EditText)findViewById(R.id.etPhone);
                 EditText etResponsible =  (EditText)findViewById(R.id.etResponsiblePerson);

                 String name = etName.getText().toString();
                 String document = etDocument.getText().toString().replaceAll("[^\\d]", "");;
                 String email = etEmail.getText().toString();
                 String phone = etPhone.getText().toString().replaceAll("[^\\d]", "");;
                 String responsible = etResponsible.getText().toString();


                 objectEdit.setName(name);
                 objectEdit.setDocument(document);
                 objectEdit.setEmail(email);
                 objectEdit.setPhone(phone);
                 objectEdit.setResponsiblePerson(responsible);
                 if(!validateFields()){
                     Snackbar.make(findViewById(android.R.id.content).getRootView(), "Preencha os campos corretamente!", 3000)
                             .show();
                     return;
                 }
                 try {
                     sendData();
                 } catch (JSONException e) {
                     e.printStackTrace();
                 }
             }
         });

        getUsers();
    }

    private boolean validateFields() {
        if(objectEdit.getDocument().length() < 11 || objectEdit.getDocument().length() > 14){
            return false;
        }
        if(!objectEdit.getEmail().contains("@")){
            return false;
        }
        String[] nameParts = objectEdit.getName().split(" ");
        if(nameParts[0].length() <=2){
            return false;
        }
        if(nameParts[nameParts.length-1].length() <= 3){
            return false;
        }

        if(!Pattern.matches("[a-zA-Z\\s]+",objectEdit.getName())){
                return false;
        }
        return true;
    }

    private void fillData() {

        EditText etName =  (EditText)findViewById(R.id.etName);
        EditText etDocument =  (EditText)findViewById(R.id.etDocument);
        EditText etEmail =  (EditText)findViewById(R.id.etEmail);
        EditText etPhone =  (EditText)findViewById(R.id.etPhone);
        EditText etResponsible =  (EditText)findViewById(R.id.etResponsiblePerson);

        etName.setText(objectEdit.getName());
       etDocument.setText(getMaskedText(objectEdit.getDocument(),"XXX.XXX.XXX-XX",'X'));
        etEmail.setText(objectEdit.getEmail());
        etPhone.setText(getMaskedText(objectEdit.getPhone(),"(XX) XXXXX-XXXX",'X'));
      etResponsible.setText(objectEdit.getResponsiblePerson());




    }

    private void fillResponsible(){
        int idx = list.indexOf(objectEdit.getInternalResponsible());
        spUsers.setSelection(idx);
    }
    private void getUsers(){

        RequestQueue queue = VolleySingleton.getInstance(CreateCliente.this).getRequestQueue();
        String url = "http://192.168.2.100:3000/usuarios";
        if(!NetworkManager.isNetworkConnected(getBaseContext()) || BatteryHelper.getBatteryPercentage(getBaseContext())<=20){
            getFromLocal();
            return;
        }
        JsonArrayRequest jsObjRequest =
                null;

        jsObjRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        list.clear();
                        try {
                            for(int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonobject = jsonArray.getJSONObject(i);
                                int id = jsonobject.getInt("id");
                                String name = jsonobject.getString("name");
                                UsuarioEntity usuario = new UsuarioEntity();
                                usuario.setId(id);
                                usuario.setName(name);
                                list.add(usuario);
                            }
                            adp1.notifyDataSetChanged();
                            syncLocal(list);
                            if (isEdit) {
                                fillResponsible();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },new Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String message = "";
                        if(error.getClass()==(TimeoutError.class)) {
                            getFromLocal();
                            return;
                        }
                            if(error.networkResponse.statusCode == 401){
                            message = "Usuário não autenticado!";
                        }else{
                            message ="Erro no processo de login.";
                        }
                        Snackbar.make(findViewById(android.R.id.content).getRootView(), message, 3000).show();
                    }
        }){
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

    private void sendData() throws JSONException {
        if(!NetworkManager.isNetworkConnected(getBaseContext())){
            Snackbar.make(findViewById(android.R.id.content).getRootView(),"Você nãoe stá conectado à internet",2000).show();
        return;
        }
        RequestQueue queue = VolleySingleton.getInstance(CreateCliente.this).getRequestQueue();
        String url = "http://192.168.2.100:3000/client";

        JsonObjectRequest jsObjRequest =
                null;

    JSONObject body = new JSONObject();
    body.put("name",objectEdit.getName());
    body.put("document",objectEdit.getDocument());
    body.put("email",objectEdit.getEmail());
    body.put("phone",objectEdit.getPhone());
    body.put("responsiblePerson",objectEdit.getResponsiblePerson());

    JSONObject internalResponsibleJson = new JSONObject();
    if(objectEdit.getInternalResponsible() != null){
        internalResponsibleJson.put("id",objectEdit.getInternalResponsible().getId());
    }
        body.put("internalResponsible",internalResponsibleJson);
    int method =  Request.Method.POST;
    if(isEdit){
        url =  "http://192.168.2.100:3000/client/"+objectEdit.getId();
        method = Request.Method.PATCH;
    }

        jsObjRequest = new JsonObjectRequest(
                method,
                url,
                body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Snackbar.make(findViewById(android.R.id.content).getRootView(), "Cliente cadastrado com sucesso!", 3000)
                                .addCallback(new Snackbar.Callback(){
                                    @Override
                                    public void onDismissed(Snackbar snackbar, int event) {
                                         onBackPressed();
                                    }
                                }).show();
                    }
                },new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
               String responseBody = null;
                try {
                    responseBody = new String(error.networkResponse.data, "utf-8");
                    JSONObject data = new JSONObject(responseBody);
                    String message = data.getString("message");
                    Snackbar.make(findViewById(android.R.id.content).getRootView(), message, 3000).show();

                } catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                }

            }
        }){
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

    public String getMaskedText(String rawText, String mask, char variableChar) {
        StringBuilder out = new StringBuilder();

        for (int i = 0, j = 0; i < mask.length() && j < rawText.length(); i++) {
            if (mask.charAt(i) == variableChar) {
                out.append(rawText.charAt(j));
                j++;
            } else {
                out.append(mask.charAt(i));
            }
        }

        return out.toString();
    }

    public void syncLocal(List<UsuarioEntity> usuarios){
        DBController crud = new DBController(getBaseContext());
        crud.deleteAll();
        for(int i = 0;i<usuarios.size();i++){
            crud.insertUsuario(usuarios.get(i));
        }
    }


    public void getFromLocal(){
        list.clear();
        DBController crud = new DBController(getBaseContext());
        List<UsuarioEntity> localList = crud.getUsuarios();
        list.addAll(localList);
        adp1.notifyDataSetChanged();
    }
    }


