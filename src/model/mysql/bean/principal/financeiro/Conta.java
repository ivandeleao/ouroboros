/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.bean.principal.financeiro;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Column;
import model.mysql.dao.principal.financeiro.CaixaDAO;
import model.mysql.dao.principal.financeiro.CaixaItemDAO;
import model.nosql.ContaTipoEnum;

/**
 *
 * @author ivand
 */
@Entity
public class Conta implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @CreationTimestamp
    private LocalDateTime criacao;
    @UpdateTimestamp
    private LocalDateTime atualizacao;

    private LocalDateTime exclusao;

    private String nome;
    
    private ContaTipoEnum contaTipo;
    
    private LocalDate data; //data base da contaCorrente
    
    @Column(columnDefinition = "decimal(20,2) default 0")//, nullable = false)
    private BigDecimal saldo;
    
    @OneToMany(mappedBy = "conta")
    private List<Caixa> caixas = new ArrayList<>();
    
    @OneToMany(mappedBy = "conta")
    private List<CaixaItem> caixaItens = new ArrayList<>();
    
    //Boleto
    private boolean boleto;
    //private  .. banco
    private String agencia;
    private String agenciaDv;
    private String posto;
    private String contaCorrente;
    private String contaCorrenteDv;
    private String cedente;
    private Integer boletoByte;
    private Integer boletoSequencial; //número global do boleto
    private Integer boletoSequencialArquivo; //NSA (Número Sequencial do Arquivo)
    
    
    
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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public ContaTipoEnum getContaTipo() {
        return contaTipo;
    }

    public void setContaTipo(ContaTipoEnum contaTipo) {
        this.contaTipo = contaTipo;
    }

    public boolean isBoleto() {
        return boleto;
    }

    public List<CaixaItem> getCaixaItens() {
        return caixaItens;
    }

    public void setCaixaItens(List<CaixaItem> caixaItens) {
        this.caixaItens = caixaItens;
    }

    public void setBoleto(boolean boleto) {
        this.boleto = boleto;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public String getAgenciaDv() {
        return agenciaDv;
    }

    public void setAgenciaDv(String agenciaDv) {
        this.agenciaDv = agenciaDv;
    }

    public String getPosto() {
        return posto;
    }

    public void setPosto(String posto) {
        this.posto = posto;
    }

    public String getContaCorrente() {
        return contaCorrente;
    }

    public void setContaCorrente(String contaCorrente) {
        this.contaCorrente = contaCorrente;
    }

    public String getContaCorrenteDv() {
        return contaCorrenteDv;
    }

    public void setContaCorrenteDv(String contaCorrenteDv) {
        this.contaCorrenteDv = contaCorrenteDv;
    }

    public String getCedente() {
        return cedente;
    }

    public void setCedente(String cedente) {
        this.cedente = cedente;
    }

    public Integer getBoletoByte() {
        return boletoByte;
    }

    public void setBoletoByte(Integer boletoByte) {
        this.boletoByte = boletoByte;
    }

    public Integer getBoletoSequencial() {
        return boletoSequencial;
    }

    public void setBoletoSequencial(Integer boletoSequencial) {
        this.boletoSequencial = boletoSequencial;
    }

    public Integer getBoletoSequencialArquivo() {
        return boletoSequencialArquivo;
    }

    public void setBoletoSequencialArquivo(Integer boletoSequencialArquivo) {
        this.boletoSequencialArquivo = boletoSequencialArquivo;
    }
    
    

    //Bags----------------------------------------------------------------------
    public void addCaixa(Caixa caixa) {
        caixas.remove(caixa);
        caixas.add(caixa);
        caixa.setConta(this);
    }

    public void removeCaixa(Caixa caixa) {
        caixa.setConta(null);
        caixas.remove(caixa);
    }
    
    public void addCaixaItem(CaixaItem caixaItem) {
        caixaItens.remove(caixaItem);
        caixaItens.add(caixaItem);
        caixaItem.setConta(this);
    }

    public void removeCaixaItem(CaixaItem caixaItem) {
        caixaItem.setConta(null);
        caixaItens.remove(caixaItem);
    }

    //Fim Bags------------------------------------------------------------------
    
    //Facilitadores-------------------------------------------------------------
    public boolean hasTurnoAberto() {
        return getLastCaixa() != null && getLastCaixa().getEncerramento() != null;
    }
    
    public Caixa getLastCaixa() {
        return new CaixaDAO().getLastCaixa(this);
    }
    
    public LocalDate getUltimaData() {
        CaixaItem caixaItem = new CaixaItemDAO().getUltimaData(this);
        if(caixaItem != null) {
            return caixaItem.getDataHora().toLocalDate();
        }
        return getData();
    }
    
    
    public BigDecimal getSaldo() {
        /*BigDecimal saldo = BigDecimal.ZERO;
        
        if (getContaTipo().equals(ContaTipoEnum.CAIXA)) {
            Caixa lastCaixa = new CaixaDAO().getLastCaixa(this);
            if (lastCaixa != null) {
                caixaItens = lastCaixa.getCaixaItens();
            }
        }
        
        if (!caixaItens.isEmpty()) {
            saldo = caixaItens.stream().map(CaixaItem::getSaldoLinear).reduce(BigDecimal::add).get();
            
            //saldo = caixaItens.get(caixaItens.size() - 1).getSaldoAcumulado(); 2020-04-24 esboço ainda não usado
        }*/
        
        return saldo != null ? saldo : BigDecimal.ZERO;
    }
    
    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }
    
    public void setSaldo() {
        if (getContaTipo().equals(ContaTipoEnum.CAIXA)) {
            Caixa lastCaixa = new CaixaDAO().getLastCaixa(this);
            if (lastCaixa != null) {
                caixaItens = lastCaixa.getCaixaItens();
            }
        }
        if (!caixaItens.isEmpty()) {
            saldo = caixaItens.get(caixaItens.size() - 1).getSaldoAcumulado();
        }
    }
    
    //Fim Facilitadores---------------------------------------------------------
    
    
    @Override
    public String toString() {
        return getNome();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        return Objects.equals(this.getId(), ((Conta) obj).getId());
    }

}
