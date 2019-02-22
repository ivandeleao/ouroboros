/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.bean.endereco;

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
@Table(name = "tblendereco")
public class Endereco implements Serializable{
    @Id
    private String cep;
    
    @ManyToOne
    @JoinColumn(name = "cidade_id")
    private Cidade cidade;
    
    @ManyToOne
    @JoinColumn(name = "bairro_id")
    private Bairro bairro;
    
    @Column(length = 300)
    private String logradouro;
    @Column(length = 300)
    private String endereco;
    @Column(name = "endereco_completo", length = 300)
    private String enderecoCompleto;

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public Cidade getCidade() {
        return cidade;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
    }

    public Bairro getBairro() {
        return bairro;
    }

    public void setBairro(Bairro bairro) {
        this.bairro = bairro;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getEndereco() {
        return endereco.trim();
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getEnderecoCompleto() {
        return enderecoCompleto.trim();
    }

    public void setEnderecoCompleto(String enderecoCompleto) {
        this.enderecoCompleto = enderecoCompleto;
    }
    
}
