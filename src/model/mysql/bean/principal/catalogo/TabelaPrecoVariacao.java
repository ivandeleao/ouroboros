/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.bean.principal.catalogo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.swing.JOptionPane;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import static ouroboros.Ouroboros.MAIN_VIEW;
import util.Decimal;

/**
 *
 * @author ivand
 */
@Entity
public class TabelaPrecoVariacao implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @CreationTimestamp
    private LocalDateTime criacao;
    @UpdateTimestamp
    private LocalDateTime atualizacao;

    private LocalDateTime exclusao;
    
    private boolean comIntervalo;
    
    private BigDecimal valorInicial;
    private BigDecimal valorFinal;
    
    private BigDecimal acrescimoMonetario;
    private BigDecimal acrescimoPercentual;
    
    private BigDecimal descontoMonetario;
    private BigDecimal descontoPercentual;
    
    @ManyToOne
    @JoinColumn(name = "tabelaPrecoId")
    private TabelaPreco tabelaPreco;

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

    public boolean isComIntervalo() {
        return comIntervalo;
    }

    public void setComIntervalo(boolean comIntervalo) {
        this.comIntervalo = comIntervalo;
    }

    public BigDecimal getValorInicial() {
        return valorInicial;
    }

    public void setValorInicial(BigDecimal valorInicial) {
        this.valorInicial = valorInicial;
    }

    public BigDecimal getValorFinal() {
        return valorFinal;
    }

    public void setValorFinal(BigDecimal valorFinal) {
        this.valorFinal = valorFinal;
    }

    public BigDecimal getAcrescimoMonetario() {
        return acrescimoMonetario != null ? acrescimoMonetario : BigDecimal.ZERO;
    }

    public void setAcrescimoMonetario(BigDecimal acrescimoMonetario) {
        this.acrescimoMonetario = acrescimoMonetario;
    }

    public BigDecimal getAcrescimoPercentual() {
        return acrescimoPercentual != null ? acrescimoPercentual : BigDecimal.ZERO;
    }

    public void setAcrescimoPercentual(BigDecimal acrescimoPercentual) {
        this.acrescimoPercentual = acrescimoPercentual;
    }

    public BigDecimal getDescontoMonetario() {
        return descontoMonetario != null ? descontoMonetario : BigDecimal.ZERO;
    }

    public void setDescontoMonetario(BigDecimal descontoMonetario) {
        this.descontoMonetario = descontoMonetario;
    }

    public BigDecimal getDescontoPercentual() {
        return descontoPercentual != null ? descontoPercentual : BigDecimal.ZERO;
    }

    public void setDescontoPercentual(BigDecimal descontoPercentual) {
        this.descontoPercentual = descontoPercentual;
    }

    public TabelaPreco getTabelaPreco() {
        return tabelaPreco;
    }

    public void setTabelaPreco(TabelaPreco tabelaPreco) {
        this.tabelaPreco = tabelaPreco;
    }

    //--------------------------------------------------------------------------
    
    public BigDecimal getValorVariacao(BigDecimal valor) {
        BigDecimal valorVariacao;
                
        if(getAcrescimoSemTipo().compareTo(getDescontoSemTipo()) > 0) {
            if(getAcrescimoMonetario().compareTo(getAcrescimoPercentual()) > 0) {
                valorVariacao = getAcrescimoMonetario();

            } else {
                valorVariacao = getAcrescimoPercentual().multiply(valor).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);

            }
            return valor.add(valorVariacao).setScale(2, RoundingMode.HALF_UP);

        } else {
            if(getDescontoMonetario().compareTo(getDescontoPercentual()) > 0) {
                valorVariacao = getDescontoMonetario();

            } else {
                valorVariacao = getDescontoPercentual().multiply(valor).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);

            }
            return valor.subtract(valorVariacao).setScale(2, RoundingMode.HALF_UP);

        }
        
    }
    
    public String getAcrescimoOuDescontoFormatado() {
        if(getAcrescimoSemTipo().compareTo(getDescontoSemTipo()) > 0) {
            return "+ " + getAcrescimoFormatado();
        } else {
            return "- " + getDescontoFormatado();
        }
    }
    
    public BigDecimal getAcrescimoSemTipo() {
        if(getAcrescimoPercentual().compareTo(BigDecimal.ZERO) > 0) {
            return getAcrescimoPercentual();
        } else {
            return getAcrescimoMonetario();
        }
    }
    
    public BigDecimal getDescontoSemTipo() {
        if(getDescontoPercentual().compareTo(BigDecimal.ZERO) > 0) {
            return getDescontoPercentual();
        } else {
            return getDescontoMonetario();
        }
    }
    
    public String getAcrescimoFormatado() {
        if(getAcrescimoPercentual().compareTo(BigDecimal.ZERO) > 0) {
            return Decimal.toString(getAcrescimoPercentual()) + "%";
        } else {
            return Decimal.toString(getAcrescimoMonetario());
        }
    }
    
    public String getDescontoFormatado() {
        if(getDescontoPercentual().compareTo(BigDecimal.ZERO) > 0) {
            return Decimal.toString(getDescontoPercentual()) + "%";
        } else {
            return Decimal.toString(getDescontoMonetario());
        }
    }
    
    public String getAcrescimoTipo() {
        return getAcrescimoMonetario().compareTo(BigDecimal.ZERO) > 0 ? "$" : "%";
    }
    
    public String getDescontoTipo() {
        return getDescontoMonetario().compareTo(BigDecimal.ZERO) > 0 ? "$" : "%";
    }
    
    
    //--------------------------------------------------------------------------
    @Override
    public boolean equals(Object obj) {
        if(obj == null){
            return false;
        }
        return Objects.equals(this.getId(), ((TabelaPrecoVariacao) obj).getId());
    }
}
