package model;

import java.io.Serializable;
import java.sql.Blob;
import java.util.Date;

/**
 * Created by ivan on 8/30/15.
 */
public class Reclamo implements Serializable {
    private int id;
    private String imei;
    private String lat;
    private String lng;
    private String categoria;
    private String subcategoria;
    private Date fecha;
    private byte[] foto;

    public Reclamo(){}
    public Reclamo(int id, String imei, String lat, String lng, String categoria, String subcategoria, byte[] foto, Date fecha) {
        this.id = id;
        this.imei = imei;
        this.lat = lat;
        this.lng = lng;
        this.categoria = categoria;
        this.subcategoria = subcategoria;
        this.foto = foto;
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public byte[] getFoto() {
        return foto;
    }

    public void setFoto(byte[] foto) {
        this.foto = foto;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getSubcategoria() {
        return subcategoria;
    }

    public void setSubcategoria(String subcategoria) {
        this.subcategoria = subcategoria;
    }

    @Override
    public String toString() {
        return "Reclamo{" +
                "id=" + id +
                ", imei='" + imei + '\'' +
                ", lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                ", categoria='" + categoria + '\'' +
                ", subcategoria='" + subcategoria + '\'' +
                ", fecha=" + fecha +
                ", foto=" + foto +
                '}';
    }
}
