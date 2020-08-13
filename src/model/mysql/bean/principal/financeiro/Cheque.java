/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.bean.principal.financeiro;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import model.mysql.bean.principal.documento.Parcela;
import model.mysql.bean.principal.documento.Venda;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 *
 * @author ivand
 */
@Entity
@Table(indexes = {
    @Index(columnList = "banco"),
    @Index(columnList = "agencia"),
    @Index(columnList = "conta"),
    @Index(columnList = "numero"),
    @Index(columnList = "correntista"),
    @Index(columnList = "cpfCnpj"),
    @Index(columnList = "valor"),
    @Index(columnList = "vencimento"),
    @Index(columnList = "observacao"),})
public class Cheque implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @CreationTimestamp
    private LocalDateTime criacao;
    @UpdateTimestamp
    private LocalDateTime atualizacao;
    private LocalDateTime exclusao;

    private String banco;
    private String agencia;
    private String conta;
    private String numero;
    private String correntista;
    private String cpfCnpj;
    private BigDecimal valor;
    private LocalDate vencimento;
    private String observacao;
    private LocalDateTime utilizado;

    @OneToMany(mappedBy = "cheque")
    private List<CaixaItem> caixaItens = new ArrayList<>();
    
    @OneToMany(mappedBy = "cheque")
    private List<Parcela> parcelas = new ArrayList<>();
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getCriacao() {
        return criacao;
    }

    public void setCriacao(LocalDateTime criacao) {
        this.criacao = criacao;
    }

    public LocalDateTime getAtualizacao() {
        return atualizacao;
    }

    public void setAtualizacao(LocalDateTime atualizacao) {
        this.atualizacao = atualizacao;
    }

    public LocalDateTime getExclusao() {
        return exclusao;
    }

    public void setExclusao(LocalDateTime exclusao) {
        this.exclusao = exclusao;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public String getConta() {
        return conta;
    }

    public void setConta(String conta) {
        this.conta = conta;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }


    public String getCorrentista() {
        return correntista;
    }

    public void setCorrentista(String correntista) {
        this.correntista = correntista;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDate getVencimento() {
        return vencimento;
    }

    public void setVencimento(LocalDate vencimento) {
        this.vencimento = vencimento;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public List<CaixaItem> getCaixaItens() {
        return caixaItens;
    }

    public void setCaixaItens(List<CaixaItem> caixaItens) {
        this.caixaItens = caixaItens;
    }

    public List<Parcela> getParcelas() {
        return parcelas;
    }

    public void setParcelas(List<Parcela> parcelas) {
        this.parcelas = parcelas;
    }

    public LocalDateTime getUtilizado() {
        return utilizado;
    }

    public void setUtilizado(LocalDateTime utilizado) {
        this.utilizado = utilizado;
    }

    //Bags----------------------------------------------------------------------
    
    public void addCaixaItem(CaixaItem caixaItem) {
        caixaItens.remove(caixaItem);
        caixaItens.add(caixaItem);
        caixaItem.setCheque(this);
    }
    
    public void removeCaixaItem(CaixaItem caixaItem) {
        caixaItem.setCheque(null);
        this.caixaItens.remove(caixaItem);
    }
    
    public void addParcela(Parcela parcela) {
        parcelas.remove(parcela);
        parcelas.add(parcela);
        parcela.setCheque(this);
    }
    
    public void removeParcela(Parcela parcela) {
        parcela.setCheque(null);
        this.parcelas.remove(parcela);
    }
    
    //Fim Bags------------------------------------------------------------------
    
    //Facilitadores-------------------------------------------------------------
    public boolean isUtilizado() {
        return getUtilizado() != null;
    }
    
    //Fim Facilitadores---------------------------------------------------------
    
}
