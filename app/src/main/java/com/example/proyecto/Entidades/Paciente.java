package com.example.proyecto.Entidades;

public class Paciente {
    public String cedula;
    public String nombre;
    public String regimen;
    public String clave;
    public String email;
    public String direccion;
    public String telefono;
    public String edad;

    public Paciente() {
    }

    public Paciente(String cedula, String nombre, String regimen, String clave, String email, String direccion, String telefono, String edad) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.regimen = regimen;
        this.clave = clave;
        this.email = email;
        this.direccion = direccion;
        this.telefono = telefono;
        this.edad = edad;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRegimen() {
        return regimen;
    }

    public void setRegimen(String regimen) {
        this.regimen = regimen;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEdad() {
        return edad;
    }

    public void setEdad(String edad) {
        this.edad = edad;
    }
}
