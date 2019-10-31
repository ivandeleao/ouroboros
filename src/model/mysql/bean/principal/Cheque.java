/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.bean.principal;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 *
 * @author ivand
 */
@Entity
@Table(indexes = {
    @Index(columnList = "placa"),
    @Index(columnList = "modelo")})
public class Cheque implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @CreationTimestamp
    private Timestamp criacao;
    @UpdateTimestamp
    private Timestamp atualizacao;

    private LocalDateTime exclusao;

    private String placa;
    private String modelo;

    private Short anoFabricacao;
    private Short anoModelo;

    private String cor;
    private String motor;
    private String chassi;
    private String renavam;

    private String observacao;

    /*
    @OneToMany(mappedBy = "veiculo")
    @OrderBy
    private List<Venda> documentos = new ArrayList<>();
     */
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

    public LocalDateTime getExclusao() {
        return exclusao;
    }

    public void setExclusao(LocalDateTime exclusao) {
        this.exclusao = exclusao;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public Short getAnoFabricacao() {
        return anoFabricacao;
    }

    public void setAnoFabricacao(Short anoFabricacao) {
        this.anoFabricacao = anoFabricacao;
    }

    public Short getAnoModelo() {
        return anoModelo;
    }

    public void setAnoModelo(Short anoModelo) {
        this.anoModelo = anoModelo;
    }

    public String getObservacao() {
        return observacao != null ? observacao : "";
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getCor() {
        return cor != null ? cor : "";
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public String getMotor() {
        return motor != null ? motor : "";
    }

    public void setMotor(String motor) {
        this.motor = motor;
    }

    public String getChassi() {
        return chassi != null ? chassi : "";
    }

    public void setChassi(String chassi) {
        this.chassi = chassi;
    }

    public String getRenavam() {
        return renavam != null ? renavam : "";
    }

    public void setRenavam(String renavam) {
        this.renavam = renavam;
    }

}
