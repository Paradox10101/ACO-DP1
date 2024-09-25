
package com.example.backend.models;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "Ubicacion")
public class Ubicacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ubicacion")
    private Long id_ubicacion;

    @Column(name = "ubigeo")
    private String ubigeo;

    @ManyToOne
    @JoinColumn(name = "id_region")
    private Long fid_region;

    @Column(name = "latitud")
    private float latitud;

    @Column(name = "longitud")
    private float longitud;

    @Column(name = "departamento")
    private String departamento;

    @Column(name = "provincia")
    private String provincia;

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public Ubicacion(){
    }

    public Ubicacion(Long id_ubicacion, String ubigeo, String ciudad, Long fid_region){
        this.id_ubicacion = id_ubicacion;
        this.ubigeo = ubigeo;
        this.fid_region = fid_region;
    }


    public Long getFid_region() {
        return fid_region;
    }

    public void setFid_region(Long fid_region) {
        this.fid_region = fid_region;
    }

    public Long getId_ubicacion() {
        return id_ubicacion;
    }

    public void setId_ubicacion(Long id_ubicacion) {
        this.id_ubicacion = id_ubicacion;
    }

    public float getLatitud() {
        return latitud;
    }

    public void setLatitud(float latitud) {
        this.latitud = latitud;
    }

    public float getLongitud() {
        return longitud;
    }

    public void setLongitud(float longitud) {
        this.longitud = longitud;
    }

    public Long getIdUbicacion(){
        return id_ubicacion;
    }

    public void setIdUbicacion(Long id_ubicacion){
        this.id_ubicacion = id_ubicacion;
    }

    public String getUbigeo(){
        return ubigeo;
    }

    public void setUbigeo(String ubigeo){
        this.ubigeo = ubigeo;
    }

    public Long getFid_Region(){
        return fid_region;
    }
    
    public void setFid_Region(Long fid_region){
        this.fid_region = fid_region;
    }
    
    

}
