
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

    public Ubicacion(Long id_ubicacion, String ubigeo, String ciudad, Long fid_region){
        this.id_ubicacion = id_ubicacion;
        this.ubigeo = ubigeo;
        this.fid_region = fid_region;
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
