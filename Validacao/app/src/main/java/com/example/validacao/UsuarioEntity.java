package com.example.validacao;

import java.io.Serializable;

public class UsuarioEntity implements Serializable {
    private int id;
    private String name;
    private String email;
    private String password;
    private String type;
    private ClienteEntity[] clientes;

    public UsuarioEntity() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ClienteEntity[] getClientes() {
        return clientes;
    }

    public void setClientes(ClienteEntity[] clientes) {
        this.clientes = clientes;
    }


    @Override
    public String toString() {
        return this.name; // What to display in the Spinner list.
    }
}