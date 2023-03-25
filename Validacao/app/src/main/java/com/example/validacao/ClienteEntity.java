package com.example.validacao;

public class ClienteEntity {
    private int id;
    private String name;
    private String document;
    private String email;
    private String phone;
    private String responsiblePerson;
    private UsuarioEntity internalResponsible;

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

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getResponsiblePerson() {
        return responsiblePerson;
    }

    public void setResponsiblePerson(String responsiblePerson) {
        this.responsiblePerson = responsiblePerson;
    }

    public UsuarioEntity getInternalResponsible() {
        return internalResponsible;
    }

    public void setInternalResponsible(UsuarioEntity internalResponsible) {
        this.internalResponsible = internalResponsible;
    }
}
