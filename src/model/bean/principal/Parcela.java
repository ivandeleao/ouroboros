/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.bean.principal;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import model.bean.fiscal.MeioDePagamento;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import util.DateTime;
import util.Decimal;

/**
 *
 * @author ivand
 */
@Entity
public class Parcela implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @CreationTimestamp
    private Timestamp criacao;
    @UpdateTimestamp
    private Timestamp atualizacao;

    @ManyToOne
    @JoinColumn(name = "vendaId")
    private Venda venda;

    @OrderBy
    private Integer numero;

    //estou mantendo vencimento nulo para parcelas de recebimento a vista
    //só coloco data quando é a prazo
    private Date vencimento;

    @Column(columnDefinition = "decimal(19,2) default 0", nullable = false)
    private BigDecimal valor;

    @Column(columnDefinition = "decimal(19,2) default 0", nullable = false)
    private BigDecimal multa; //valor percentual cobrado uma única vez quando vencido

    @Column(columnDefinition = "decimal(19,2) default 0", nullable = false)
    private BigDecimal jurosMonetario; //valor mensal - calculado como diário de mês comercial: 30 dias

    @Column(columnDefinition = "decimal(19,2) default 0", nullable = false)
    private BigDecimal jurosPercentual; //valor mensal - calculado como diário de mês comercial: 30 dias

    //não usados - rever
    //private BigDecimal acrescimo;
    //private BigDecimal desconto;
    //private BigDecimal descontoPercentual;
    //----------
    private BigDecimal acrescimoMonetario;
    private BigDecimal acrescimoPercentual;
    private BigDecimal descontoMonetario;
    private BigDecimal descontoPercentual;

    @ManyToOne
    @JoinColumn(name = "meioDePagamentoId")
    private MeioDePagamento meioDePagamento;

    @OneToMany(mappedBy = "parcela", cascade = CascadeType.ALL)//, fetch = FetchType.EAGER)
    private List<CaixaItem> recebimentos = new ArrayList<>();

    public Parcela() {
    }

    public Parcela(Date vencimento, BigDecimal valor, BigDecimal multa, BigDecimal jurosMonetario, BigDecimal jurosPercentual, MeioDePagamento meioDePagamento) {
        this.vencimento = vencimento;
        this.valor = valor;

        this.multa = multa;
        this.jurosMonetario = jurosMonetario;
        this.jurosPercentual = jurosPercentual;

        this.meioDePagamento = meioDePagamento;
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

    public Venda getVenda() {
        return venda;
    }

    public void setVenda(Venda venda) {
        this.venda = venda;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    public BigDecimal getValorAtual() {
        return getValor().add(
                getMultaCalculada())
                .add(getJurosCalculado())
                .setScale(2, RoundingMode.HALF_UP
                ).subtract(getRecebido())
                .add(getAcrescimoPercentualEmMonetario())
                .subtract(getDescontoPercentualEmMonetario());
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getMultaCalculada() {
        BigDecimal multaCalculada = BigDecimal.ZERO;
        if (getDiasEmAtraso().compareTo(0l) > 0) {
            multaCalculada = getValor().multiply(getMulta()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
        }
        return multaCalculada;
    }

    public BigDecimal getMulta() {
        return multa;
    }

    public void setMulta(BigDecimal multa) {
        this.multa = multa;
    }

    //--------------------------------------------------------------------------
    
    public BigDecimal getAcrescimoPercentualEmMonetario() {
        return getValor().multiply(getAcrescimoPercentual().divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));
    }
    
    public BigDecimal getDescontoPercentualEmMonetario() {
        return getValor().multiply(getDescontoPercentual().divide(new BigDecimal(100), 2, RoundingMode.HALF_UP));
    }
    
    /**
     *
     * @return representação do juros em monetário ou percentual
     */
    public String getJurosFormatado() {
        if (getJurosMonetario().compareTo(BigDecimal.ZERO) > 0) {
            return Decimal.toString(getJurosMonetario());
        } else {
            return Decimal.toString(getJurosPercentual()) + "%";
        }
    }

    /**
     *
     * @return o valor do juros utilizado: monetário ou percentual
     */
    public BigDecimal getJuros() {
        if (getJurosMonetario().compareTo(BigDecimal.ZERO) > 0) {
            return getJurosMonetario();
        } else {
            return getJurosPercentual();
        }
    }

    public Long getDiasEmAtraso() {
        Long dias = 0l;
        LocalDate hoje;
        /*Deveria ser getValorAtual ao invés de getValor, mas entra em recursividade infinita! :<
        pois getValorAtual usa este método getDiasEmAtraso para calcular
         */
        System.out.println("getRecebido: " + getRecebido());
        System.out.println("getValor: " + getValor());
        if (getRecebido().compareTo(getValor()) >= 0) { //se quitado
            //usar a data em que foi pago como limite de dias em atraso
            System.out.println("getRecebimentos(): " + getRecebimentos());
            System.out.println("getRecebimentos().get(0): " + getRecebimentos().get(0));
            System.out.println("getRecebimentos().get(0).getCriacao(): " + getRecebimentos().get(0).getCriacao());

            for (CaixaItem r : getRecebimentos()) {
                System.out.println("recebimento: " + r.getId());
            }

            hoje = getRecebimentos().get(0).getCriacao().toLocalDateTime().toLocalDate();
        } else {
            hoje = LocalDate.now();
        }

        if (getVencimento() != null && hoje.compareTo(getVencimento().toLocalDate()) > 0) {
            dias = Math.abs(DateTime.diasEntreDatas(hoje, getVencimento().toLocalDate()));
        }
        return dias;
    }

    public BigDecimal getJurosCalculado() {
        BigDecimal jurosAcumulado = BigDecimal.ZERO;
        BigDecimal jurosDiarioMonetario = BigDecimal.ZERO;

        if (getDiasEmAtraso().compareTo(0l) > 0) {
            if (getJurosMonetario().compareTo(BigDecimal.ZERO) > 0) { //monetário
                jurosDiarioMonetario = getJurosMonetario();
            } else { //percentual
                BigDecimal jurosMensal = getValor().multiply(getJurosPercentual()).divide(new BigDecimal(100), 10, RoundingMode.HALF_UP);
                //System.out.println("--jurosMensal: " + jurosMensal);
                jurosDiarioMonetario = jurosMensal.divide(new BigDecimal(30), 10, RoundingMode.HALF_UP); //mês comercial = 30 dias
                //System.out.println("--jurosDiarioMonetario: " + jurosDiarioMonetario);
            }
            jurosAcumulado = jurosDiarioMonetario.multiply(new BigDecimal(getDiasEmAtraso()));
        }

        return jurosAcumulado;
    }

    public BigDecimal getJurosMonetario() {
        return jurosMonetario;
    }

    public void setJurosMonetario(BigDecimal jurosMonetario) {
        this.jurosMonetario = jurosMonetario;
    }

    public BigDecimal getJurosPercentual() {
        return jurosPercentual;
    }

    public void setJurosPercentual(BigDecimal jurosPercentual) {
        this.jurosPercentual = jurosPercentual;
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

    public MeioDePagamento getMeioDePagamento() {
        return meioDePagamento;
    }

    public void setMeioDePagamento(MeioDePagamento meioDePagamento) {
        this.meioDePagamento = meioDePagamento;
    }

    public List<CaixaItem> getRecebimentos() {
        return recebimentos;
    }

    public void setRecebimentos(List<CaixaItem> recebimentos) {
        this.recebimentos = recebimentos;
    }

    //Métodos facilitadores ----------------------------------------------------
    public void addRecebimento(CaixaItem caixaItem) {
        recebimentos.remove(caixaItem);
        recebimentos.add(caixaItem);
        caixaItem.setParcela(this);
    }

    public void removeRecebimento(CaixaItem caixaItem) {
        caixaItem.setParcela(null);
        recebimentos.remove(caixaItem);
    }

    public String getNumeroDeTotal() {
        if (getNumero() == null) {
            return "--/--";
        } else {
            return String.format("%02d", getNumero()) + "/" + String.format("%02d", getVenda().getParcelas().size());
        }
    }

    /**
     *
     * @return soma dos recebimentos desta parcela
     */
    public BigDecimal getRecebido() {
        BigDecimal recebido = BigDecimal.ZERO;
        /*for (CaixaItem recebimento : recebimentos) {
            recebido = recebido.add(recebimento.getSaldoLinear());
        }*/
        if (!getRecebimentos().isEmpty()) {
            recebido = getRecebimentos().stream().map(CaixaItem::getSaldoLinear).reduce(BigDecimal::add).get();
        }
        return recebido;
    }

    /**
     *
     * @return se está quitado
     */
    public ParcelaStatus getStatus() {

        if (getRecebido().compareTo(getValorAtual()) < 0 && getDiasEmAtraso() > 0) {
            return ParcelaStatus.VENCIDO;

        } else if (getRecebido().compareTo(getValorAtual()) < 0 // O valor atual pode ficar menor que o recebido, quando tem recebimento parcial
                || getRecebido().compareTo(getValor()) < 0) {
            return ParcelaStatus.ABERTO;

        } else if (getRecebido().compareTo(getValorAtual()) >= 0 // O valor atual pode ficar menor que o recebido, quando tem recebimento parcial
                && getRecebido().compareTo(getValor()) >= 0) {
            return ParcelaStatus.QUITADO;
        }

        return null;
    }

    public Timestamp getUltimoRecebimento() {
        java.sql.Timestamp data = null;
        if (getStatus() == ParcelaStatus.QUITADO) {
            //data = getRecebimentos().get(0).getCriacao();
            data = getRecebimentos().get(getRecebimentos().size() - 1).getCriacao();
        }
        return data;
    }

    public BigDecimal getTroco() {
        BigDecimal troco = BigDecimal.ZERO;
        for (CaixaItem recebimento : recebimentos) {
            if (recebimento.getCaixaItemTipo().equals(CaixaItemTipo.TROCO_DE_VENDA)) {
                troco = troco.add(recebimento.getDebito());
            }
        }
        return troco;
    }

    public Pessoa getCliente() {
        return getVenda().getCliente();
    }

}
