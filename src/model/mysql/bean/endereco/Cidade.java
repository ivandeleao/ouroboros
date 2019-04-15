/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.bean.endereco;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * dados provenientes do sqLite
 * @author ivand
 */
@Entity
@Table(name = "tblendereco_cidade")
public class Cidade implements Serializable {
    @Id
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "estado_id")
    private Estado estado;
    
    @Column(name = "cidade")
    private String nome;
    
    @Column(name = "codigo_ibge")
    private Integer codigoIbge;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getCodigoIbge() {
        return codigoIbge;
    }

    public void setCodigoIbge(Integer codigoIbge) {
        this.codigoIbge = codigoIbge;
    }
    
    public String getCodigoIbgeCompleto() {
        return getEstado().getCodigoIbge() + String.format("%05d", getCodigoIbge());
    }
    
}
