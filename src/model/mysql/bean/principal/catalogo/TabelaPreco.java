/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.bean.principal.catalogo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import util.Decimal;

/**
 *
 * @author ivand
 */
@Entity
public class TabelaPreco implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @CreationTimestamp
    private LocalDateTime criacao;
    @UpdateTimestamp
    private LocalDateTime atualizacao;

    private LocalDateTime exclusao;
    
    private String nome;
    
    @OneToMany(mappedBy = "tabelaPreco")
    private List<TabelaPrecoVariacao> tabelaPrecoVariacoes = new ArrayList<>();

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

    public List<TabelaPrecoVariacao> getTabelaPrecoVariacoes() {
        tabelaPrecoVariacoes.sort(Comparator.comparing(TabelaPrecoVariacao::getValorInicial));
        return tabelaPrecoVariacoes;
    }

    public void setTabelaPrecoVariacoes(List<TabelaPrecoVariacao> tabelaPrecoVariacoes) {
        this.tabelaPrecoVariacoes = tabelaPrecoVariacoes;
    }
    
    //Bags----------------------------------------------------------------------
    public void addTabelaPrecoVariacao(TabelaPrecoVariacao tabelaPrecoVariacao) {
        tabelaPrecoVariacoes.remove(tabelaPrecoVariacao);
        tabelaPrecoVariacoes.add(tabelaPrecoVariacao);
        tabelaPrecoVariacao.setTabelaPreco(this);
    }
    
    public void removeTabelaPrecoVariacao(TabelaPrecoVariacao tabelaPrecoVariacao) {
        tabelaPrecoVariacao.setTabelaPreco(null);
        tabelaPrecoVariacoes.remove(tabelaPrecoVariacao);
    }
    //Fim Bags------------------------------------------------------------------
    
    public String getTabelaPrecoVariacoesFormatada() {
        if(getTabelaPrecoVariacoes().isEmpty()) {
            return "--";
            
        } else {
            List<String> valores = new ArrayList<>();
            for(TabelaPrecoVariacao v :getTabelaPrecoVariacoes()) {
                String variacao = "";
                
                variacao += v.getAcrescimoOuDescontoFormatado();
                
                if(v.isComIntervalo()) {
                    variacao += " (" +Decimal.toString(v.getValorInicial()) + " - " + Decimal.toString(v.getValorFinal()) + ")";
                }
                
                valores.add(variacao);
            }
            return String.join("\r\n", valores);
        }
    }
    
    public boolean validarNovoIntervalo(TabelaPrecoVariacao tabelaPrecoVariacao, BigDecimal valorInicial, BigDecimal valorFinal) {
        if(getId() == null) {
            return true;
        }
        
        if(getTabelaPrecoVariacoes().isEmpty()) {
            return true;
        }
        
        for(TabelaPrecoVariacao v : getTabelaPrecoVariacoes()) {
            //ignorar a variação atual e validar intervalo das outras variações
            if(!v.equals(tabelaPrecoVariacao) && !(valorFinal.compareTo(v.getValorInicial()) < 0) && !(valorInicial.compareTo(v.getValorFinal()) > 0)) {
                return false;
            }
        }
        
        return true;
        
    }
    
    
    
    
    
    
    //--------------------------------------------------------------------------
    @Override
    public String toString(){
        return nome;
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj == null){
            return false;
        }
        return Objects.equals(this.getId(), ((TabelaPreco) obj).getId());
    }
}
