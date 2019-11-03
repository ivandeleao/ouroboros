/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.bean.principal;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import model.mysql.bean.principal.documento.OSTransporte;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;


@Entity
@Table(indexes = {
    @Index(columnList = "criacao"),
    @Index(columnList = "atualizacao")
    
})
public class OSTransporteItem implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @CreationTimestamp
    private Timestamp criacao;
    @UpdateTimestamp
    private Timestamp atualizacao;
    
    @ManyToOne
    @JoinColumn(name = "osTransporteId", nullable = true)
    private OSTransporte osTransporte;

    private String descricao;

    private BigDecimal valor;
    
    private BigDecimal pedagio;
    
    private BigDecimal adicional;
    
    public OSTransporteItem() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Timestamp getCriacao() {
        return criacao;
    }

    public void setCriacao(Timestamp criacao) {
        this.criacao = criacao;
    }

    public Timestamp getAtualizacao() {
        return atualizacao;
    }

    public void setAtualizacao(Timestamp atualizacao) {
        this.atualizacao = atualizacao;
    }

    public OSTransporte getOsTransporte() {
        return osTransporte;
    }

    public void setOsTransporte(OSTransporte osTransporte) {
        this.osTransporte = osTransporte;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValor() {
        return valor != null ? valor : BigDecimal.ZERO;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getPedagio() {
        return pedagio != null ? pedagio : BigDecimal.ZERO;
    }

    public void setPedagio(BigDecimal pedagio) {
        this.pedagio = pedagio;
    }

    public BigDecimal getAdicional() {
        return adicional != null ? adicional : BigDecimal.ZERO;
    }

    public void setAdicional(BigDecimal adicional) {
        this.adicional = adicional;
    }


}
