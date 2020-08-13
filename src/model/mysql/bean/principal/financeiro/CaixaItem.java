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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    
    private LocalDateTime dataHora; //da conta - para os casos de operar a conta com data retroativa
    
    private LocalDateTime dataHoraRecebimento; //para os casos de recebimento feito em data retroativa e lançado posteriormente

    @ManyToOne
    @JoinColumn(name = "caixaId", nullable = true) //2019-12-02 adicionado nullable pois CaixaItem pode pertencer a um caixa ou a uma conta
    private Caixa caixa;
    
    @ManyToOne
    @JoinColumn(name = "contaId", nullable = true) //2019-12-02
    private Conta conta;

    @ManyToOne
    @JoinColumn(name = "parcelaId", nullable = true)
    private Parcela parcela;

    @OneToOne
    @JoinColumn(name = "contaProgramadaBaixaId", nullable = true)
    private ContaProgramadaBaixa contaProgramadaBaixa;

    @ManyToOne
    @JoinColumn(name = "caixaItemTipoId")
    private CaixaItemTipo caixaItemTipo;

    @ManyToOne
    @JoinColumn(name = "meioDePagamentoId")
    private MeioDePagamento meioDePagamento;

    @Column(nullable = true)
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
    
    //------------------- relacionamento circular
    @OneToOne(mappedBy = "tranferenciaOrigem")
    private CaixaItem tranferencia;

    @OneToOne
    @JoinColumn(name = "tranferenciaOrigemId")
    private CaixaItem tranferenciaOrigem;
    //-------------------
    
    @ManyToOne
    @JoinColumn(name = "chequeId", nullable = true)
    private Cheque cheque;
    
    public CaixaItem() {
    }

    /**
     * Não usar este por conta do parâmetro caixa. O correto é usar o addCaixa ou addConta na entidade Caixa
     * @param caixa
     * @param caixaItemTipo
     * @param meioDePagamento
     * @param observacao
     * @param credito
     * @param debito 
     */
    public CaixaItem(Caixa caixa, CaixaItemTipo caixaItemTipo, MeioDePagamento meioDePagamento, String observacao, BigDecimal credito, BigDecimal debito) {
        this.caixa = caixa;
        this.caixaItemTipo = caixaItemTipo;
        this.meioDePagamento = meioDePagamento;
        this.observacao = observacao;
        this.credito = credito;
        this.debito = debito;
        
        this.saldoAcumulado = BigDecimal.ZERO;
        this.dataHora = LocalDateTime.now();
    }
    
    public CaixaItem(CaixaItemTipo caixaItemTipo, MeioDePagamento meioDePagamento, String observacao, BigDecimal credito, BigDecimal debito) {
        this.caixaItemTipo = caixaItemTipo;
        this.meioDePagamento = meioDePagamento;
        this.observacao = observacao;
        this.credito = credito;
        this.debito = debito;
        
        this.saldoAcumulado = BigDecimal.ZERO;
        this.dataHora = LocalDateTime.now();
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

    public LocalDateTime getDataHoraRecebimento() {
        return dataHoraRecebimento;
    }

    public void setDataHoraRecebimento(LocalDateTime dataHoraRecebimento) {
        this.dataHoraRecebimento = dataHoraRecebimento;
    }

    public LocalDateTime getDataHora() {
        return dataHora != null ? dataHora : getCriacao();
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public Caixa getCaixa() {
        return caixa;
    }

    public void setCaixa(Caixa caixa) {
        this.caixa = caixa;
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
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
    

    public CaixaItem getTranferencia() {
        return tranferencia;
    }

    public void setTranferencia(CaixaItem tranferencia) {
        this.tranferencia = tranferencia;
    }

    public CaixaItem getTranferenciaOrigem() {
        return tranferenciaOrigem;
    }

    public void setTranferenciaOrigem(CaixaItem tranferenciaOrigem) {
        this.tranferenciaOrigem = tranferenciaOrigem;
    }

    public Cheque getCheque() {
        return cheque;
    }

    public void setCheque(Cheque cheque) {
        this.cheque = cheque;
    }
    
    

    //Métodos Facilitadores ----------------------------------------------------
    /**
     * 
     * @return com horário zerado se a data de recebimento foi alterada
     */
    /*public LocalDateTime getDataHoraRecebimento() {
        //return getDataRecebimento() != null ? getDataRecebimento().atTime(LocalTime.MIN) : getDataHora();
        //2020-07-09
        return !getDataRecebimento().equals(getDataHora().toLocalDate()) ? getDataRecebimento().atTime(LocalTime.MIN) : getDataHora();
    }*/
    
    /**
     * 
     * @return getCredito().subtract(getDebito());
     */
    public BigDecimal getSaldoLinear() {
        return getCredito().subtract(getDebito());
    }

    /**
     * Método conveniente para trazer mais detalhes
     *
     * @return tipo, id da venda, etc...
     */
    public String getDescricao() {
        try {
            String descricao = "";

            if (!getCaixaItemTipo().equals(CaixaItemTipo.DOCUMENTO)) {
                descricao = getCaixaItemTipo().getNome() + " ";
            }

            if (getCaixaItemTipo().equals(CaixaItemTipo.DOCUMENTO)
                    || getCaixaItemTipo().equals(CaixaItemTipo.TROCO)) {
                if (getParcela().getVenda().getVendaTipo() != null) {
                    descricao += getParcela().getVenda().getVendaTipo();
                }
                if (getParcela() != null && getParcela().getVenda() != null) {
                    descricao += " " + getParcela().getVenda().getId();

                    if (getCaixaItemTipo().equals(CaixaItemTipo.DOCUMENTO)) {
                        if (getParcela().getNumero() != null) {
                            descricao += " PARCELA";
                        }
                        descricao += " " + getParcela().getNumeroDeTotal();
                    }

                    if (getParcela().getVenda().getPessoa() != null) {
                        descricao += " - " + getParcela().getVenda().getPessoa().getNome();
                    }
                }
            } else if (getCaixaItemTipo().equals(CaixaItemTipo.CONTA_PROGRAMADA)) {
                descricao += " - " + getContaProgramadaBaixa().getContaProgramada().getNome();
                
            } else if (getCaixaItemTipo().equals(CaixaItemTipo.ESTORNO)) {
                if (getEstornoOrigem() != null) {
                    descricao += " (ORIGEM ID " + getEstornoOrigem().getId() + ")";
                }
            } else if (getCaixaItemTipo().equals(CaixaItemTipo.TRANSFERENCIA)) {
                if (getTranferenciaOrigem() != null) {
                    descricao += "(ORIGEM " + getTranferenciaOrigem().getContaCaixa() + " ID " + getTranferenciaOrigem().getId() + ")";
                    
                } else if (getTranferencia() != null) {
                    descricao += "(DESTINO " + getTranferencia().getContaCaixa() + " ID " + getTranferencia().getId() + ")";
                    
                }
            }

            if (getEstorno() != null) {
                descricao += " (ESTORNADO ID " + getEstorno().getId() + ")";
            }

            return descricao;
        } catch (Exception e) {
            System.err.println("Erro ao obter descrição do CaixaItem " + e);
            return "Erro ao obter descrição do CaixaItem " + e;
        }
    }

    @Override
    public boolean equals(Object obj) {
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
    
    
    //--------------------------------------------------------------------------
    /**
     * 
     * @return nome da Conta / Caixa de origem
     */
    public String getContaCaixa() {
        if(getConta() != null) {
            return getConta().getNome();
            
        } else {
            return getCaixa().getConta().getNome();
        }
    }
    
    
    
    
    
}
