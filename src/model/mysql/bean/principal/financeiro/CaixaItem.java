/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.bean.principal.financeiro;

import model.mysql.bean.principal.documento.Parcela;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import model.mysql.bean.fiscal.MeioDePagamento;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 *
 * @author ivand
 */
@Entity
public class CaixaItem implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @CreationTimestamp
    private LocalDateTime criacao;
    @UpdateTimestamp
    private LocalDateTime atualizacao;
    
    @ManyToOne
    @JoinColumn(name = "caixaId")
    private Caixa caixa;
    
    @ManyToOne
    @JoinColumn(name = "parcelaId", nullable = true)
    private Parcela parcela;
    
    @OneToOne
    //@MapsId
    @JoinColumn(name = "contaProgramadaBaixaId", nullable = true)
    private ContaProgramadaBaixa contaProgramadaBaixa;
    
    @ManyToOne
    @JoinColumn(name = "caixaItemTipoId")
    private CaixaItemTipo caixaItemTipo;
    
    @ManyToOne
    @JoinColumn(name = "meioDePagamentoId")
    private MeioDePagamento meioDePagamento;
    
    private String observacao;
    
    @Column(columnDefinition = "decimal(19,2) default 0", nullable = false)
    private BigDecimal credito;
    
    @Column(columnDefinition = "decimal(19,2) default 0", nullable = false)
    private BigDecimal debito;
    
    @Column(columnDefinition = "decimal(19,2) default 0", nullable = false)
    private BigDecimal saldoAcumulado;
    
    //------------------- relacionamento circular
    @OneToOne(mappedBy = "estornoOrigem")
    private CaixaItem estorno;
    
    @OneToOne
    @JoinColumn(name = "estornoOrigemId")
    private CaixaItem estornoOrigem;
    //-------------------
    
    
    public CaixaItem(){}
    
    public CaixaItem(Caixa caixa, CaixaItemTipo caixaItemTipo, MeioDePagamento meioDePagamento, String observacao, BigDecimal credito, BigDecimal debito){
        this.caixa = caixa;
        this.caixaItemTipo = caixaItemTipo;
        this.meioDePagamento = meioDePagamento;
        this.observacao = observacao;
        this.credito = credito;
        this.debito = debito;
        this.saldoAcumulado = BigDecimal.ZERO;
    }
    
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

    public Caixa getCaixa() {
        return caixa;
    }

    public void setCaixa(Caixa caixa) {
        this.caixa = caixa;
    }

    public Parcela getParcela() {
        return parcela;
    }

    public void setParcela(Parcela parcela) {
        this.parcela = parcela;
    }

    public ContaProgramadaBaixa getContaProgramadaBaixa() {
        return contaProgramadaBaixa;
    }

    public void setContaProgramadaBaixa(ContaProgramadaBaixa contaProgramadaBaixa) {
        this.contaProgramadaBaixa = contaProgramadaBaixa;
    }
    
    public CaixaItemTipo getCaixaItemTipo() {
        return caixaItemTipo;
    }

    public void setCaixaItemTipo(CaixaItemTipo caixaItemTipo) {
        this.caixaItemTipo = caixaItemTipo;
    }

    public MeioDePagamento getMeioDePagamento() {
        return meioDePagamento;
    }

    public void setMeioDePagamento(MeioDePagamento meioDePagamento) {
        this.meioDePagamento = meioDePagamento;
    }

    public String getObservacao() {
        return observacao != null ? observacao : "";
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }
    
    public BigDecimal getCredito() {
        return credito != null ? credito : BigDecimal.ZERO;
    }

    public void setCredito(BigDecimal credito) {
        this.credito = credito != null ? credito : BigDecimal.ZERO;
    }

    public BigDecimal getDebito() {
        return debito != null ? debito : BigDecimal.ZERO;
    }

    public void setDebito(BigDecimal debito) {
        this.debito = debito != null ? debito : BigDecimal.ZERO;
    }

    public BigDecimal getSaldoAcumulado() {
        return saldoAcumulado != null ? saldoAcumulado : BigDecimal.ZERO;
    }

    public void setSaldoAcumulado(BigDecimal saldoAcumulado) {
        this.saldoAcumulado = saldoAcumulado;
    }
/*
    public Integer getEstornoId() {
        return estornoId;
    }

    public void setEstornoId(Integer estornoId) {
        this.estornoId = estornoId;
    }
*/

    public CaixaItem getEstorno() {
        return estorno;
    }

    public void setEstorno(CaixaItem estorno) {
        this.estorno = estorno;
    }
    
    public CaixaItem getEstornoOrigem() {
        return estornoOrigem;
    }

    public void setEstornoOrigem(CaixaItem estornoOrigem) {
        this.estornoOrigem = estornoOrigem;
    }

    
    //----------------------
    
    
    public BigDecimal getSaldoLinear(){
        return getCredito().subtract(getDebito());
    }
    
    /**
     * Método conveniente para trazer mais detalhes
     * @return tipo, id da venda, etc...
     */
    public String getDescricao() {
        String descricao = "";
        
        if(!getCaixaItemTipo().equals(CaixaItemTipo.DOCUMENTO)) {
            descricao = getCaixaItemTipo().getNome() + " ";
        }
        
        if(getCaixaItemTipo().equals(CaixaItemTipo.DOCUMENTO)
                || getCaixaItemTipo().equals(CaixaItemTipo.TROCO)) {
            if(getParcela().getVenda().getVendaTipo() != null) {
                descricao += getParcela().getVenda().getVendaTipo();
            }
            if(getParcela() != null && getParcela().getVenda() != null) {
                descricao += " " + getParcela().getVenda().getId();
                
                if(getCaixaItemTipo().equals(CaixaItemTipo.DOCUMENTO)) {
                    if(getParcela().getNumero() != null) {
                        descricao += " PARCELA";
                    }
                    descricao += " " + getParcela().getNumeroDeTotal();
                }
                
                if(getParcela().getVenda().getPessoa() != null) {
                    descricao += " - " + getParcela().getVenda().getPessoa().getNome();
                }
            }
        } else if(getCaixaItemTipo().equals(CaixaItemTipo.CONTA_PROGRAMADA)) {
            descricao += " - " + getContaProgramadaBaixa().getContaProgramada().getNome();
        /*
        } else if(getCaixaItemTipo().equals(CaixaItemTipo.PAGAMENTO_DOCUMENTO)) {
            descricao += " - " + getContaProgramadaBaixa().getContaProgramada().getNome();
        */
        } else if(getCaixaItemTipo().equals(CaixaItemTipo.ESTORNO)) {
            if(getEstornoOrigem() != null) {
                descricao += " (ORIGEM ID " + getEstornoOrigem().getId() + ")";
            }
        }
        
        if(getEstorno() != null) {
            descricao += " (ESTORNADO ID " + getEstorno().getId() + ")";
        }
        
        return descricao;
    }
    
    
    @Override
    public boolean equals(Object obj){
        //Reference: https://www.sitepoint.com/implement-javas-equals-method-correctly/
        //Usei apenas o id, se depararmos com algo que exija uma comparação mais forte
        //comparar todos os campos
        return Objects.equals(this.getId(), ((CaixaItem) obj).getId());
    }
    
    
    public CaixaItem deepClone() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);

            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (CaixaItem) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println(e);
            return null;
        }
    }
}
