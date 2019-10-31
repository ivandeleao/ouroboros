/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.mysql.bean.principal.documento;

import model.mysql.bean.principal.documento.Venda;
import model.mysql.bean.principal.pessoa.Pessoa;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
import model.mysql.bean.fiscal.MeioDePagamento;
import model.mysql.bean.principal.financeiro.CaixaItem;
import model.mysql.bean.principal.financeiro.CaixaItemTipo;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import util.DateTime;
import util.Decimal;

/**
 *
 * @author ivand
 */
@Entity
public class Parcela implements Serializable, Comparable<Parcela> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @CreationTimestamp
    private LocalDateTime criacao;
    @UpdateTimestamp
    private LocalDateTime atualizacao;

    @ManyToOne
    @JoinColumn(name = "vendaId")
    private Venda venda;

    @OrderBy
    private Integer numero;

    //estou mantendo vencimento nulo para parcelas de recebimento a vista
    //só coloco data quando é a prazo
    private LocalDate vencimento;

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

    public Parcela(LocalDate vencimento, BigDecimal valor, BigDecimal multa, BigDecimal jurosMonetario, BigDecimal jurosPercentual, MeioDePagamento meioDePagamento) {
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

    public LocalDate getVencimento() {
        return vencimento;
    }

    public void setVencimento(LocalDate vencimento) {
        this.vencimento = vencimento;
    }

    public BigDecimal getValorAtual() {
        /*System.out.println("getValor(): " + getValor());
        System.out.println("+ getMultaCalculada(): " + getMultaCalculada());
        System.out.println("+ getJurosCalculado(): " + getJurosCalculado());
        System.out.println("- getValorQuitado(): " + getValorQuitado());
        System.out.println("+ getAcrescimoPercentualEmMonetario(): " + getAcrescimoPercentualEmMonetario());
        System.out.println("- getDescontoPercentualEmMonetario(): " + getDescontoPercentualEmMonetario());
*/
        BigDecimal valor = getValor()
                .add(getMultaCalculada())
                .add(getJurosCalculado())
                .setScale(2, RoundingMode.HALF_UP)
                .subtract(getValorQuitado())
                .add(getAcrescimoPercentualEmMonetario())
                .subtract(getDescontoPercentualEmMonetario());
        
        return valor.setScale(2, RoundingMode.HALF_UP); //2019-10-07 arredondar para comparar corretamente com o valor da parcela
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
        return getValor().multiply(getAcrescimoPercentual()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
    }
    
    public BigDecimal getDescontoPercentualEmMonetario() {
        return getValor().multiply(getDescontoPercentual()).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP).setScale(2, RoundingMode.HALF_UP);
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
        if (getValorQuitado().compareTo(getValor()) >= 0 && !getRecebimentos().isEmpty()) { //se quitado
            //usar a data em que foi pago como limite de dias em atraso
            //System.out.println("getRecebimentos(): " + getRecebimentos());
            //System.out.println("getRecebimentos().get(0): " + getRecebimentos().get(0));
            //System.out.println("getRecebimentos().get(0).getCriacao(): " + getRecebimentos().get(0).getCriacao());

            /*
            for (CaixaItem r : getRecebimentos()) {
                System.out.println("recebimento: " + r.getId());
            }*/

            hoje = getRecebimentos().get(0).getCriacao().toLocalDate();
        } else {
            hoje = LocalDate.now();
        }

        if (getVencimento() != null && hoje.compareTo(getVencimento()) > 0) {
            dias = Math.abs(DateTime.diasEntreDatas(hoje, getVencimento()));
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
    
    public String getDescricao() {
        String descricao = getVenda().getPessoa().getNome();
        
        descricao += " - Venda " + getVenda().getId();
        
        descricao += " - Parcela " + getNumeroDeTotal();
        
        return descricao;
    }
    
    
    public void addRecebimento(CaixaItem caixaItem) {
        recebimentos.remove(caixaItem);
        recebimentos.add(caixaItem);
        caixaItem.setParcela(this);
    }

    public void removeRecebimento(CaixaItem caixaItem) {
        caixaItem.setParcela(null);
        recebimentos.remove(caixaItem);
    }

    /**
     * 
     * @return Número da Parcela e Quantidade: 9/99
     */
    public String getNumeroDeTotal() {
        if (getNumero() == null) {
            return "À VISTA";
        } else {
            return String.format("%02d", getNumero()) + "/" + String.format("%02d", getVenda().getParcelas().size());
        }
    }
    
    /**
     * 
     * @return numero formatado com 2 dígitos
     */
    public String getNumeroFormatado() {
        if (getNumero() == null) {
            return "À VISTA";
        } else {
            return String.format("%02d", getNumero());
        }
    }

    /**
     *
     * @return soma dos valores pagos/recebidos desta parcela
     */
    public BigDecimal getValorQuitado() {
        BigDecimal valorQuitado = BigDecimal.ZERO;
        /*for (CaixaItem recebimento : recebimentos) {
            valorQuitado = valorQuitado.add(recebimento.getSaldoLinear());
        }*/
        if (!getRecebimentos().isEmpty()) {
            valorQuitado = getRecebimentos().stream().map(CaixaItem::getSaldoLinear).reduce(BigDecimal::add).get().abs();
        }
        return valorQuitado;
    }

    /**
     *
     * @return se está quitado
     */
    public FinanceiroStatus getStatus() {

        if (getValorQuitado().compareTo(getValorAtual()) < 0 && getDiasEmAtraso() > 0) {
            return FinanceiroStatus.VENCIDO;

        /*} else if (getValorQuitado().compareTo(getValorAtual()) < 0 // O valor atual pode ficar menor que o valorQuitado, quando tem recebimento parcial
                || getValorQuitado().compareTo(getValor()) < 0) {
            return FinanceiroStatus.ABERTO;*/
        } else if (getValorAtual().compareTo(BigDecimal.ZERO) > 0) {
            return FinanceiroStatus.ABERTO;
            
        } else if (getValorQuitado().compareTo(getValorAtual()) >= 0 // O valor atual pode ficar menor que o valorQuitado, quando tem recebimento parcial
                && getValorQuitado().compareTo(getValor()) >= 0) {
            return FinanceiroStatus.QUITADO;
        } else {
            return FinanceiroStatus.QUITADO;
        }

        //return null;
    }

    public LocalDateTime getUltimoRecebimento() {
        if (getStatus() == FinanceiroStatus.QUITADO) {
            return getRecebimentos().get(getRecebimentos().size() - 1).getCriacao();
        }
        return null;
    }

    public BigDecimal getTroco() {
        BigDecimal troco = BigDecimal.ZERO;
        for (CaixaItem recebimento : recebimentos) {
            if (recebimento.getCaixaItemTipo().equals(CaixaItemTipo.TROCO)) {
                troco = troco.add(recebimento.getDebito());
            }
        }
        return troco;
    }

    public Pessoa getCliente() {
        return getVenda().getPessoa();
    }
    
    

    @Override
    public int compareTo(Parcela o) {
        return id.compareTo(o.getId());
    }
    

}
